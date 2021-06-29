package com.liushao.service;

import com.liushao.client.InitEsClient;
import com.liushao.dao.*;
import com.liushao.entity.Result;
import com.liushao.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.hutool.json.JSONArray;

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
    public void init(){
        Result init = initEsClient.init();
        Map<String, Object> data = (Map<String, Object>)init.getData();
        List<Map<String, Object>> articleMap = (List<Map<String,Object>>) data.get("article");
        JSONArray articleArray = new JSONArray();
        articleArray.addAll(articleMap);
        List<Article> article = articleArray.toList(Article.class);

        List<Map<String, Object>> channelMap = (List<Map<String,Object>>) data.get("channel");
        JSONArray channelArray = new JSONArray();
        channelArray.addAll(channelMap);
        List<Channel> channel = channelArray.toList(Channel.class);

        List<Map<String, Object>> labelMap = (List<Map<String,Object>>) data.get("label");
        JSONArray labelArray = new JSONArray();
        labelArray.addAll(labelMap);
        List<Label> label = labelArray.toList(Label.class);

        List<Map<String, Object>> columnMap = (List<Map<String,Object>>) data.get("column");
        JSONArray columnArray = new JSONArray();
        columnArray.addAll(columnMap);
        List<Column> column = columnArray.toList(Column.class);

        List<Map<String, Object>> problemMap = (List<Map<String,Object>>) data.get("problem");
        JSONArray problemArray = new JSONArray();
        problemArray.addAll(problemMap);
        List<Problem> problem = problemArray.toList(Problem.class);

        List<Map<String, Object>> replyMap = (List<Map<String,Object>>) data.get("reply");
        JSONArray replyArray = new JSONArray();
        replyArray.addAll(replyMap);
        List<Reply> reply = replyArray.toList(Reply.class);

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
