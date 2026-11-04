package com.liushao.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;

@Schema(description = "非好友")
@Entity
@Table(name="tb_nofriend")
@IdClass(FriendId.class)
public class NoFriend implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -6817107813628847709L;

    @Schema(description = "userid")
    @Id
    private String userid;

    @Schema(description = "friendid", requiredMode = Schema.RequiredMode.REQUIRED)
    @Id
    private String friendid;

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
