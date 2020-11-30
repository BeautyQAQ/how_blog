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
@ApiModel(value = "User用户")
@Entity
@Table(name="tb_user")
public class User implements Serializable{

	@ApiModelProperty(value = "ID编号")
	@Id
	private String id;

	@ApiModelProperty(value = "手机号码")
	private String mobile;

	@ApiModelProperty(value = "密码")
	private String password;

	@ApiModelProperty(value = "昵称")
	private String nickname;

	@ApiModelProperty(value = "性别")
	private String sex;

	@ApiModelProperty(value = "出生年月日")
	private java.util.Date birthday;

	@ApiModelProperty(value = "头像")
	private String avatar;

	@ApiModelProperty(value = "E-Mail")
	private String email;

	@ApiModelProperty(value = "注册日期")
	private java.util.Date regdate;

	@ApiModelProperty(value = "修改日期")
	private java.util.Date updatedate;

	@ApiModelProperty(value = "最后登录日期")
	private java.util.Date lastdate;

	@ApiModelProperty(value = "在线时长（分钟）")
	private Long online;

	@ApiModelProperty(value = "兴趣")
	private String interest;

	@ApiModelProperty(value = "个性")
	private String personality;

	@ApiModelProperty(value = "粉丝数")
	private Integer fanscount;

	@ApiModelProperty(value = "关注数量")
	private Integer followcount;

	
	public String getId() {		
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getMobile() {		
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPassword() {		
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickname() {		
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSex() {		
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}

	public java.util.Date getBirthday() {		
		return birthday;
	}
	public void setBirthday(java.util.Date birthday) {
		this.birthday = birthday;
	}

	public String getAvatar() {		
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getEmail() {		
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public java.util.Date getRegdate() {		
		return regdate;
	}
	public void setRegdate(java.util.Date regdate) {
		this.regdate = regdate;
	}

	public java.util.Date getUpdatedate() {		
		return updatedate;
	}
	public void setUpdatedate(java.util.Date updatedate) {
		this.updatedate = updatedate;
	}

	public java.util.Date getLastdate() {		
		return lastdate;
	}
	public void setLastdate(java.util.Date lastdate) {
		this.lastdate = lastdate;
	}

	public Long getOnline() {		
		return online;
	}
	public void setOnline(Long online) {
		this.online = online;
	}

	public String getInterest() {		
		return interest;
	}
	public void setInterest(String interest) {
		this.interest = interest;
	}

	public String getPersonality() {		
		return personality;
	}
	public void setPersonality(String personality) {
		this.personality = personality;
	}

	public Integer getFanscount() {		
		return fanscount;
	}
	public void setFanscount(Integer fanscount) {
		this.fanscount = fanscount;
	}

	public Integer getFollowcount() {		
		return followcount;
	}
	public void setFollowcount(Integer followcount) {
		this.followcount = followcount;
	}


	
}
