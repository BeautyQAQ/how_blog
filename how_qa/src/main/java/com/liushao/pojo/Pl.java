package com.liushao.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author huangshen
 */
@ApiModel(value = "问题和标签关联对象")
@Entity
@Table(name="tb_pl")
public class Pl implements Serializable {

    @ApiModelProperty(value = "问题id")
    @Id
    private String problemid;

    @ApiModelProperty(value = "标签id")
    @Id
    private String labelid;
    public String getLabelid() {
        return labelid;
    }
    public void setLabelid(String lableid) {
        this.labelid = lableid;
    }
    public String getProblemid() {
        return problemid;
    }
    public void setProblemid(String problemid) {
        this.problemid = problemid;
    }
}