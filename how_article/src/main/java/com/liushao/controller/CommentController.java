package com.liushao.controller;

import com.liushao.entity.PageResult;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.service.CommentService;
import com.liushao.vo.CommentVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 前缀 /article
 * @author Administrator
 */
@Api(tags = "评论控制层")
@RestController
@CrossOrigin
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 分页+多条件查询
     * @param searchMap 查询条件封装
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @ApiOperation(value = "分页+多条件查询")
    @RequestMapping(value="/search/{page}/{size}",method=RequestMethod.POST)
    public Result findSearch(@RequestBody Map<String, Object> searchMap , @PathVariable int page, @PathVariable int size){
        Page<CommentVo> pageList = commentService.findSearch(searchMap, page, size);
        return  new Result(true,StatusCode.OK,"查询成功",  new PageResult<CommentVo>(pageList.getTotalElements(), pageList.getContent()) );
    }

    /**
     * 添加评论
     * @param comment
     * @return
     */
    @ApiOperation(value = "添加评论")
    @RequestMapping(method= RequestMethod.POST)
    public Result save(@RequestBody com.liushao.pojo.Comment comment){
        commentService.add(comment);
        return new Result(true, StatusCode.OK, "提交成功 ");
    }

    /**
     * 删除
     * @param id
     */
    @ApiOperation(value = "删除评论")
    @RequestMapping(value="/{id}",method=RequestMethod.DELETE)
    public Result deleteById(@PathVariable String id ){
        commentService.deleteById(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /**
     * 根据文章ID查询评论列表
     */
    @ApiOperation(value = "根据文章id查询评论列表")
    @RequestMapping(value="/article/{articleid}",method= RequestMethod.GET)
    public Result findByArticleid(@PathVariable String articleid){
        return new Result(true, StatusCode.OK, "查询成功", commentService.findByArticleid(articleid));
    }

    /**
     * 审核
     * @param id
     * @return
     */
    @ApiOperation(value = "审核评论")
    @RequestMapping(value="/examine/{id}",method=RequestMethod.PUT)
    public Result examine(@PathVariable String id){
        commentService.examine(id);
        return new Result(true, StatusCode.OK, "审核成功！");
    }
}
