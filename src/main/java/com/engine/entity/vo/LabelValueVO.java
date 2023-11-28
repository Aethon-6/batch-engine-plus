package com.engine.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabelValueVO {
    private String oper;
    private String inputValue;
    private String inputValueLow;
    private String inputValueHigh;
}
