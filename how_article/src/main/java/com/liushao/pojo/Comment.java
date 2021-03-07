package com.liushao.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @ApiModelProperty(value = "文章标题")
    private String article;

    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "用户id")
    private String userid;

    @ApiModelProperty(value = "用户名称")
    private String user;

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

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Date getChecktime() {
        return checktime;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public void setChecktime(Date checktime) {
        this.checktime = checktime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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
