package com.liushao.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 * @author huangshen
 */
@Schema(description = "好友")
@Entity
@Table(name="tb_friend")
@IdClass(FriendId.class)
public class Friend implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -6849107332525437532L;

    @Schema(description = "userid")
    @Id
    private String userid;

    @Schema(description = "friendid", requiredMode = Schema.RequiredMode.REQUIRED)
    @Id
    private String friendid;

    @Schema(description = "是否喜欢")
    private String islike;

    public String getIslike() {
        return islike;
    }

    public void setIslike(String islike) {
        this.islike = islike;
    }

    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }
    public String getFriendid() {
        return friendid;
    }
    public void setFriendid(String friendid) {
        this.friendid = friendid;
    }
}
