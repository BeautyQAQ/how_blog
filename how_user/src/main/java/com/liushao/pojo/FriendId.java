package com.liushao.pojo;

import java.io.Serializable;
import java.util.Objects;

/** Composite key shared by the friend and no-friend tables. */
public class FriendId implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userid;
    private String friendid;

    public FriendId() {
    }

    public FriendId(String userid, String friendid) {
        this.userid = userid;
        this.friendid = friendid;
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

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FriendId that)) {
            return false;
        }
        return Objects.equals(userid, that.userid) && Objects.equals(friendid, that.friendid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userid, friendid);
    }
}
