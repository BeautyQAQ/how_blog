package com.liushao.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
/**
 * 专栏实体类
 * @author Administrator
 *
 */
@Data
@Schema(description = "专栏")
@Entity
@Table(name="tb_column")
public class Column implements Serializable{

	private static final long serialVersionUID = -5145926516795764886L;

	@Schema(description = "ID")
	@Id
	private String id;

	@Schema(description = "专栏名称", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;

	@Schema(description = "专栏简介")
	private String summary;

	@Schema(description = "用户ID")
	private String userid;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@Schema(description = "申请日期")
	private java.util.Date createtime;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@Schema(description = "审核日期")
	private java.util.Date checktime;

	@Schema(description = "状态")
	private String state;
}
