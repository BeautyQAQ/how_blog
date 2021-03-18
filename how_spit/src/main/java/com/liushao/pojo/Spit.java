package com.liushao.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

/**
 * 吐槽
 * @author huangshen
 */
@Data
@ApiModel(value = "吐槽")
public class Spit implements Serializable {

    @ApiModelProperty(value = "_id")
    @Id
    private String _id;

    @ApiModelProperty(value = "内容", required = true)
    private String content;

    @JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @ApiModelProperty(value = "发布时间")
    private Date publishtime;

    @ApiModelProperty(value = "用户id")
    private String userid;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "浏览量")
    private Integer visits;

    @ApiModelProperty(value = "点赞数")
    private Integer thumbup;

    @ApiModelProperty(value = "分享数量")
    private Integer share;

    @ApiModelProperty(value = "评论数量")
    private Integer comment;

    @ApiModelProperty(value = "状态")
    private String state;

    @ApiModelProperty(value = "父ID")
    private String parentid;

}
