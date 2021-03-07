package com.liushao.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * 文章评论（mongoDB）
 * @author huangshen
 */
@Data
@ApiModel(value = "评论mongoDB")
public class Comment implements Serializable {

    private static final long serialVersionUID = 7212242187409603851L;

    @ApiModelProperty(value = "_id")
    @Id
    private String _id;

    @ApiModelProperty(value = "文章id", required = true)
    private String articleid;

    @ApiModelProperty(value = "评论内容", required = true)
    private String content;

    @ApiModelProperty(value = "用户id")
    private String userid;

    @ApiModelProperty(value = "父评论id")
    private String parentid;

    //这里暂时不确定是否这么使用注解，因为这里的数据来自mongoDB
    @JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @ApiModelProperty(value = "发布时间")
    private Date publishdate;

    @JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @ApiModelProperty(value = "审核时间")
    private Date checktime;

    @ApiModelProperty(value = "状态")
    private String state;
}
