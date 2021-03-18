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
@ApiModel(value = "文章")
@Entity
@Table(name="tb_article")
public class Article implements Serializable{

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "ID")
	@Id
	private String id;

	@ApiModelProperty(value = "专栏ID")
	private String columnid;

	@ApiModelProperty(value = "用户ID")
	private String userid;

	@ApiModelProperty(value = "标题", required = true)
	private String title;

	@ApiModelProperty(value = "文章正文", required = true)
	private String content;

	@ApiModelProperty(value = "文章封面", required = true)
	private String image;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@ApiModelProperty(value = "发表日期")
	private java.util.Date createtime;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@ApiModelProperty(value = "修改日期")
	private java.util.Date updatetime;

	@ApiModelProperty(value = "是否公开")
	private String ispublic;

	@ApiModelProperty(value = "是否置顶")
	private String istop;

	@ApiModelProperty(value = "浏览量")
	private Integer visits;

	@ApiModelProperty(value = "点赞数")
	private Integer thumbup;

	@ApiModelProperty(value = "评论数")
	private Integer comment;

	@ApiModelProperty(value = "审核状态")
	private String state;

	@ApiModelProperty(value = "所属频道")
	private String channelid;

	@ApiModelProperty(value = "URL")
	private String url;

	@ApiModelProperty(value = "类型")
	private String type;
}
