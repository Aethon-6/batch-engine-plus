package com.engine.core.execute.node.service.impl;

import com.engine.core.execute.channel.factory.ChannelFactory;
import com.engine.core.execute.channel.factory.ChannelHandler;
import com.engine.core.execute.common.service.ICommonService;
import com.engine.core.execute.node.factory.NodeFactory;
import com.engine.core.execute.node.factory.NodeHandler;
import com.engine.core.execute.node.service.INodeService;
import com.engine.core.execute.redis.service.IRedisService;
import com.engine.entity.vo.AssemblyRVO;
import com.engine.entity.vo.ExecuteRVO;
import com.engine.entity.vo.NodeVO;
import com.engine.entity.vo.RelevanceInfoVO;
import com.engine.enums.NodeTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;

@Service
public class NodeServiceImpl implements INodeService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Resource
    private ICommonService commonService;

    @Resource
    private IRedisService redisService;

    @Override
    public ExecuteRVO execute(NodeVO node, String onlyId, String tempTable) {
        LOGGER.info("[{}]：【{}】执行开始", this.getNodeTypeName(node.getNodeType()), node.getName());
        int count = 0;
        try {
            Timestamp sTime = new Timestamp(System.currentTimeMillis());

            RelevanceInfoVO associated = redisService.queryRelevanceInfoFromRedis(onlyId);
            assert associated != null;

            NodeHandler handler = NodeFactory.getInvokeStrategy(NodeTypeEnum.getType(node.getNodeType()));
            AssemblyRVO assembly = handler.assemblySql(node, associated.getLabels(), associated.getRelations(), tempTable);

            count = commonService.generateTempData(assembly, tempTable);

            if ("communicate".equals(node.getNodeType())) {
                ChannelHandler channelHandler = ChannelFactory.getInvokeStrategy(node.getSubType());
                if (channelHandler == null) {
                    throw new RuntimeException("渠道不存在！");
                }
                if (!channelHandler.generate(node, tempTable, associated.getLabels())) {
                    throw new RuntimeException("渠道报错");
                }
            }
            redisService.addExecutedNode(node.getNodeId(), onlyId);

            if (redisService.executeNextNode(node.getNodeId(), associated.getRelations(), onlyId)) {
                redisService.addNodeNextIdToRedis(node.getNodeId(), onlyId, associated.getRelations());
            }

            Timestamp eTime = new Timestamp(System.currentTimeMillis());


        } catch (Exception ex) {
            LOGGER.error("[{}]：【{}】执行失败：【{}】", this.getNodeTypeName(node.getNodeType()), node.getName(), ex.getMessage());
        }

        LOGGER.info("[{}]：【{}】执行结束", this.getNodeTypeName(node.getNodeType()), node.getName());
        return ExecuteRVO.builder().build();
    }

    private String getNodeTypeName(String nodeType) {
        String nodeTypeName = "";

        switch (nodeType) {
            case "and":
                nodeTypeName = "与合并节点";
                break;
            case "choose":
                nodeTypeName = "选择节点";
                break;
            case "communicate":
                nodeTypeName = "沟通节点";
                break;
            case "or":
                nodeTypeName = "或合并节点";
                break;
            case "unit":
                nodeTypeName = "单元节点";
                break;
            case "split":
                nodeTypeName = "单元节点";
                break;
            default:
                break;
        }
        return nodeTypeName;
    }
}
