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
 * 下发批次
 * </p>
 *
 * @author ywx
 * @since 2023-03-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("engine_customer_issued_batch")
public class EngineCustomerIssuedBatch implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 批次编号
     */
    @TableField("batch_no")
    private String batchNo;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 话术
     */
    @TableField("content")
    private String content;
}
