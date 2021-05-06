package com.liushao.dao;

import com.liushao.pojo.NoFriend;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 不喜欢列表数据访问层
 * @author huangshen
 */
public interface NoFriendDao extends JpaRepository<NoFriend,String> {
}

