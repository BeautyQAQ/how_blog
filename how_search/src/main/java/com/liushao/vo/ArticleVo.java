package com.liushao.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 文章标题搜索VO
 * @author huangshen
 */
@Data
@Schema(description = "文章标题搜索Vo")
public class ArticleVo implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 4018596605143052936L;

    @Schema(description = "ID编号")
    private String id;

    @Schema(description = "标题")
    private String title;

}

