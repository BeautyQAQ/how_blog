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
@ApiModel(value = "问题")
@Entity
@Table(name="tb_problem")
public class Problem implements Serializable{

	@ApiModelProperty(value = "ID编号")
	@Id
	private String id;

	@ApiModelProperty(value = "标题", required = true)
	private String title;

	@ApiModelProperty(value = "内容", required = true)
	private String content;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@ApiModelProperty(value = "创建日期")
	private java.util.Date createtime;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@ApiModelProperty(value = "修改日期")
	private java.util.Date updatetime;

	@ApiModelProperty(value = "用户ID")
	private String userid;

	@ApiModelProperty(value = "昵称")
	private String nickname;

	@ApiModelProperty(value = "浏览量")
	private Long visits;

	@ApiModelProperty(value = "点赞数")
	private Long thumbup;

	@ApiModelProperty(value = "回复数")
	private Long reply;

	@ApiModelProperty(value = "是否解决")
	private String solve;

	@ApiModelProperty(value = "回复人昵称")
	private String replyname;

	@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@ApiModelProperty(value = "回复日期")
	private java.util.Date replytime;

}
