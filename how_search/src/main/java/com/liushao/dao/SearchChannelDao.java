package com.liushao.dao;

import com.liushao.pojo.Channel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 频道数据访问层接口
 * @author huangshen
 */
public interface SearchChannelDao extends ElasticsearchRepository<Channel,String> {
    /**
     * 检索
     * @param
     * @return
     */
    Page<Channel> findByNameLike(String name, Pageable pageable);

}
