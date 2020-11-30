package com.liushao.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.io.Serializable;

/**
 * 文章实体类
 * @author huangshen
 */
@ApiModel(value = "文章搜索实体类")
@Document(indexName="how",type="article")
public class Article implements Serializable {

    @ApiModelProperty(value = "ID编号")
    @Id
    private String id;

    @ApiModelProperty(value = "标题")
    @Field(index= true,analyzer="ik_max_word",searchAnalyzer="ik_max_word")
    private String title;

    @ApiModelProperty(value = "文章正文")
    @Field(index= true,analyzer="ik_max_word",searchAnalyzer="ik_max_word")
    private String content;

    @ApiModelProperty(value = "审核状态")
    private String state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}

