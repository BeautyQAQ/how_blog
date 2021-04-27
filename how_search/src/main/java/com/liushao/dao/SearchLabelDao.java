package com.liushao.dao;

import com.liushao.pojo.Label;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 标签数据访问层接口
 * @author huangshen
 */
public interface SearchLabelDao extends ElasticsearchRepository<Label,String> {

    /**
     * 检索
     */
    Page<Label> findByLabelnameLike(String labelname, Pageable pageable);

}
