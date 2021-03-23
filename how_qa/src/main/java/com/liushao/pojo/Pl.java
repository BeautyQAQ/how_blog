package com.liushao.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author huangshen
 */
@Data
@ApiModel(value = "问题和标签关联对象")
@Entity
@Table(name="tb_pl")
public class Pl implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -4130801027386954396L;

    @ApiModelProperty(value = "问题id")
    @Id
    private String problemid;

    @ApiModelProperty(value = "标签id")
    @Id
    private String labelid;
}