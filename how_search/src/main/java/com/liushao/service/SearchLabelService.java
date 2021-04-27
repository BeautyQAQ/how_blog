package com.liushao.service;

import java.util.Optional;

import com.liushao.dao.SearchLabelDao;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.pojo.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * @author huangshen
 */
@Service
public class SearchLabelService {
    @Autowired
    private SearchLabelDao searchLabelDao;
    /**
     * 增加标签
     * @param label
     */
    public void add(Label label){
        searchLabelDao.save(label);
    }

    /**
     * 标签搜索
     * @param keywords
     * @param page
     * @param size
     * @return
     */
    public Page<Label> findByLabelnameLike(String keywords, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page-1, size);
        return searchLabelDao.findByLabelnameLike(keywords, pageRequest);
    }

    /**
     * 标签删除
     * @param id
     */
    public Result deleteById(String id) {
        // 先查询索引中是否存在
        Optional<Label> channel = searchLabelDao.findById(id);
        if(channel.isPresent()){
            searchLabelDao.deleteById(id);
            return new Result(true, StatusCode.OK, "删除成功");
        }
        return new Result(true, StatusCode.OK, "删除失败，该标签不存在");
    }
}