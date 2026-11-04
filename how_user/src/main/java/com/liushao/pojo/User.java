package com.liushao.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
/**
 * 实体类
 * @author Administrator
 *
 */
@Data
@Schema(description = "User用户")
@Entity
@Table(name="tb_user")
public class User implements Serializable{

	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 376268953281515231L;

	@Schema(description = "ID编号")
	@Id
	private String id;

	@Schema(description = "手机号码", requiredMode = Schema.RequiredMode.REQUIRED)
	private String mobile;

	@Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
	private String password;

	@Schema(description = "昵称", requiredMode = Schema.RequiredMode.REQUIRED)
	private String nickname;

	@Schema(description = "性别")
	private String sex;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd",timezone="GMT+8")
	@Schema(description = "出生年月日")
	private java.util.Date birthday;

	@Schema(description = "头像")
	private String avatar;

	@Schema(description = "E-Mail")
	private String email;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@Schema(description = "注册日期")
	private java.util.Date regdate;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@Schema(description = "修改日期")
	private java.util.Date updatedate;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@Schema(description = "最后登录日期")
	private java.util.Date lastdate;

	@Schema(description = "在线时长（分钟）")
	private Long online;

	@Schema(description = "兴趣")
	private String interest;

	@Schema(description = "个性")
	private String personality;

	@Schema(description = "粉丝数")
	private Integer fanscount;

	@Schema(description = "关注数量")
	private Integer followcount;
}
