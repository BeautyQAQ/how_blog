package com.liushao.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
/**
 * 专栏搜索实体类
 * @author Administrator
 *
 */
@Data
@ApiModel(value = "专栏")
@Document(indexName="column",type="docs")
public class Column implements Serializable{

	private static final long serialVersionUID = -5145926516795764886L;

	@ApiModelProperty(value = "ID")
	@Id
	private String id;

	@ApiModelProperty(value = "专栏名称")
	@Field(index= true,analyzer="ik_max_word",searchAnalyzer="ik_max_word")
	private String name;

	@ApiModelProperty(value = "专栏简介")
	@Field(index= true,analyzer="ik_max_word",searchAnalyzer="ik_max_word")
	private String summary;

	@ApiModelProperty(value = "用户ID")
	private String userid;

}
