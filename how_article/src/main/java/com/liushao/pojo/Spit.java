package com.liushao.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.io.Serializable;
import java.util.Date;

/**
 * 吐槽
 * @author huangshen
 */
@Data
@Schema(description = "吐槽")
@Entity
@Table(name = "tb_spit")
public class Spit implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -3220778639859847389L;

    @Schema(description = "_id")
    @Id
    @Column(name = "id", length = 20, nullable = false)
    private String _id;

    @Schema(description = "内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(columnDefinition = "TEXT")
    private String content;

    @JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Schema(description = "发布时间")
    @Temporal(TemporalType.TIMESTAMP)
    private Date publishtime;

    @Schema(description = "用户id")
    @Column(length = 20)
    private String userid;

    @Schema(description = "用户昵称")
    @Column(length = 100)
    private String nickname;

    @Schema(description = "浏览量")
    @Column(nullable = false)
    private Integer visits = 0;

    @Schema(description = "点赞数")
    @Column(nullable = false)
    private Integer thumbup = 0;

    @Schema(description = "分享数量")
    @Column(name = "`share`", nullable = false)
    private Integer share = 0;

    @Schema(description = "评论数量")
    @Column(name = "`comment`", nullable = false)
    private Integer comment = 0;

    @Schema(description = "状态")
    @Column(length = 1)
    private String state;

    @Schema(description = "父ID")
    @Column(length = 20)
    private String parentid;

    @PrePersist
    @PreUpdate
    private void initializeCounters() {
        visits = visits == null ? 0 : visits;
        thumbup = thumbup == null ? 0 : thumbup;
        share = share == null ? 0 : share;
        comment = comment == null ? 0 : comment;
    }
}
