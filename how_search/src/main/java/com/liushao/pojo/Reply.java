package com.liushao.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
/**
 * 回复实体类
 * @author Administrator
 *
 */
@Data
@ApiModel(value = "回复")
@Document(indexName="reply",type="docs")
public class Reply implements Serializable{

	@ApiModelProperty(value = "ID编号")
	@Id
	private String id;

	@ApiModelProperty(value = "问题id")
	private String problemid;

	@ApiModelProperty(value = "回答内容")
	@Field(index= true,analyzer="ik_max_word",searchAnalyzer="ik_max_word")
	private String content;

	@ApiModelProperty(value = "回答人ID")
	private String userid;
	
}
