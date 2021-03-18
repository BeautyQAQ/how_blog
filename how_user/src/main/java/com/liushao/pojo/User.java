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
@ApiModel(value = "User用户")
@Entity
@Table(name="tb_user")
public class User implements Serializable{

	@ApiModelProperty(value = "ID编号")
	@Id
	private String id;

	@ApiModelProperty(value = "手机号码", required = true)
	private String mobile;

	@ApiModelProperty(value = "密码", required = true)
	private String password;

	@ApiModelProperty(value = "昵称", required = true)
	private String nickname;

	@ApiModelProperty(value = "性别")
	private String sex;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd",timezone="GMT+8")
	@ApiModelProperty(value = "出生年月日")
	private java.util.Date birthday;

	@ApiModelProperty(value = "头像")
	private String avatar;

	@ApiModelProperty(value = "E-Mail")
	private String email;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@ApiModelProperty(value = "注册日期")
	private java.util.Date regdate;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@ApiModelProperty(value = "修改日期")
	private java.util.Date updatedate;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
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
}
