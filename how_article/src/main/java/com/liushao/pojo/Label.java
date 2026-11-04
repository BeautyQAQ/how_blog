package com.liushao.pojo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 标签实体
 * @author huangshen
 */
@Data
@Schema(description = "标签")
@Entity
@Table(name="tb_label")
public class Label {

    @Schema(description = "ID")
    @Id
    private String id;

    @Schema(description = "标签名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String labelname;

    @Schema(description = "状态")
    private String state;

    @Schema(description = "使用数量")
    private Long count;

    @Schema(description = "关注数")
    private Long fans;

    @Schema(description = "是否推荐")
    private String recommend;

}
