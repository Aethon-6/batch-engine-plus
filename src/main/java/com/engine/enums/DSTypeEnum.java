package com.engine.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DSTypeEnum {
    MASTER("master"),
    SLAVE("slave");

    private final String name;
}
