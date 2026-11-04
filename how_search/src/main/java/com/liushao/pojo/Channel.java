package com.liushao.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "频道")
@Document(indexName="channel")
public class Channel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Schema(description = "ID")
	@Id
	private String id;

	@Schema(description = "频道名称")
	@Field(type = FieldType.Text, analyzer="smartcn",searchAnalyzer="smartcn")
	private String name;

}
