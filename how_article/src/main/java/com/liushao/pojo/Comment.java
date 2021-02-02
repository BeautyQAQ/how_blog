package com.liushao.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * 文章评论（mongoDB）
 * @author huangshen
 */
@ApiModel(value = "评论mongoDB")
public class Comment implements Serializable {

    private static final long serialVersionUID = 7212242187409603851L;

    @ApiModelProperty(value = "_id")
    @Id
    private String _id;

    @ApiModelProperty(value = "文章id")
    private String articleid;

    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "用户id")
    private String userid;

    @ApiModelProperty(value = "父评论id")
    private String parentid;

    //这里暂时不确定是否这么使用注解，因为这里的数据来自mongoDB
    //@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @ApiModelProperty(value = "发布时间")
    private Date publishdate;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getArticleid() {
        return articleid;
    }

    public void setArticleid(String articleid) {
        this.articleid = articleid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public Date getPublishdate() {
        return publishdate;
    }

    public void setPublishdate(Date publishdate) {
        this.publishdate = publishdate;
    }
}
