package com.liushao.service;

import com.liushao.client.InitEsClient;
import com.liushao.dao.*;
import com.liushao.entity.Result;
import com.liushao.pojo.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

/**
 * @author huangshen
 */
@Service
public class InitEsService {

    @Autowired
    private InitEsClient initEsClient;

    @Autowired
    private SearchArticleDao searchArticleDao;

    @Autowired
    private SearchChannelDao searchChannelDao;

    @Autowired
    private SearchColumnDao searchColumnDao;

    @Autowired
    private SearchLabelDao searchLabelDao;

    @Autowired
    private SearchProblemDao searchProblemDao;

    @Autowired
    private SearchReplyDao searchReplyDao;

    /**
     * 初始化elasticsearch索引
     */
    @ApiOperation(value = "在es保存文章")
    @RequestMapping(value = "/init", method= RequestMethod.POST)
    public void init(){
        Result init = initEsClient.init();
        Map<String, Object> data = (Map<String, Object>)init.getData();
        List<Article> article = (List<Article>) data.get("article");
        List<Channel> channel = (List<Channel>) data.get("channel");
        List<Label> label = (List<Label>) data.get("label");
        List<Column> column = (List<Column>) data.get("column");
        List<Problem> problem = (List<Problem>) data.get("problem");
        List<Reply> reply = (List<Reply>) data.get("reply");
        searchArticleDao.deleteAll();
        searchArticleDao.saveAll(article);
        searchChannelDao.deleteAll();
        searchChannelDao.saveAll(channel);
        searchColumnDao.deleteAll();
        searchColumnDao.saveAll(column);
        searchLabelDao.deleteAll();
        searchLabelDao.saveAll(label);
        searchProblemDao.deleteAll();
        searchProblemDao.saveAll(problem);
        searchReplyDao.deleteAll();
        searchReplyDao.saveAll(reply);
    }
}
