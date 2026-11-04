package com.liushao.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 * 问题和标签关联对象
 * @author huangshen
 */
@Data
@Schema(description = "问题和标签关联对象")
@Entity
@Table(name="tb_pl")
public class Pl implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -4130801027386954396L;

    @Schema(description = "问题id")
    @Id
    private String problemid;

    @Schema(description = "标签id")
    @Id
    private String labelid;
}