package com.engine.core.execute.node.service;

import com.engine.entity.vo.ExecuteRVO;
import com.engine.entity.vo.LabelInfoVO;
import com.engine.entity.vo.NodeVO;

import java.util.List;

public interface INodeService {
    /*ExecuteRVO execute(NodeVO node, List<LabelInfoVO> labelList);*/
    ExecuteRVO execute(NodeVO node, String onlyId, String tempTable);
}
