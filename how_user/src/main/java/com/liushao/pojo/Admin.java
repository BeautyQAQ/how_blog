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
@ApiModel(value = "Admin用户")
@Entity
@Table(name="tb_admin")
public class Admin implements Serializable{

	@ApiModelProperty(value = "ID")
	@Id
	private String id;

	@ApiModelProperty(value = "登录名称")
	private String loginname;

	@ApiModelProperty(value = "密码")
	private String password;

	@ApiModelProperty(value = "状态")
	private String state;

	
	public String getId() {		
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getLoginname() {		
		return loginname;
	}
	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	public String getPassword() {		
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getState() {		
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}

}
