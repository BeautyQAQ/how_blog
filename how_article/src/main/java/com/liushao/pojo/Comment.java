package com.liushao.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * 文章评论
 * @author huangshen
 */
@Data
@Schema(description = "评论")
@Entity
@Table(name = "tb_comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 7212242187409603851L;

    @Schema(description = "_id")
    @JsonProperty("_id")
    @Id
    @Column(name = "id", length = 20, nullable = false)
    private String _id;

    @Schema(description = "文章id", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(length = 20)
    private String articleid;

    @Schema(description = "评论内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(columnDefinition = "TEXT")
    private String content;

    @Schema(description = "用户id")
    @Column(length = 20)
    private String userid;

    @Schema(description = "父评论id")
    @Column(length = 20)
    private String parentid;

    @JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Schema(description = "发布时间")
    @Temporal(TemporalType.TIMESTAMP)
    private Date publishdate;

    @JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Schema(description = "审核时间")
    @Temporal(TemporalType.TIMESTAMP)
    private Date checktime;

    @Schema(description = "状态")
    @Column(length = 1)
    private String state;
}
