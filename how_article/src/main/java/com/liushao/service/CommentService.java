package com.liushao.service;

import com.liushao.dao.CommentDao;
import com.liushao.pojo.Comment;
import com.liushao.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentDao commentDao;
    @Autowired
    private IdWorker idWorker;

    /**
     * 添加评论
     * @param comment
     */
    public void add(Comment comment){
        comment.set_id( idWorker.nextId()+"" );
        commentDao.save(comment);
    }

    /**
     * 删除评论
     */
    public void deleteById(String id){
        commentDao.deleteById(id);
    }

    /**
     * 根据文章ID查询评论列表
     */
    public List<Comment> findByArticleid(String articleid){
        return commentDao.findByArticleid(articleid);
    }

}
