package com.engine.active.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 短信下发
 * </p>
 *
 * @author ywx
 * @since 2023-03-21
 */
@Getter
@Setter
@TableName("engine_customer_sms_issued")
public class EngineCustomerSmsIssued implements Serializable {

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
     * 客户唯一编号
     */
    @TableField("customer_id")
    private Long customerId;

    /**
     * 话术
     */
    @TableField("content")
    private String content;

    /**
     * 是否使用（0：未使用；1：已使用）
     */
    @TableField("use_flag")
    private String useFlag;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}
