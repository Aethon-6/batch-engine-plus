package com.engine.active.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.*;

/**
 * <p>
 * 客户人口轮廓
 * </p>
 *
 * @author ywx
 * @since 2023-03-08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("engine_customer_rklk")
public class EngineCustomerRklk implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 客户唯一编号
     */
    @TableField("khwybh")
    private Long khwybh;

    /**
     * 客户年龄
     */
    @TableField("khnl")
    private Integer khnl;

    /**
     * 性别
     */
    @TableField("xb")
    private String xb;

    /**
     * 开户日期
     */
    @TableField("khrq")
    private LocalDateTime khrq;
}
