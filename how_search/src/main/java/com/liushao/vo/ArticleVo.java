package com.liushao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 文章标题搜索VO
 * @author huangshen
 */
@Data
@ApiModel(value = "文章标题搜索Vo")
public class ArticleVo implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 4018596605143052936L;

    @ApiModelProperty(value = "ID编号")
    private String id;

    @ApiModelProperty(value = "标题")
    private String title;

}

