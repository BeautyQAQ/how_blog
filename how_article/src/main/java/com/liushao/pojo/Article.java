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
@Schema(description = "文章")
@Entity
@Table(name="tb_article")
public class Article implements Serializable{

	private static final long serialVersionUID = 1L;

	@Schema(description = "ID")
	@Id
	private String id;

	@Schema(description = "专栏ID")
	private String columnid;

	@Schema(description = "用户ID")
	private String userid;

	@Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
	private String title;

	@Schema(description = "文章正文", requiredMode = Schema.RequiredMode.REQUIRED)
	@jakarta.persistence.Column(columnDefinition = "longtext")
	private String content;

	@Schema(description = "正文格式：text 或 markdown")
	@jakarta.persistence.Column(length = 16)
	private String contentformat;

	@Schema(description = "手工摘要，留空自动生成")
	@jakarta.persistence.Column(length = 300)
	private String summary;

	@Schema(description = "服务端生成的正文摘要")
	@jakarta.persistence.Column(length = 300)
	private String excerpt;

	@Schema(description = "文章封面", requiredMode = Schema.RequiredMode.REQUIRED)
	private String image;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@Schema(description = "发表日期")
	private java.util.Date createtime;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@Schema(description = "修改日期")
	private java.util.Date updatetime;

	@Schema(description = "是否公开")
	private String ispublic;

	@Schema(description = "是否置顶")
	private String istop;

	@Schema(description = "浏览量")
	private Integer visits;

	@Schema(description = "点赞数")
	private Integer thumbup;

	@Schema(description = "评论数")
	private Integer comment;

	@Schema(description = "审核状态")
	private String state;

	@Schema(description = "所属频道")
	private String channelid;

	@Schema(description = "URL")
	private String url;

	@Schema(description = "类型")
	private String type;
}
