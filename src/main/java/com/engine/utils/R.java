package com.engine.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class R {
    private Boolean flag;
    private Integer code;
    private String message;
    private Map<String, Object> data = new HashMap<>();

    public static R success() {
        return R.builder()
                .flag(true)
                .code(ResultCode.SUCCESS)
                .message("成功")
                .build();
    }

    public static R error() {
        return R.builder()
                .flag(false)
                .code(ResultCode.ERROR)
                .message("失败")
                .build();
    }

    public R code(Integer code) {
        this.setCode(code);
        return this;
    }

    public R success(Boolean success) {
        this.setFlag(success);
        return this;
    }

    public R message(String message) {
        this.setMessage(message);
        return this;
    }

    public R data(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        this.setData(map);
        return this;
    }

    public R data(Map<String, Object> data) {
        this.setData(data);
        return this;
    }

}
