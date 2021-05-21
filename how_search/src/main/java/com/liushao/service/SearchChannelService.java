package com.liushao.service;

import java.util.Optional;

import com.liushao.dao.SearchChannelDao;
import com.liushao.pojo.Channel;
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
public class SearchChannelService {

    @Autowired
    private SearchChannelDao searchChanneldDao;

    /**
     * 增加频道
     * 
     * @param article
     */
    public void add(Channel channel) {
        searchChanneldDao.save(channel);
    }

    /**
     * 频道搜索
     * 
     * @param keywords
     * @param page
     * @param size
     * @return 分页结果
     */
    public Page<Channel> findByNameLike(String keywords, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return searchChanneldDao.findByNameLike(keywords, pageRequest);
    }

    /**
     * 频道删除
     * @param id
     */
    public Result deleteById(String id) {
        // 先查询索引中是否存在
        Optional<Channel> channel = searchChanneldDao.findById(id);
        if(channel.isPresent()){
            searchChanneldDao.deleteById(id);
            return new Result(true, StatusCode.OK, "删除成功");
        }
        return new Result(true, StatusCode.OK, "删除失败，该频道不存在");
    }

    /**
     * 频道更新
     * @param channel 频道
     */
    public void update(Channel channel) {
        // 先查询索引中是否存在
        Optional<Channel> channelOptional = searchChanneldDao.findById(channel.getId());
        if(channelOptional.isPresent()){
            searchChanneldDao.save(channel);
        }
    }
}