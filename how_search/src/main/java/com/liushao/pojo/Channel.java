package com.liushao.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 频道搜索实体类
 * @author Administrator
 *
 */
@Data
@ApiModel(value = "频道")
@Document(indexName="channel",type="docs")
public class Channel implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "ID")
	@Id
	private String id;

	@ApiModelProperty(value = "频道名称")
	@Field(type = FieldType.Text, analyzer="ik_max_word",searchAnalyzer="ik_max_word")
	private String name;

}
