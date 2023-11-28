package com.engine.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 关联信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelevanceInfoVO {
    private List<LabelInfoVO> labels;
    private List<NodeRelationVO> relations;
}
