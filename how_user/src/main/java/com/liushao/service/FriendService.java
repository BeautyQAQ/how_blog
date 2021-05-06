package com.liushao.service;

import com.liushao.dao.FriendDao;
import com.liushao.dao.NoFriendDao;
import com.liushao.pojo.Friend;
import com.liushao.pojo.NoFriend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author huangshen
 */
@Service
public class FriendService {
    @Autowired
    private FriendDao friendDao;
    @Autowired
    private NoFriendDao noFriendDao;
    @Autowired
    private UserService userService;

    @Transactional(rollbackFor = Exception.class)
    public int addFriend(String userid,String friendid){
        //判断如果用户已经添加了这个好友，则不进行任何操作,返回0
        if(friendDao.selectCount(userid, friendid)>0){
            return 0;
        }
        //向喜欢表中添加记录
        Friend friend=new Friend();
        friend.setUserid(userid);
        friend.setFriendid(friendid);
        friend.setIslike("0");
        friendDao.save(friend);
        //判断对方是否喜欢你，如果喜欢，将islike设置为1
        if(friendDao.selectCount( friendid,userid)>0){
            friendDao.updateLike(userid,friendid,"1");
            friendDao.updateLike(friendid,userid,"1");
        }
        userService.incFollowcount(userid,1);//增加自己的关注数
        userService.incFanscount(friendid,1);//增加对方的粉丝数
        return 1;
    }

    /**
     * 向不喜欢列表中添加记录
     * @param userid
     * @param friendid
     */
    public void addNoFriend(String userid,String friendid){
        NoFriend noFriend=new NoFriend();
        noFriend.setUserid(userid);
        noFriend.setFriendid(friendid);
        noFriendDao.save(noFriend);
    }

    /**
     * 删除好友
     * @param userid
     * @param friendid
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteFriend(String userid,String friendid){
        friendDao.deleteFriend(userid,friendid);
        friendDao.updateLike(friendid,userid,"0");
        addNoFriend(userid,friendid);//向不喜欢表中添加记录
        userService.incFollowcount(userid,-1);//减少自己的关注数
        userService.incFanscount(friendid,-1);//减少对方的粉丝数
    }
}