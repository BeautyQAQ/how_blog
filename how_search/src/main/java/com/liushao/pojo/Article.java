package com.liushao.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * 文章搜索实体类
 * @author huangshen
 */
@Data
@Schema(description = "文章搜索实体类")
@Document(indexName="article")
public class Article implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 4018596605143052936L;

    @Schema(description = "ID编号")
    @Id
    private String id;

    @Schema(description = "标题")
    @Field(type = FieldType.Text, analyzer="smartcn",searchAnalyzer="smartcn")
    private String title;

    @Schema(description = "文章正文")
    @Field(type = FieldType.Text, analyzer="smartcn",searchAnalyzer="smartcn")
    private String content;

}

