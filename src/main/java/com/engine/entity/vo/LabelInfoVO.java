package com.engine.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabelInfoVO {
    private Long id;
    private String labelBType;
    private String labelBAlias;
    private String fieldNo;
    private String fieldType;
}
