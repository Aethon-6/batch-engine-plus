package com.engine.core.execute.node.factory.service;

import com.engine.core.execute.node.factory.NodeFactory;
import com.engine.core.execute.node.factory.NodeHandler;
import com.engine.entity.vo.*;
import com.engine.enums.NodeTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ChooseNodeHandlerServiceImpl extends NodeHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public AssemblyRVO assemblySql(NodeVO node, List<LabelInfoVO> labelList, List<NodeRelationVO> relations, String table) {
        LOGGER.info("[选择节点SQL组装开始]{}", node.getName());

        List<LabelVO> nodeinfoList = node.getNodeInfo();

        assert nodeinfoList != null;

        StringBuilder select = new StringBuilder(" SELECT '" + node.getCode() + "' as node_code ,i.id FROM engine_customer_info i ");
        StringBuilder where = new StringBuilder(" where ");
        int index = 0;
        ArrayList<LabelInfoVO> cList = new ArrayList<>();
        for (LabelVO nodeInfo : nodeinfoList) {
            List<LabelInfoVO> lList = labelList.stream().filter(l -> nodeInfo.getKey().toUpperCase().equals(l.getFieldNo().toUpperCase())).collect(Collectors.toList());
            if (lList.size() == 0) continue;

            LabelInfoVO label = lList.get(0);
            cList.add(label);
            String logicOperator = "";
            if (index > 0) {
                logicOperator = this.handlerConditionLogic(nodeInfo.getLogicOper());
            }

            String lLogicOper = "";
            String rLogicOper = "";
            if (StringUtils.isNotBlank(nodeInfo.getLLogicOper()) && StringUtils.isNotBlank(nodeInfo.getRLogicOper())) {
                lLogicOper = nodeInfo.getLLogicOper();
                rLogicOper = nodeInfo.getRLogicOper();
            }
            where.append(" ").append(logicOperator).append(lLogicOper).append(" (");
            List<HashMap<String, Object>> dataMapList = this.handlerConditionData(nodeInfo);

            for (int i = 0; i < dataMapList.size(); i++) {
                HashMap<String, Object> hashMap = dataMapList.get(i);
                if (i > 0) {
                    where.append(" or ");
                }
                where.append(label.getLabelBAlias()).append(".").append(label.getFieldNo()).append(" ");
                switch (nodeInfo.getOper()) {
                    case "in":
                        where.append("in (").append(hashMap.get("low")).append(")");
                        break;
                    case "between":
                        where.append("between").append(" ").append(hashMap.get("low")).append(" and ").append(hashMap.get("high"));
                        break;
                    default:
                        where.append(nodeInfo.getOper()).append(hashMap.get("low"));
                        break;
                }
            }

            where.append(") ").append(rLogicOper);
            index++;
        }
        Map<String, List<LabelInfoVO>> cGroup = cList.stream().collect(Collectors.groupingBy(c -> c.getLabelBType() + "-" + c.getLabelBAlias()));
        for (Map.Entry<String, List<LabelInfoVO>> entry : cGroup.entrySet()) {
            String[] keys = entry.getKey().split("-");
            if ("engine_customer_info".equals(keys[0].toLowerCase())) {
                continue;
            }
            select.append(" inner join ").append(keys[0]).append(" ").append(keys[1]).append(" on ").append("i.id = ").append(keys[1]).append(".khwybh");
        }

        select.append(where);
        LOGGER.info("[选择节点SQL组装结束]{}", node.getName());
        return AssemblyRVO.builder().sql(select.toString()).build();
    }

    /**
     * 处理条件值
     *
     * @param nodeInfo 要处理的条件
     * @return
     */
    private List<HashMap<String, Object>> handlerConditionData(LabelVO nodeInfo) {
        List<HashMap<String, Object>> rList = new ArrayList<>();

        for (LabelValueVO labelValue : nodeInfo.getItemData()) {
            HashMap<String, Object> hashMap = new HashMap<>();
            String lowData = StringUtils.isBlank(labelValue.getInputValueLow()) ? labelValue.getInputValue() : labelValue.getInputValueLow();
            String highData = StringUtils.isBlank(labelValue.getInputValueHigh()) ? "" : labelValue.getInputValueHigh();
            switch (nodeInfo.getVersion()) {
                case "char":
                case "character":
                case "varchar":
                    if ("like".equals(nodeInfo.getOper().toLowerCase())) {
                        lowData = "%" + lowData + "%";
                        highData = "%" + highData + "%";
                    }
                    hashMap.put("low", "'" + lowData + "'");
                    hashMap.put("high", "'" + highData + "'");
                    break;
                case "date":
                case "time":
                    hashMap = this.handlerConditionDateData(nodeInfo, labelValue);
                    break;
                case "int":
                case "integer":
                case "decimal":
                    hashMap.put("low", lowData);
                    hashMap.put("high", highData);
                    break;
                default:
                    break;
            }

            rList.add(hashMap);
        }
        return rList;
    }

    /**
     * 处理条件的日期值
     *
     * @param nodeInfo   要处理的条件
     * @param labelValue 要处理的日期值
     * @return
     */
    private HashMap<String, Object> handlerConditionDateData(LabelVO nodeInfo, LabelValueVO labelValue) {
        HashMap<String, Object> hashMap = new HashMap<>();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        switch (nodeInfo.getDateType()) {
            case "1":
                hashMap.put("low", "'" + (StringUtils.isBlank(labelValue.getInputValueLow()) ? labelValue.getInputValue() : labelValue.getInputValueLow()) + "'");
                hashMap.put("high", "'" + (StringUtils.isBlank(labelValue.getInputValueHigh()) ? "" : labelValue.getInputValueHigh()) + "'");
                break;
            case "2":
                int dayLow = Integer.parseInt(StringUtils.isBlank(labelValue.getInputValueLow()) ? labelValue.getInputValue() : labelValue.getInputValueLow());
                int dayHigh = Integer.parseInt(StringUtils.isBlank(labelValue.getInputValueHigh()) ? "" : labelValue.getInputValueHigh());
                Calendar calendar = Calendar.getInstance();

                Date nowDate = new Date();
                calendar.setTime(nowDate);
                calendar.add(Calendar.DAY_OF_YEAR, -dayLow);
                hashMap.put("low", "'" + sd.format(calendar.getTime()) + "'");

                calendar.setTime(nowDate);
                calendar.add(Calendar.DAY_OF_YEAR, -dayHigh);
                hashMap.put("high", "'" + sd.format(calendar.getTime()) + "'");
                break;
        }
        return hashMap;
    }

    private String handlerConditionLogic(String logic) {
        switch (logic) {
            case "|":
                return "or";
            case "&":
                return "and";
            case "!&":
                return "not and";
            case "!|":
                return "not or";
            default:
                return "";
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        NodeFactory.register(NodeTypeEnum.CHOOSE, this);
    }
}
