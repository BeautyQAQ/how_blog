package com.liushao.pojo;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 标签实体
 * @author huangshen
 */
@Data
@ApiModel(value = "标签")
@Document(indexName="how",type="label")
public class Label {

    @ApiModelProperty(value = "ID")
    @Id
    private String id;

    @ApiModelProperty(value = "标签名称")
    @Field(index= true,analyzer="ik_max_word",searchAnalyzer="ik_max_word")
    private String labelname;

}
