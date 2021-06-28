package com.liushao.controller;

import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.pojo.*;
import com.liushao.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 控制器层
 * 前缀 /article
 * @author Administrator
 *
 */
@Api(tags = "数据初始化控制层")
@RestController
@CrossOrigin
@RequestMapping("/init")
public class InitController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private LabelService labelService;

    @Autowired
    private ColumnService columnService;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private ReplyService replyService;

    /**
     * 查询全部可搜索数据,用于搜索模块初始化
     * @return 全部可搜索数据
     */
    @ApiOperation(value = "查询全部文章数据")
    @RequestMapping(value="/data", method= RequestMethod.GET)
    public Result init(){
        List<Article> articles = articleService.findAll();
        List<Channel> channels = channelService.findAll();
        List<Label> labels = labelService.findAll();
        List<Column> columns = columnService.findAll();
        List<Problem> problems = problemService.findAll();
        List<Reply> replies = replyService.findAll();
        Map<String, Object> map = new HashMap<>();
        map.put("article", articles);
        map.put("channel", channels);
        map.put("label", labels);
        map.put("column", columns);
        map.put("problem", problems);
        map.put("reply", replies);
        return new Result(true, StatusCode.OK,"查询成功", map);
    }
}
