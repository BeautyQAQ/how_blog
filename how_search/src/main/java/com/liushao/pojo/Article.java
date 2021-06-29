package com.liushao.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "文章搜索实体类")
@Document(indexName="article",type="docs")
public class Article implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 4018596605143052936L;

    @ApiModelProperty(value = "ID编号")
    @Id
    private String id;

    @ApiModelProperty(value = "标题")
    @Field(type = FieldType.Text, analyzer="ik_max_word",searchAnalyzer="ik_max_word")
    private String title;

    @ApiModelProperty(value = "文章正文")
    @Field(type = FieldType.Text, analyzer="ik_max_word",searchAnalyzer="ik_max_word")
    private String content;

}

