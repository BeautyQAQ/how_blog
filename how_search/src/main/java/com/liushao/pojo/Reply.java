package com.liushao.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
/**
 * 回复实体类
 * @author Administrator
 *
 */
@Data
@Schema(description = "回复")
@Document(indexName="reply")
public class Reply implements Serializable{

	@Schema(description = "ID编号")
	@Id
	private String id;

	@Schema(description = "问题id")
	private String problemid;

	@Schema(description = "回答内容")
	@Field(type = FieldType.Text, analyzer="smartcn",searchAnalyzer="smartcn")
	private String content;

	@Schema(description = "回答人ID")
	private String userid;
	
}
