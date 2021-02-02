package com.liushao.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
/**
 * 实体类
 * @author Administrator
 *
 */
@ApiModel(value = "问题")
@Entity
@Table(name="tb_problem")
public class Problem implements Serializable{

	@ApiModelProperty(value = "ID编号")
	@Id
	private String id;

	@ApiModelProperty(value = "标题")
	private String title;

	@ApiModelProperty(value = "内容")
	private String content;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@ApiModelProperty(value = "创建日期")
	private java.util.Date createtime;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@ApiModelProperty(value = "修改日期")
	private java.util.Date updatetime;

	@ApiModelProperty(value = "用户ID")
	private String userid;

	@ApiModelProperty(value = "昵称")
	private String nickname;

	@ApiModelProperty(value = "浏览量")
	private Long visits;

	@ApiModelProperty(value = "点赞数")
	private Long thumbup;

	@ApiModelProperty(value = "回复数")
	private Long reply;

	@ApiModelProperty(value = "是否解决")
	private String solve;

	@ApiModelProperty(value = "回复人昵称")
	private String replyname;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@ApiModelProperty(value = "回复日期")
	private java.util.Date replytime;

	
	public String getId() {		
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {		
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {		
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public java.util.Date getCreatetime() {		
		return createtime;
	}
	public void setCreatetime(java.util.Date createtime) {
		this.createtime = createtime;
	}

	public java.util.Date getUpdatetime() {		
		return updatetime;
	}
	public void setUpdatetime(java.util.Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getUserid() {		
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getNickname() {		
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Long getVisits() {		
		return visits;
	}
	public void setVisits(Long visits) {
		this.visits = visits;
	}

	public Long getThumbup() {		
		return thumbup;
	}
	public void setThumbup(Long thumbup) {
		this.thumbup = thumbup;
	}

	public Long getReply() {		
		return reply;
	}
	public void setReply(Long reply) {
		this.reply = reply;
	}

	public String getSolve() {		
		return solve;
	}
	public void setSolve(String solve) {
		this.solve = solve;
	}

	public String getReplyname() {		
		return replyname;
	}
	public void setReplyname(String replyname) {
		this.replyname = replyname;
	}

	public java.util.Date getReplytime() {		
		return replytime;
	}
	public void setReplytime(java.util.Date replytime) {
		this.replytime = replytime;
	}


	
}
