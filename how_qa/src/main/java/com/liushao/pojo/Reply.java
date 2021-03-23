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
@ApiModel(value = "回复")
@Entity
@Table(name="tb_reply")
public class Reply implements Serializable{

	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = -8910640791140199637L;

	@ApiModelProperty(value = "ID编号")
	@Id
	private String id;

	@ApiModelProperty(value = "问题id")
	private String problemid;

	@ApiModelProperty(value = "回答内容", required = true)
	private String content;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@ApiModelProperty(value = "创建日期")
	private java.util.Date createtime;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@ApiModelProperty(value = "更新日期")
	private java.util.Date updatetime;

	@ApiModelProperty(value = "回答人ID")
	private String userid;

	@ApiModelProperty(value = "回答人昵称")
	private String nickname;
	
}
