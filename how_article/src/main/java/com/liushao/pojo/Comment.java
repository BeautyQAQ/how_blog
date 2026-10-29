package com.liushao.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * 文章评论
 * @author huangshen
 */
@Data
@ApiModel(value = "评论")
@Entity
@Table(name = "tb_comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 7212242187409603851L;

    @ApiModelProperty(value = "_id")
    @Id
    @Column(name = "id", length = 20, nullable = false)
    private String _id;

    @ApiModelProperty(value = "文章id", required = true)
    @Column(length = 20)
    private String articleid;

    @ApiModelProperty(value = "评论内容", required = true)
    @Column(columnDefinition = "TEXT")
    private String content;

    @ApiModelProperty(value = "用户id")
    @Column(length = 20)
    private String userid;

    @ApiModelProperty(value = "父评论id")
    @Column(length = 20)
    private String parentid;

    @JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @ApiModelProperty(value = "发布时间")
    @Temporal(TemporalType.TIMESTAMP)
    private Date publishdate;

    @JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @ApiModelProperty(value = "审核时间")
    @Temporal(TemporalType.TIMESTAMP)
    private Date checktime;

    @ApiModelProperty(value = "状态")
    @Column(length = 1)
    private String state;
}
