package com.liushao.pojo;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.FieldType;


/**
 * 标签实体
 * @author huangshen
 */
@Data
@Schema(description = "标签")
@Document(indexName="label")
public class Label {

    @Schema(description = "ID")
    @Id
    private String id;

    @Schema(description = "标签名称")
    @Field(type = FieldType.Text, analyzer="smartcn",searchAnalyzer="smartcn")
    private String labelname;

}
