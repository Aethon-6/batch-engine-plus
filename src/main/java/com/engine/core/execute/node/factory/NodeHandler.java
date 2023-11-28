package com.engine.core.execute.node.factory;

import com.engine.entity.vo.AssemblyRVO;
import com.engine.entity.vo.LabelInfoVO;
import com.engine.entity.vo.NodeRelationVO;
import com.engine.entity.vo.NodeVO;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

public abstract class NodeHandler implements InitializingBean {

    public AssemblyRVO assemblySql(NodeVO node, List<LabelInfoVO> labelList, List<NodeRelationVO> relations, String table) {
        return AssemblyRVO.builder().build();
    }
}
