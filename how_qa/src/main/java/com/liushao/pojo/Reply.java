package com.liushao.pojo;

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
@ApiModel(value = "回复")
@Entity
@Table(name="tb_reply")
public class Reply implements Serializable{

	@ApiModelProperty(value = "ID编号")
	@Id
	private String id;

	@ApiModelProperty(value = "问题id")
	private String problemid;

	@ApiModelProperty(value = "回答内容")
	private String content;

	@ApiModelProperty(value = "创建日期")
	private java.util.Date createtime;

	@ApiModelProperty(value = "更新日期")
	private java.util.Date updatetime;

	@ApiModelProperty(value = "回答人ID")
	private String userid;

	@ApiModelProperty(value = "回答人昵称")
	private String nickname;

	
	public String getId() {		
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getProblemid() {		
		return problemid;
	}
	public void setProblemid(String problemid) {
		this.problemid = problemid;
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


	
}
