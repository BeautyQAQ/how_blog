package com.liushao.client;

import com.liushao.client.impl.SearchClientImpl;
import com.liushao.entity.Result;
import com.liushao.pojo.Article;
import com.liushao.pojo.Channel;
import com.liushao.pojo.Column;
import com.liushao.pojo.Label;
import com.liushao.pojo.Problem;
import com.liushao.pojo.Reply;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author huangshen
 */
@FeignClient(value = "how-search", fallback = SearchClientImpl.class)
public interface SearchClient {

    /**
     * 在es中新增文章
     * @param article 文章
     * @return Result
     */
    @RequestMapping(value="/search/article", method = RequestMethod.POST)
    Result saveArticle(Article article);

    /**
     * 在es中更新文章
     * @param article 文章
     * @return Result
     */
    @RequestMapping(value="/search/article/update", method = RequestMethod.PUT)
    Result updateArticle(Article article);
    
    /**
     * 在es中删除文章
     * @param id id
     * @return Result
     */
    @RequestMapping(value="/search/article/delete/{id}", method = RequestMethod.DELETE)
    Result deleteArticle(@PathVariable String id);


    /**
     * 在es中新增频道
     * @param channel 频道
     * @return Result
     */
    @RequestMapping(value="/search/channel", method = RequestMethod.POST)
    Result saveChannel(Channel channel);

    /**
     * 在es中更新频道
     * @param channel 频道
     * @return Result
     */
    @RequestMapping(value="/search/channel/update", method = RequestMethod.PUT)
    Result updateChannel(Channel channel);

    /**
     * 在es中删除频道
     * @param id id
     * @return Result
     */
    @RequestMapping(value="/search/channel/delete/{id}", method = RequestMethod.DELETE)
    Result deleteChannel(@PathVariable String id);


    /**
     * 在es中新增标签
     * @param label 标签
     * @return Result
     */
    @RequestMapping(value="/search/label", method = RequestMethod.POST)
    Result saveLabel(Label label);

    /**
     * 在es中更新标签
     * @param label 标签
     * @return Result
     */
    @RequestMapping(value="/search/label/update", method = RequestMethod.PUT)
    Result updateLabel(Label label);

    /**
     * 在es中删除标签
     * @param id id
     * @return Result
     */
    @RequestMapping(value="/search/label/delete/{id}", method = RequestMethod.DELETE)
    Result deleteLabel(@PathVariable String id);


    /**
     * 在es中新增专栏
     * @param column 专栏
     * @return Result
     */
    @RequestMapping(value="/search/column", method = RequestMethod.POST)
    Result saveColumn(Column column);

    /**
     * 在es中更新专栏
     * @param colum 专栏
     * @return Result
     */
    @RequestMapping(value="/search/column/update", method = RequestMethod.PUT)
    Result updateColumn(Column column);

    /**
     * 在es中删除专栏
     * @param id id
     * @return Result
     */
    @RequestMapping(value="/search/column/delete/{id}", method = RequestMethod.DELETE)
    Result deleteColumn(@PathVariable String id);


    /**
     * 在es中新增问题
     * @param problem 问题
     * @return Result
     */
    @RequestMapping(value="/search/problem", method = RequestMethod.POST)
    Result saveProblem(Problem problem);

    /**
     * 在es中更新问题
     * @param problem 问题
     * @return Result
     */
    @RequestMapping(value="/search/problem/update", method = RequestMethod.PUT)
    Result updateProblem(Problem problem);

    /**
     * 在es中删除问题
     * @param id id
     * @return Result
     */
    @RequestMapping(value="/search/problem/delete/{id}", method = RequestMethod.DELETE)
    Result deleteProblem(@PathVariable String id);


    /**
     * 在es中新增回复
     * @param problem 回复
     * @return Result
     */
    @RequestMapping(value="/search/reply", method = RequestMethod.POST)
    Result saveReply(Reply reply);

    /**
     * 在es中更新回复
     * @param problem 回复
     * @return Result
     */
    @RequestMapping(value="/search/reply/update", method = RequestMethod.PUT)
    Result updateReply(Reply reply);
    /**
     * 在es中删除回复
     * @param id id
     * @return Result
     */
    @RequestMapping(value="/search/reply/delete/{id}", method = RequestMethod.DELETE)
    Result deleteReply(@PathVariable String id);

}
