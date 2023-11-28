package com.engine.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveVO {
    private String id;
    private String name;
    private List<NodeVO> nodes;
    private List<NodeRelationVO> nodeRelations;
}
