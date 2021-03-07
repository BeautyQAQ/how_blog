package com.liushao.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@ApiModel(value = "非好友")
@Entity
@Table(name="tb_nofriend")
@IdClass(NoFriend.class)
public class NoFriend implements Serializable {

    @ApiModelProperty(value = "userid")
    @Id
    private String userid;

    @ApiModelProperty(value = "friendid", required = true)
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
