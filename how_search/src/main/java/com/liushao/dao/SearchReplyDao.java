package com.liushao.dao;


import com.liushao.pojo.Reply;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 回答数据访问层接口
 * @author huangshen
 */
public interface SearchReplyDao extends ElasticsearchRepository<Reply,String> {

    /**
     * 检索
     */
    Page<Reply> findByContentLike(String content, Pageable pageable);

}
