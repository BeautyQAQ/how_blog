package com.liushao.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
/**
 * 问题实体类
 * @author Administrator
 *
 */
@Data
@Schema(description = "问题")
@Document(indexName="problem")
public class Problem implements Serializable{

	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 455998952938095229L;

	@Schema(description = "ID编号")
	@Id
	private String id;

	@Schema(description = "标题")
	@Field(type = FieldType.Text, analyzer="smartcn",searchAnalyzer="smartcn")
	private String title;

	@Schema(description = "内容")
	@Field(type = FieldType.Text, analyzer="smartcn",searchAnalyzer="smartcn")
	private String content;

	@Schema(description = "用户ID")
	private String userid;

	@Schema(description = "是否解决")
	private String solve;

}
