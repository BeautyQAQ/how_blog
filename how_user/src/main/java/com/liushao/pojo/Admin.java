package com.liushao.pojo;

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
@Schema(description = "Admin用户")
@Entity
@Table(name="tb_admin")
public class Admin implements Serializable{

	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = -7732162879078866642L;

	@Schema(description = "ID")
	@Id
	private String id;

	@Schema(description = "登录名称", requiredMode = Schema.RequiredMode.REQUIRED)
	private String loginname;

	@Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
	private String password;

	@Schema(description = "状态")
	private String state;
}
