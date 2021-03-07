package com.liushao.pojo;

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
@ApiModel(value = "Admin用户")
@Entity
@Table(name="tb_admin")
public class Admin implements Serializable{

	@ApiModelProperty(value = "ID")
	@Id
	private String id;

	@ApiModelProperty(value = "登录名称", required = true)
	private String loginname;

	@ApiModelProperty(value = "密码", required = true)
	private String password;

	@ApiModelProperty(value = "状态")
	private String state;
}
