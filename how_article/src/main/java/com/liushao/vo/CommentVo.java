package com.liushao.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 评论Vo（mongoDB）
 * @author huangshen
 */
@Data
public class CommentVo implements Serializable {

    private String _id;

    private String id;

    private String articleid;

    private String content;

    private String userid;

    private String parentid;

    private Date publishdate;

    private Date checktime;

    private String state;

    private String article;

    private String user;
}
