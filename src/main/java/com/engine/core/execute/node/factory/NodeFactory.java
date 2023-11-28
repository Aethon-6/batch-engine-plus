package com.engine.core.execute.node.factory;

import java.util.concurrent.ConcurrentHashMap;

import com.engine.enums.NodeTypeEnum;
import org.springframework.util.ObjectUtils;

public class NodeFactory {
    private static ConcurrentHashMap<NodeTypeEnum, NodeHandler> strategyMap = new ConcurrentHashMap<>();

    public static NodeHandler getInvokeStrategy(NodeTypeEnum nodeType) {
        return strategyMap.get(nodeType);
    }

    public static void register(NodeTypeEnum nodeType, NodeHandler nodeHandler) {
        if (ObjectUtils.isEmpty(nodeType) && nodeHandler == null) {
            return;
        }
        strategyMap.put(nodeType, nodeHandler);
    }
}
