package com.liushao.controller;

import com.liushao.entity.PageResult;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.service.CommentService;
import com.liushao.vo.CommentVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 前缀 /article
 * @author Administrator
 */
@Tag(name = "评论控制层")
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
    @Operation(summary = "分页+多条件查询")
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
    @Operation(summary = "添加评论")
    @RequestMapping(method= RequestMethod.POST)
    public Result save(@RequestBody com.liushao.pojo.Comment comment, HttpServletRequest request){
        Claims claims = (Claims) request.getAttribute("user_claims");
        comment.setUserid(claims.getId());
        commentService.add(comment);
        return new Result(true, StatusCode.OK, "已提交，等待审核", comment);
    }

    /**
     * 删除
     * @param id
     */
    @Operation(summary = "删除评论")
    @RequestMapping(value="/{id}",method=RequestMethod.DELETE)
    public Result deleteById(@PathVariable String id ){
        commentService.deleteById(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /**
     * 根据文章ID查询评论列表
     */
    @Operation(summary = "根据文章id查询评论列表")
    @RequestMapping(value="/article/{articleid}",method= RequestMethod.GET)
    public Result findByArticleid(@PathVariable String articleid){
        return new Result(true, StatusCode.OK, "查询成功", commentService.findByArticleid(articleid));
    }

    /**
     * 审核
     * @param id
     * @return
     */
    @Operation(summary = "审核评论")
    @RequestMapping(value="/examine/{id}",method=RequestMethod.PUT)
    public Result examine(@PathVariable String id){
        commentService.examine(id);
        return new Result(true, StatusCode.OK, "审核成功！");
    }
}
