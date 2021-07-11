package com.liushao.controller;

import com.liushao.entity.PageResult;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.pojo.Reply;
import com.liushao.service.SearchReplyService;

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
@Api(tags = "回复搜索")
@RestController
@CrossOrigin
@RequestMapping("/search")
public class SearchReplyController {

    @Autowired
    private SearchReplyService searchReplyService;

    /**
     * 回复索引添加
     * @param reply 回复
     * @return Result
     */
    @ApiOperation(value = "在es保存回复")
    @RequestMapping(value = "/reply", method= RequestMethod.POST)
    public Result save(@RequestBody Reply reply){
        searchReplyService.add(reply);
        return new Result(true, StatusCode.OK, "操作成功");
    }

    /**
     * 回复搜索
     * @param keywords 关键字
     * @param page 页码
     * @param size 页面大小
     */
    @ApiOperation(value = "回复搜索")
    @RequestMapping(value="/reply/{keywords}/{page}/{size}",method= RequestMethod.GET)
    public Result findByContentLike(@PathVariable String keywords, @PathVariable int page, @PathVariable int size){
        Page<Reply> reply = searchReplyService.findByContentLike(keywords, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<>(reply.getTotalElements(), reply.getContent()));
    }

    /**
     * 删除回复索引
     */
    @ApiOperation(value = "回复删除")
    @RequestMapping(value="/reply/delete/{id}",method= RequestMethod.DELETE)
    public Result delete(@PathVariable String id){
        return searchReplyService.deleteById(id);
    }

    /**
     * 更新回复索引
     */
    @ApiOperation(value = "回复更新")
    @RequestMapping(value="/reply/update",method= RequestMethod.PUT)
    public Result update(Reply reply){
        searchReplyService.update(reply);
        return new Result(true, StatusCode.OK, "操作成功");
    }
}
