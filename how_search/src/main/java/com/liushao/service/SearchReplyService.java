package com.liushao.service;

import java.util.Optional;

import com.liushao.dao.SearchReplyDao;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.pojo.Reply;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * @author huangshen
 */
@Service
public class SearchReplyService {
    @Autowired
    private SearchReplyDao searchReplyDao;
    /**
     * 增加回复
     * @param reply
     */
    public void add(Reply reply){
        searchReplyDao.save(reply);
    }

    /**
     * 回复搜索
     * @param keywords
     * @param page
     * @param size
     * @return
     */
    public Page<Reply> findByContentLike(String keywords, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page-1, size);
        return searchReplyDao.findByContentLike(keywords, pageRequest);
    }

    /**
     * 回复删除
     * @param id
     */
    public Result deleteById(String id) {
        // 先查询索引中是否存在
        Optional<Reply> channel = searchReplyDao.findById(id);
        if(channel.isPresent()){
            searchReplyDao.deleteById(id);
            return new Result(true, StatusCode.OK, "删除成功");
        }
        return new Result(true, StatusCode.OK, "删除失败，该问题不存在");
    }

    /**
     * 回复更新
     * @param reply 回复
     */
    public void update(Reply reply) {
        // 先查询索引中是否存在
        Optional<Reply> replyOptional = searchReplyDao.findById(reply.getId());
        if(replyOptional.isPresent()){
            searchReplyDao.save(reply);
        }
    }
}