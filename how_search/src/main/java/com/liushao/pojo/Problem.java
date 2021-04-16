package com.liushao.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
/**
 * 问题实体类
 * @author Administrator
 *
 */
@Data
@ApiModel(value = "问题")
@Document(indexName="how",type="problem")
public class Problem implements Serializable{

	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 455998952938095229L;

	@ApiModelProperty(value = "ID编号")
	@Id
	private String id;

	@ApiModelProperty(value = "标题")
	@Field(index= true,analyzer="ik_max_word",searchAnalyzer="ik_max_word")
	private String title;

	@ApiModelProperty(value = "内容")
	@Field(index= true,analyzer="ik_max_word",searchAnalyzer="ik_max_word")
	private String content;

	@ApiModelProperty(value = "用户ID")
	private String userid;

	@ApiModelProperty(value = "是否解决")
	private String solve;

}
