package com.liushao.controller;

import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.service.FriendService;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author huangshen
 */
@Api(tags = "交友模块")
@RestController
@RequestMapping("/friend/friend")
public class FriendController {
    @Autowired
    private FriendService friendService;
    @Autowired
    private HttpServletRequest request;
    /**
     * 添加好友
     * @param friendid 对方用户ID
     * @param type 1：喜欢 0：不喜欢
     * @return
     */
    @ApiOperation(value = "添加好友")
    @RequestMapping(value="/like/{friendid}/{type}",method= RequestMethod.PUT)
    public Result addFriend(@PathVariable String friendid , @PathVariable String type){
        Claims claims=(Claims)request.getAttribute("user_claims");
        if(claims==null){
            return new Result(false, StatusCode.ACCESSERROR,"无权访问");
        }
        //如果是喜欢
        if(type.equals("1")){
            if(friendService.addFriend(claims.getId(),friendid)==0){
                return new Result(false, StatusCode.REPERROR,"已经添加此好友");
            }
        }else{
            //不喜欢，向不喜欢列表中添加记录
            friendService.addNoFriend(claims.getId(),friendid);
        }
        return new Result(true, StatusCode.OK, "操作成功");
    }

    /**
     * 删除好友
     * @param friendid
     * @return
     */
    @ApiOperation(value = "删除好友")
    @RequestMapping(value="/{friendid}",method=RequestMethod.DELETE)
    public Result remove(@PathVariable String friendid){
        Claims claims=(Claims)request.getAttribute("user_claims");
        if(claims==null){
            return new Result(false, StatusCode.ACCESSERROR,"无权访问");
        }
        friendService.deleteFriend(claims.getId(), friendid);
        return new Result(true, StatusCode.OK, "删除成功");
    }
}