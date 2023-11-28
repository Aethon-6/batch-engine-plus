package com.engine.active.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.*;

/**
 * <p>
 * 标签配置表
 * </p>
 *
 * @author ywx
 * @since 2023-03-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("engine_label_config")
public class EngineLabelConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标签大类
     */
    @TableField("label_b_type")
    private String labelBType;

    /**
     * 标签大类名称
     */
    @TableField("label_b_type_name")
    private String labelBTypeName;

    /**
     * 标签大类别名
     */
    @TableField("label_b_alias")
    private String labelBAlias;

    /**
     * 字段名
     */
    @TableField("field_no")
    private String fieldNo;

    /**
     * 字段名称
     */
    @TableField("field_name")
    private String fieldName;

    /**
     * 字段名称
     */
    @TableField("field_type")
    private String fieldType;
}
