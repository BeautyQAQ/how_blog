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
@ApiModel(value = "活动")
@Entity
@Table(name="tb_gathering")
public class Gathering implements Serializable{

	@ApiModelProperty(value = "ID编号")
	@Id
	private String id;

	@ApiModelProperty(value = "活动名称")
	private String name;

	@ApiModelProperty(value = "大会简介")
	private String summary;

	@ApiModelProperty(value = "详细说明")
	private String detail;

	@ApiModelProperty(value = "主办方")
	private String sponsor;

	@ApiModelProperty(value = "活动图片")
	private String image;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@ApiModelProperty(value = "开始时间")
	private java.util.Date starttime;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@ApiModelProperty(value = "截止时间")
	private java.util.Date endtime;

	@ApiModelProperty(value = "举办地点")
	private String address;

	@ApiModelProperty(value = "报名截止")
	private java.util.Date enrolltime;

	@ApiModelProperty(value = "是否可见")
	private String state;

	@ApiModelProperty(value = "城市")
	private String city;

	
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

	public String getDetail() {		
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getSponsor() {		
		return sponsor;
	}
	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}

	public String getImage() {		
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}

	public java.util.Date getStarttime() {		
		return starttime;
	}
	public void setStarttime(java.util.Date starttime) {
		this.starttime = starttime;
	}

	public java.util.Date getEndtime() {		
		return endtime;
	}
	public void setEndtime(java.util.Date endtime) {
		this.endtime = endtime;
	}

	public String getAddress() {		
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public java.util.Date getEnrolltime() {		
		return enrolltime;
	}
	public void setEnrolltime(java.util.Date enrolltime) {
		this.enrolltime = enrolltime;
	}

	public String getState() {		
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {		
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}


	
}
