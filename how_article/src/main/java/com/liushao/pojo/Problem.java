package com.liushao.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
/**
 * 问题实体类
 * @author Administrator
 *
 */
@Data
@Schema(description = "问题")
@Entity
@Table(name="tb_problem")
public class Problem implements Serializable{

	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 455998952938095229L;

	@Schema(description = "ID编号")
	@Id
	private String id;

	@Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
	private String title;

	@Schema(description = "内容", requiredMode = Schema.RequiredMode.REQUIRED)
	private String content;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@Schema(description = "创建日期")
	private java.util.Date createtime;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@Schema(description = "修改日期")
	private java.util.Date updatetime;

	@Schema(description = "用户ID")
	private String userid;

	@Schema(description = "昵称")
	private String nickname;

	@Schema(description = "浏览量")
	private Long visits;

	@Schema(description = "点赞数")
	private Long thumbup;

	@Schema(description = "回复数")
	private Long reply;

	@Schema(description = "是否解决")
	private String solve;

	@Schema(description = "回复人昵称")
	private String replyname;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@Schema(description = "回复日期")
	private java.util.Date replytime;

}
