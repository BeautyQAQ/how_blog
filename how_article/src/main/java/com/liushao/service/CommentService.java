package com.liushao.service;

import cn.hutool.core.bean.BeanUtil;
import com.liushao.client.UserClient;
import com.liushao.dao.CommentDao;
import com.liushao.entity.Result;
import com.liushao.pojo.Comment;
import com.liushao.util.IdWorker;
import com.liushao.vo.CommentVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private UserClient userClient;
    @Autowired
    private CommentDao commentDao;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 添加评论
     * @param comment
     */
    public void add(com.liushao.pojo.Comment comment){
        // 设置ID
        comment.set_id( idWorker.nextId()+"" );
        // 设置发布时间
        comment.setPublishdate(new Date());
        // 设置状态未审核
        comment.setState("0");
        commentDao.save(comment);
    }

    /**
     * 删除评论
     */
    public void deleteById(String id){
        commentDao.deleteById(id);
    }

    /**
     * 根据文章ID查询评论列表
     */
    public List<com.liushao.pojo.Comment> findByArticleid(String articleid){
        return commentDao.findByArticleid(articleid);
    }

    /**
     * 条件分页查询
     */
    public Page<CommentVo> findSearch(Map<String, Object> searchMap, int page, int size) {
        //条件值对象
        Comment comment = new Comment();
        if(StringUtils.isNotEmpty((String)searchMap.get("id"))){
            comment.set_id((String)searchMap.get("id"));
        }
        if(StringUtils.isNotEmpty((String)searchMap.get("articleid"))){
            comment.setArticleid((String)searchMap.get("articleid"));
        }
        if(StringUtils.isNotEmpty((String)searchMap.get("content"))){
            comment.setContent((String)searchMap.get("content"));
        }
        if(StringUtils.isNotEmpty((String)searchMap.get("userid"))){
            comment.setUserid((String)searchMap.get("userid"));
        }
        if(StringUtils.isNotEmpty((String)searchMap.get("parentid"))){
            comment.setParentid((String)searchMap.get("parentid"));
        }
        if(StringUtils.isNotEmpty((String)searchMap.get("state"))){
            comment.setState((String)searchMap.get("state"));
        }
        if(StringUtils.isNotEmpty((String)searchMap.get("publishdate"))){
            comment.setPublishdate((Date) searchMap.get("publishdate"));
        }

        //条件匹配器 如果不设置匹配器默认精确匹配
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("content",ExampleMatcher.GenericPropertyMatchers.contains());
                //.withMatcher("processStatus",ExampleMatcher.GenericPropertyMatchers.exact());

        //定义example条件对象
        Example<Comment> example = Example.of(comment,exampleMatcher);
        PageRequest pageRequest =  PageRequest.of(page-1, size);
        Page<Comment> comments = commentDao.findAll(example, pageRequest);

        CommentVo commentVo = new CommentVo();
        ArrayList<CommentVo> commentVos = new ArrayList<>();
        for (Comment commentOne : comments) {
            BeanUtil.copyProperties(commentOne, commentVo);
            // 设置文章标题
            commentVo.setArticle(articleService.findById(commentOne.getArticleid()).getTitle());
            // 设置用户名
            Result nicknameById = userClient.findNicknameById(commentOne.getUserid());
            System.out.println(nicknameById);
            commentVo.setUser((String)userClient.findNicknameById(commentOne.getUserid()).getData());
            commentVos.add(commentVo);
        }
        Page<CommentVo> commentVoPage = new PageImpl<CommentVo>(commentVos);
        return commentVoPage;
    }

    /**
     * 审核
     * @param id
     */
    public void examine(String id) {
        Query query=new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Update update = Update.update("state", "1");
        mongoTemplate.updateFirst(query, update, "commentVo");
    }
}
