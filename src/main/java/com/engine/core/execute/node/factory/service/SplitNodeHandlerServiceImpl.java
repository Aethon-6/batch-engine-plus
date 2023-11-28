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
public class SplitNodeHandlerServiceImpl extends NodeHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Resource
    private IRedisService redisService;

    public AssemblyRVO assemblySql(NodeVO node, List<LabelInfoVO> labelList, List<NodeRelationVO> relations, String table) {
        LOGGER.info("[拆分节点SQL组装开始]{}", node.getName());
        List<NodeRelationVO> list = relations.stream().filter(r -> node.getNodeId().equals(r.getDestNode())).collect(Collectors.toList());

        StringBuilder select = new StringBuilder("SELECT '" + node.getCode() + "' AS node_code ,customer_id FROM " + table);
        StringBuilder where = new StringBuilder(" WHERE use_flg = 0 AND node_code IN (");
        for (NodeRelationVO r : list) {
            NodeVO preNode = redisService.getNodeFromRedis(r.getSourceNode());
            where.append("'").append(preNode.getCode()).append("'").append(",");
        }

        if (where.length() > 0) {
            where.deleteCharAt(where.length() - 1);
        }

        where.append(")");
        select.append(where);
        LOGGER.info("[拆分节点SQL组装结束]{}", node.getName());
        return AssemblyRVO.builder().sql(select.toString()).where(where.toString()).build();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        NodeFactory.register(NodeTypeEnum.SPLIT, this);
    }
}
