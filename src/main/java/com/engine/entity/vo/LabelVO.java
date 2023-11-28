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
public class LabelVO {
    private String logicOper;
    private String oper;
    private String version;
    private String parentKey;
    private String key;
    private String lLogicOper;
    private String rLogicOper;
    private String dateType;
    private String nextNodeId;
    private String splitPercent;
    private List<LabelValueVO> itemData;
    private Integer order;
}
