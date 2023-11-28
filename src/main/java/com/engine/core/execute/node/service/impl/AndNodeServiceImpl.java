/*
package com.engine.core.execute.node.service.impl;

import com.engine.aop.annotation.NodeExecute;
import com.engine.core.execute.node.factory.NodeFactory;
import com.engine.core.execute.node.factory.NodeHandler;
import com.engine.core.execute.node.service.INodeService;
import com.engine.entity.vo.ExecuteRVO;
import com.engine.entity.vo.LabelInfoVO;
import com.engine.entity.vo.NodeVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service("and")
public class AndNodeServiceImpl implements INodeService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    @NodeExecute
    public ExecuteRVO execute(NodeVO node, List<LabelInfoVO> labelList) {
        logger.info("[与合并节点]：【{}】执行开始", node.getName());
        Timestamp sTime = new Timestamp(new Date().getTime());

        NodeHandler handler = NodeFactory.getInvokeStrategy(node.getNodeType());
        handler.assemblySql(node, labelList);

        Timestamp eTime = new Timestamp(new Date().getTime());

        logger.info("[与合并节点]：【{}】执行结束", node.getName());
        return ExecuteRVO.builder().build();
    }

}
*/
