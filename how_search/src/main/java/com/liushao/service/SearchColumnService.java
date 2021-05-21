package com.liushao.service;

import java.util.Optional;

import com.liushao.dao.SearchColumnDao;
import com.liushao.pojo.Column;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * @author huangshen
 */
@Service
public class SearchColumnService {

    @Autowired
    private SearchColumnDao searchColumndDao;

    /**
     * 增加专栏
     * 
     * @param article
     */
    public void add(Column column) {
        searchColumndDao.save(column);
    }

    /**
     * 专栏搜索
     * 
     * @param keywords
     * @param page
     * @param size
     * @return 分页结果
     */
    public Page<Column> findByNameOrSummaryLike(String keywords, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return searchColumndDao.findByNameOrSummaryLike(keywords, keywords, pageRequest);
    }

    /**
     * 专栏删除
     * @param id
     */
    public Result deleteById(String id) {
        // 先查询索引中是否存在
        Optional<Column> column = searchColumndDao.findById(id);
        if(column.isPresent()){
            searchColumndDao.deleteById(id);
            return new Result(true, StatusCode.OK, "删除成功");
        }
        return new Result(true, StatusCode.OK, "删除失败，该专栏不存在");
    }

    /**
     * 专栏更新
     * @param channel 专栏
     */
    public void update(Column column) {
        // 先查询索引中是否存在
        Optional<Column> columnOptional = searchColumndDao.findById(column.getId());
        if(columnOptional.isPresent()){
            searchColumndDao.save(column);
        }
    }
}