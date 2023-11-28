package com.engine.core.execute.common.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.engine.active.entity.EngineCustomerInfo;
import com.engine.active.entity.EngineCustomerRklk;
import com.engine.active.entity.EngineLabelConfig;
import com.engine.active.service.IEngineCustomerInfoService;
import com.engine.active.service.IEngineCustomerRklkService;
import com.engine.active.service.IEngineLabelConfigService;
import com.engine.aop.annotation.DS;
import com.engine.core.execute.common.service.ICommonService;
import com.engine.entity.vo.*;
import com.engine.enums.DSTypeEnum;
import com.engine.sql.service.ISqlService;
import com.engine.utils.FileUtil;
import com.engine.utils.SnowflakeIdWorker;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CommonServiceImpl implements ICommonService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final Pattern PATTERN = Pattern.compile("\\$\\{(.*?)\\}");
    @Resource
    private IEngineLabelConfigService engineLabelConfigService;

    @Resource
    private ISqlService sqlService;

    @Resource
    private IEngineCustomerInfoService engineCustomerInfoService;

    @Resource
    private IEngineCustomerRklkService engineCustomerRklkService;

    @Override
    public List<EngineLabelConfig> extractLabel(List<NodeVO> nodes, List<NodeRelationVO> relations) {
        ArrayList<String> wheres = new ArrayList<>();
        for (NodeVO node : nodes) {
            //choose 算子标签提取
            if ("choose".equals(node.getNodeType())) {
                for (LabelVO label : node.getNodeInfo()) {
                    String key = label.getKey().toUpperCase();
                    if (wheres.contains(key)) {
                        continue;
                    }
                    wheres.add(key);
                }
            }

            //communicate 算子标签提取
            CommunicateInfoVO communicateInfo = node.getCommunicateInfo();
            if ("communicate".equals(node.getNodeType()) && communicateInfo != null) {
                String content = communicateInfo.getContent();

                Matcher matcher = PATTERN.matcher(content);
                while (matcher.find()) {
                    String key = matcher.group(1).toUpperCase();
                    if (wheres.contains(key)) {
                        continue;
                    }
                    wheres.add(key);
                }
            }
        }
        QueryWrapper<EngineLabelConfig> wrapper = new QueryWrapper<>();
        wrapper.in("field_no", wheres);

        return engineLabelConfigService.list(wrapper);
    }

    @Override
    @DS(DSTypeEnum.SLAVE)
    public int generateTempData(AssemblyRVO assembly, String tempTable) {
        String insertSql = "INSERT into " + tempTable + " (node_code,customer_id) " + assembly.getSql();
        int count = sqlService.insert(insertSql);

        if (StringUtils.isNotBlank(assembly.getWhere())) {
            String updateSql = "UPDATE " + tempTable + " set use_flg = '1' " + assembly.getWhere();
            sqlService.update(updateSql);
        }

        return count;
    }

    @Override
    public boolean jsonValidator(String json) {
        String activeStr = null;
        try {
            activeStr = FileUtil.readFileContent("active.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ActiveVO active = JSON.parseObject(activeStr, ActiveVO.class);
        if (active.getNodes() == null) {
            LOGGER.error("活动设计不规范，未包含节点！");
            return false;
        }
        if (active.getNodes().size() > 1 && active.getNodeRelations().size() <= 0) {
            LOGGER.error("活动设计不规范，未包含节点关联信息！");
            return false;
        }
        if (active.getNodes().stream().noneMatch(n -> "begin".equals(n.getProperty()))) {
            LOGGER.error("活动设计不规范，未包含选择节点！");
            return false;
        }
        if (active.getNodes().stream().noneMatch(n -> "communicate".equals(n.getNodeType()))) {
            LOGGER.error("活动设计不规范，未包含沟通节点！");
            return false;
        }

        int count = 0;
        for (NodeVO node : active.getNodes().stream().filter(n -> "begin".equals(n.getProperty())).collect(Collectors.toList())) {
            String nodeId = this.communicateReachAnalyse(active.getNodeRelations(), node.getNodeId());
            if (active.getNodes().stream().noneMatch(n -> "communicate".equals(n.getNodeType()) && nodeId.equals(n.getNodeId()))) {
                count++;
            }
        }
        if (count < active.getNodes().stream().filter(n -> "communicate".equals(n.getNodeType())).count()) {
            LOGGER.error("活动设计不规范，不能触达全部沟通节点！");
        }

        for (NodeVO node : active.getNodes()) {
            switch (node.getNodeType()) {
                case "choose":
                    if (node.getNodeInfo().size() > 0) {
                        for (LabelVO label : node.getNodeInfo()) {
                            if (StringUtils.isBlank(label.getVersion()) || StringUtils.isBlank(label.getOper()) || StringUtils.isBlank(label.getKey())) {
                                LOGGER.error("[选择节点]【{}】活动设计不规范，缺少标签信息！", node.getName());
                                return false;
                            }
                            if (label.getItemData() == null || label.getItemData().size() <= 0) {
                                LOGGER.error("[选择节点]【{}】活动设计不规范，缺少标签值！", node.getName());
                                return false;
                            }
                            for (LabelValueVO item : label.getItemData()) {
                                if (StringUtils.isBlank(item.getInputValue()) && StringUtils.isBlank(item.getInputValueLow()) && StringUtils.isBlank(item.getInputValueHigh())) {
                                    LOGGER.error("[选择节点]【{}】活动设计不规范，缺少标签值！", node.getName());
                                    return false;
                                }
                            }
                        }
                    }
                    break;
                case "unit":
                    break;
                case "split":
                    if (StringUtils.isBlank(node.getSplitRule())) {
                        LOGGER.error("[拆分节点]【{}】活动设计不规范，缺少拆分规则！", node.getName());
                        return false;
                    }
                    break;
                case "and":
                    break;
                case "or":
                    break;
                case "communicate":
                    if (StringUtils.isBlank(node.getSubType())) {
                        LOGGER.error("[拆分节点]【{}】活动设计不规范，缺少沟通类型！", node.getName());
                        return false;
                    }
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    private String communicateReachAnalyse(List<NodeRelationVO> relations, String nodeId) {
        if (relations.stream().noneMatch(r -> nodeId.equals(r.getSourceNode()))) {
            return nodeId;
        }
        for (NodeRelationVO r : relations.stream().filter(r -> nodeId.equals(r.getSourceNode())).collect(Collectors.toList())) {
            this.communicateReachAnalyse(relations, r.getDestNode());
        }
        return "";
    }

    @Override
    @DS(DSTypeEnum.SLAVE)
    public void channelIssued(String innSql) {
        sqlService.insert(innSql);
    }

    @Override
    @DS(DSTypeEnum.SLAVE)
    public void generatorData() {
        String sTime = "1999-01-01 08:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime sLocalDateTime = LocalDateTime.parse(sTime, formatter);

        Random random = new Random();

        String[] xbs = {"N", "M", "F"};
        String[] names = {"A", "B", "C", "E", "F", "J", "H", "I", "O", "P", "L", "M"};

        for (int i = 5721; i < 200000; i++) {
            EngineCustomerInfo customerInfo = EngineCustomerInfo.builder()
                    .name(names[random.nextInt(12)] + "_" + (i + 1))
                    .age(random.nextInt(90))
                    .build();
            engineCustomerInfoService.save(customerInfo);

            EngineCustomerRklk customerRklk = EngineCustomerRklk.builder()
                    .khwybh(customerInfo.getId())
                    .khnl(customerInfo.getAge())
                    .xb(xbs[random.nextInt(2)])
                    .khrq(sLocalDateTime.plusDays(random.nextInt(365)))
                    .build();
            engineCustomerRklkService.save(customerRklk);
        }
    }
}
