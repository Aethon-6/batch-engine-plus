package com.engine.core.execute.common.service;

import com.engine.active.entity.EngineLabelConfig;
import com.engine.entity.vo.AssemblyRVO;
import com.engine.entity.vo.NodeRelationVO;
import com.engine.entity.vo.NodeVO;
import com.engine.entity.vo.RelevanceInfoVO;

import java.util.List;
import java.util.Map;

public interface ICommonService {
    /**
     * 解析节点
     *
     * @param nodes     节点
     * @param relations 节点关系
     * @return
     */
    List<EngineLabelConfig> extractLabel(List<NodeVO> nodes, List<NodeRelationVO> relations);

    /**
     * 生成临时表数据
     *
     * @param assembly  临时表数据生成依据
     * @param tempTable 临时表
     * @return
     */
    int generateTempData(AssemblyRVO assembly, String tempTable);

    /**
     * json格式验证
     *
     * @param json json字符串
     * @return
     */
    boolean jsonValidator(String json);

    /**
     * 渠道下发
     *
     * @param innSql insert sql语句
     */
    void channelIssued(String innSql);
}
