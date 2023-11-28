package com.engine.core.execute.node.factory.service;

import com.engine.core.execute.node.factory.NodeFactory;
import com.engine.core.execute.node.factory.NodeHandler;
import com.engine.core.execute.redis.service.IRedisService;
import com.engine.entity.vo.AssemblyRVO;
import com.engine.entity.vo.LabelInfoVO;
import com.engine.entity.vo.NodeRelationVO;
import com.engine.entity.vo.NodeVO;
import com.engine.enums.NodeTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class UnitNodeHandlerServiceImpl extends NodeHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Resource
    private IRedisService redisService;

    public AssemblyRVO assemblySql(NodeVO node, List<LabelInfoVO> labelList, List<NodeRelationVO> relations, String table) {
        LOGGER.info("[与合并节点SQL组装开始]{}", node.getName());
        List<NodeRelationVO> list = relations.stream().filter(r -> node.getNodeId().equals(r.getDestNode())).collect(Collectors.toList());
        NodeVO preNode = redisService.getNodeFromRedis(list.get(0).getSourceNode());
        StringBuilder select = new StringBuilder();
        StringBuilder where = new StringBuilder();

        if ("split".equals(preNode.getNodeType().toLowerCase())) {
            String splitPercent = preNode.getNodeInfo().stream().filter(n -> node.getNodeId().equals(n.getNextNodeId())).collect(Collectors.toList()).get(0).getSplitPercent();
            switch (preNode.getSplitRule()) {
                case "rate":
                    /*where.append(" where use_flg = 0 and customer_id = '" + preNode.getCode() + "' order by customer_id limit (round((select count(0) from " + table + " where use_flg = 0 and customer_id = '" + preNode.getCode() + "')*" + (Double.parseDouble(splitPercent) / 100) + ")) offset 0");*/
                    select.append("SELECT '").append(node.getCode()).append("' AS node_code, C.customer_id FROM (SELECT A.customer_id,  @row_num:=@row_num+1 AS ROW_NUM FROM ").append(table).append(" A , (SELECT @row_num:=0) B WHERE A.use_flg = 0 AND A.node_code = '").append(preNode.getCode()).append("') C WHERE C.ROW_NUM<=(@row_num*").append(Double.parseDouble(splitPercent) / 100).append(")");

                    where.append("where node_code = '").append(preNode.getCode()).append("' and customer_id in (SELECT customer_id from (SELECT C.customer_id FROM (SELECT A.node_code ,A.customer_id,  @row_num:=@row_num+1 AS ROW_NUM FROM ").append(table).append(" A , (SELECT @row_num:=0) B WHERE A.use_flg = 0 AND A.node_code = '").append(preNode.getCode()).append("') C WHERE C.ROW_NUM<=(@row_num*").append(Double.parseDouble(splitPercent) / 100).append(")) t)");
                    break;
                case "random":
                    Random random = new Random();
                    select.append("SELECT '").append(node.getCode()).append("' AS node_code, customer_id FROM ").append(table).append(" WHERE use_flg = 0 AND node_code = '").append(preNode.getCode()).append("' ORDER BY customer_id LIMIT ").append(random.nextInt(50000));

                    where.append("where node_code = '").append(preNode.getCode()).append("' and customer_id in (SELECT customer_id from (SELECT customer_id FROM ").append(table).append(" WHERE use_flg = 0 AND node_code = '").append(preNode.getCode()).append("' ORDER BY customer_id LIMIT ").append(random.nextInt(50000)).append(") t)");
                    break;
                case "count":
                    select.append("SELECT '").append(node.getCode()).append("' AS node_code, customer_id FROM ").append(table).append(" WHERE use_flg = 0 AND node_code = '").append(preNode.getCode()).append("' ORDER BY customer_id LIMIT ").append(splitPercent);

                    where.append("where node_code = '").append(preNode.getCode()).append("' customer_id in (SELECT customer_id from (SELECT customer_id FROM ").append(table).append(" WHERE use_flg = 0 AND node_code = '").append(preNode.getCode()).append("' ORDER BY customer_id LIMIT ").append(splitPercent).append(") t)");
                    break;
                default:
                    break;
            }
        } else {
            select.append("SELECT '").append(node.getCode()).append("' AS node_code, customer_id FROM ").append(table).append(" WHERE use_flg = 0 AND node_code = '").append(preNode.getCode()).append("' ORDER BY customer_id");

            where.append("where node_code = '").append(preNode.getCode()).append("' and customer_id in (SELECT customer_id from (SELECT customer_id FROM ").append(table).append(" WHERE use_flg = 0 AND node_code = '").append(preNode.getCode()).append("' ORDER BY customer_id) t)");
        }
        LOGGER.info("[与合并节点SQL组装结束]{}", node.getName());
        return AssemblyRVO.builder().sql(select.toString()).where(where.toString()).build();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        NodeFactory.register(NodeTypeEnum.UNIT, this);
    }
}
