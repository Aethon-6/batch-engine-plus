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
import java.util.stream.Collectors;

@Component
public class OrNodeHandlerServiceImpl extends NodeHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Resource
    private IRedisService redisService;

    public AssemblyRVO assemblySql(NodeVO node, List<LabelInfoVO> labelList, List<NodeRelationVO> relations, String table) {
        LOGGER.info("[或合并节点SQL组装开始]{}", node.getName());
        List<NodeRelationVO> list = relations.stream().filter(r -> node.getNodeId().equals(r.getDestNode())).collect(Collectors.toList());

        StringBuilder select = new StringBuilder("SELECT '" + node.getCode() + "' AS node_code ,customer_id FROM " + table);
        StringBuilder where = new StringBuilder(" WHERE use_flg = 0 ");

        for (int i = 0; i < list.size(); i++) {
            NodeRelationVO r = list.get(i);
            NodeVO preNode = redisService.getNodeFromRedis(r.getSourceNode());
            if (i == 0) {
                where.append(" and (");
            }
            if (i == list.size() - 1) {
                where.append(" node_code = '").append(preNode.getCode()).append("')");
            } else {
                where.append(" node_code = '").append(preNode.getCode()).append("' or");
            }
        }
        select.append(where);
        LOGGER.info("[或合并节点SQL组装结束]{}", node.getName());
        return AssemblyRVO.builder().sql(select.toString()).where(where.toString()).build();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        NodeFactory.register(NodeTypeEnum.OR, this);
    }
}
