package com.liushao.dao;

import com.liushao.pojo.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * 评论Dao
 * @author huangshen
 */
public interface CommentDao extends JpaRepository<Comment, String> {
    /**
     * 根据文章ID查询评论列表
     * @param articleid
     * @return
     */
    List<Comment> findByArticleid(String articleid);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Comment c set c.state = '1', c.checktime = :checktime where c._id = :id")
    int examine(@Param("id") String id, @Param("checktime") Date checktime);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Comment c where c._id = :id")
    int deleteExistingById(@Param("id") String id);
}
