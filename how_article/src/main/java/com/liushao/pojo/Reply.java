package com.liushao.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
/**
 * 回复实体类
 * @author Administrator
 *
 */
@Data
@Schema(description = "回复")
@Entity
@Table(name="tb_reply")
public class Reply implements Serializable{

	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = -8910640791140199637L;

	@Schema(description = "ID编号")
	@Id
	private String id;

	@Schema(description = "问题id")
	private String problemid;

	@Schema(description = "回答内容", requiredMode = Schema.RequiredMode.REQUIRED)
	private String content;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@Schema(description = "创建日期")
	private java.util.Date createtime;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@Schema(description = "更新日期")
	private java.util.Date updatetime;

	@Schema(description = "回答人ID")
	private String userid;

	@Schema(description = "回答人昵称")
	private String nickname;
	
}
