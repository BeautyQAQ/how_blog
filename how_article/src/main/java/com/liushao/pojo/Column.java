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
 * 专栏实体类
 * @author Administrator
 *
 */
@Data
@ApiModel(value = "专栏")
@Entity
@Table(name="tb_column")
public class Column implements Serializable{

	private static final long serialVersionUID = -5145926516795764886L;

	@ApiModelProperty(value = "ID")
	@Id
	private String id;

	@ApiModelProperty(value = "专栏名称", required = true)
	private String name;

	@ApiModelProperty(value = "专栏简介")
	private String summary;

	@ApiModelProperty(value = "用户ID")
	private String userid;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@ApiModelProperty(value = "申请日期")
	private java.util.Date createtime;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@ApiModelProperty(value = "审核日期")
	private java.util.Date checktime;

	@ApiModelProperty(value = "状态")
	private String state;
}
