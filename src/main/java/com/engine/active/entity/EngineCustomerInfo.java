package com.engine.active.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.*;

/**
 * <p>
 * 
 * </p>
 *
 * @author ywx
 * @since 2023-03-08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("engine_customer_info")
public class EngineCustomerInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户编号
     */
    @TableId("id")
    private Long id;

    /**
     * 客户姓名
     */
    @TableField("name")
    private String name;

    /**
     * 客户年龄
     */
    @TableField("age")
    private Integer age;
}
