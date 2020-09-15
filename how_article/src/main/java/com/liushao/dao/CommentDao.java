package com.liushao.dao;

import com.liushao.pojo.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * 评论Dao
 * @author huangshen
 */
public interface CommentDao  extends MongoRepository<Comment, String> {
    /**
     * 根据文章ID查询评论列表
     * @param articleid
     * @return
     */
    List<Comment> findByArticleid(String articleid);
}
