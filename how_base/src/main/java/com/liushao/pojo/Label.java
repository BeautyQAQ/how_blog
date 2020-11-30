package com.liushao.pojo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 标签实体
 * @author huangshen
 */
@ApiModel(value = "标签")
@Entity
@Table(name="tb_label")
public class Label {

    @ApiModelProperty(value = "ID")
    @Id
    private String id;

    @ApiModelProperty(value = "标签名称")
    private String labelname;

    @ApiModelProperty(value = "状态")
    private String state;

    @ApiModelProperty(value = "使用数量")
    private Long count;

    @ApiModelProperty(value = "关注数")
    private Long fans;

    @ApiModelProperty(value = "是否推荐")
    private String recommend;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabelname() {
        return labelname;
    }

    public void setLabelname(String labelname) {
        this.labelname = labelname;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getFans() {
        return fans;
    }

    public void setFans(Long fans) {
        this.fans = fans;
    }

    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }
}
