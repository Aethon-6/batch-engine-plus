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
public class NodeVO {
    private String nodeId;
    private String code;
    private String property;
    private String name;
    private String subType;
    private String nodeType;
    private String splitRule;
    private CommunicateInfoVO communicateInfo;
    private List<LabelVO> nodeInfo;
}
