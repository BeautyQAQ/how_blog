package com.liushao.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.io.Serializable;
import java.util.Date;

/**
 * 吐槽
 * @author huangshen
 */
@Data
@ApiModel(value = "吐槽")
@Entity
@Table(name = "tb_spit")
public class Spit implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -3220778639859847389L;

    @ApiModelProperty(value = "_id")
    @Id
    @Column(name = "id", length = 20, nullable = false)
    private String _id;

    @ApiModelProperty(value = "内容", required = true)
    @Column(columnDefinition = "TEXT")
    private String content;

    @JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @ApiModelProperty(value = "发布时间")
    @Temporal(TemporalType.TIMESTAMP)
    private Date publishtime;

    @ApiModelProperty(value = "用户id")
    @Column(length = 20)
    private String userid;

    @ApiModelProperty(value = "用户昵称")
    @Column(length = 100)
    private String nickname;

    @ApiModelProperty(value = "浏览量")
    @Column(nullable = false)
    private Integer visits = 0;

    @ApiModelProperty(value = "点赞数")
    @Column(nullable = false)
    private Integer thumbup = 0;

    @ApiModelProperty(value = "分享数量")
    @Column(name = "`share`", nullable = false)
    private Integer share = 0;

    @ApiModelProperty(value = "评论数量")
    @Column(name = "`comment`", nullable = false)
    private Integer comment = 0;

    @ApiModelProperty(value = "状态")
    @Column(length = 1)
    private String state;

    @ApiModelProperty(value = "父ID")
    @Column(length = 20)
    private String parentid;

    @PrePersist
    @PreUpdate
    private void initializeCounters() {
        visits = visits == null ? 0 : visits;
        thumbup = thumbup == null ? 0 : thumbup;
        share = share == null ? 0 : share;
        comment = comment == null ? 0 : comment;
    }
}
