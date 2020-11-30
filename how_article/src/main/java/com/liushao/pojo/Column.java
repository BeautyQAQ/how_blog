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
@ApiModel(value = "专栏")
@Entity
@Table(name="tb_column")
public class Column implements Serializable{

	private static final long serialVersionUID = -5145926516795764886L;

	@ApiModelProperty(value = "ID")
	@Id
	private String id;

	@ApiModelProperty(value = "专栏名称")
	private String name;

	@ApiModelProperty(value = "专栏简介")
	private String summary;

	@ApiModelProperty(value = "用户ID")
	private String userid;

	@ApiModelProperty(value = "申请日期")
	private java.util.Date createtime;

	@ApiModelProperty(value = "审核日期")
	private java.util.Date checktime;

	@ApiModelProperty(value = "状态")
	private String state;

	
	public String getId() {		
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {		
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getSummary() {		
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getUserid() {		
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}

	public java.util.Date getCreatetime() {		
		return createtime;
	}
	public void setCreatetime(java.util.Date createtime) {
		this.createtime = createtime;
	}

	public java.util.Date getChecktime() {		
		return checktime;
	}
	public void setChecktime(java.util.Date checktime) {
		this.checktime = checktime;
	}

	public String getState() {		
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}


	
}
