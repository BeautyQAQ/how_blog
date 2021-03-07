package com.liushao.pojo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 标签实体
 * @author huangshen
 */
@Data
@ApiModel(value = "标签")
@Entity
@Table(name="tb_label")
public class Label {

    @ApiModelProperty(value = "ID")
    @Id
    private String id;

    @ApiModelProperty(value = "标签名称", required = true)
    private String labelname;

    @ApiModelProperty(value = "状态")
    private String state;

    @ApiModelProperty(value = "使用数量")
    private Long count;

    @ApiModelProperty(value = "关注数")
    private Long fans;

    @ApiModelProperty(value = "是否推荐")
    private String recommend;

}
