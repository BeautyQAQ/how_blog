package com.liushao.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author huangshen
 */
@ApiModel(value = "好友")
@Entity
@Table(name="tb_friend")
@IdClass(Friend.class)
public class Friend implements Serializable {

    @ApiModelProperty(value = "userid")
    @Id
    private String userid;

    @ApiModelProperty(value = "friendid", required = true)
    @Id
    private String friendid;

    @ApiModelProperty(value = "是否喜欢")
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
