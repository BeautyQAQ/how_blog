package com.liushao.controller;

import com.liushao.entity.PageResult;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.pojo.Channel;
import com.liushao.service.SearchChannelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 前缀 /search
 * @author huangshen
 */
@Api(tags = "频道搜索")
@RestController
@CrossOrigin
@RequestMapping("/search")
public class SearchChannelController {
    @Autowired
    private SearchChannelService searchChannelService;

    /**
     * 频道索引添加
     * @param channel
     * @return
     */
    @ApiOperation(value = "在es保存频道")
    @RequestMapping(value = "/channel", method= RequestMethod.POST)
    public Result save(@RequestBody Channel channel){
        searchChannelService.add(channel);
        return new Result(true, StatusCode.OK, "操作成功");
    }

    /**
     * 频道搜索
     * @param keywords 关键字
     * @param page 页码
     * @param size 页面大小
     */
    @ApiOperation(value = "频道搜索")
    @RequestMapping(value="/channel/{keywords}/{page}/{size}",method= RequestMethod.GET)
    public Result findByTitleOrContentLike(@PathVariable String keywords, @PathVariable int page, @PathVariable int size){
        Page<Channel> channelPage = searchChannelService.findByNameLike(keywords, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Channel>(channelPage.getTotalElements(), channelPage.getContent()));
    }

    /**
     * 删除频道索引
     */
    @ApiOperation(value = "频道删除")
    @RequestMapping(value="/channel/delete/{id}",method= RequestMethod.DELETE)
    public Result delete(@PathVariable String id){
        return searchChannelService.deleteById(id);
    }

    /**
     * 更新频道索引
     */
    @ApiOperation(value = "频道更新")
    @RequestMapping(value="/channel/update",method= RequestMethod.PUT)
    public Result update(Channel channel){
        searchChannelService.update(channel);
        return new Result(true, StatusCode.OK, "操作成功");
    }
}
