package com.liushao.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
/**
 * 实体类
 * @author Administrator
 *
 */
@Data
@ApiModel(value = "活动")
@Entity
@Table(name="tb_gathering")
public class Gathering implements Serializable{

	@ApiModelProperty(value = "ID编号")
	@Id
	private String id;

	@ApiModelProperty(value = "活动名称", required = true)
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
	@ApiModelProperty(value = "开始时间", required = true)
	private java.util.Date starttime;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@ApiModelProperty(value = "截止时间", required = true)
	private java.util.Date endtime;

	@ApiModelProperty(value = "举办地点", required = true)
	private String address;

	@ApiModelProperty(value = "报名截止", required = true)
	private java.util.Date enrolltime;

	@ApiModelProperty(value = "是否可见")
	private String state;

	@ApiModelProperty(value = "城市")
	private String city;
	
}
