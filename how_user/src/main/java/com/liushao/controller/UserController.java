package com.liushao.controller;
import java.util.HashMap;
import java.util.Map;

import com.liushao.entity.PageResult;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.liushao.pojo.User;
import com.liushao.service.UserService;

import javax.servlet.http.HttpServletRequest;


/**
 * 控制器层
 * 前缀 /user
 * @author Administrator
 *
 */
@Api(tags = "User用户模块")
@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private JwtUtil jwtUtil;
	
	
	/**
	 * 查询全部数据
	 */
	@ApiOperation(value = "查询全部数据")
	@RequestMapping(method= RequestMethod.GET)
	public Result findAll(){
		return new Result(true, StatusCode.OK,"查询成功",userService.findAll());
	}
	
	/**
	 * 根据ID查询
	 * @param id ID
	 */
	@ApiOperation(value = "根据ID查询")
	@RequestMapping(value="/{id}",method= RequestMethod.GET)
	public Result findById(@PathVariable String id){
		return new Result(true,StatusCode.OK,"查询成功",userService.findById(id));
	}


	/**
	 * 分页+多条件查询
	 * @param searchMap 查询条件封装
	 * @param page 页码
	 * @param size 页大小
	 * @return 分页结果
	 */
	@ApiOperation(value = "分页+条件查询")
	@RequestMapping(value="/search/{page}/{size}",method=RequestMethod.POST)
	public Result findSearch(@RequestBody Map<String, String> searchMap , @PathVariable int page, @PathVariable int size){
		Page<User> pageList = userService.findSearch(searchMap, page, size);
		return  new Result(true,StatusCode.OK,"查询成功",  new PageResult<User>(pageList.getTotalElements(), pageList.getContent()) );
	}

	/**
     * 根据条件查询
     * @param searchMap 查询条件
     */
	@ApiOperation(value = "条件查询")
    @RequestMapping(value="/search",method = RequestMethod.POST)
    public Result findSearch( @RequestBody Map<String, String> searchMap){
        return new Result(true,StatusCode.OK,"查询成功",userService.findSearch(searchMap));
    }
	
	/**
	 * 增加
	 * @param user 用户
	 */
	@ApiOperation(value = "增加用户")
	@RequestMapping(method=RequestMethod.POST)
	public Result add(@RequestBody User user  ){
		userService.add(user);
		return new Result(true,StatusCode.OK,"增加成功");
	}
	
	/**
	 * 修改
	 * @param user 用户
	 */
	@ApiOperation(value = "修改用户")
	@RequestMapping(value="/{id}",method= RequestMethod.PUT)
	public Result update(@RequestBody User user, @PathVariable String id ){
		user.setId(id);
		userService.update(user);		
		return new Result(true,StatusCode.OK,"修改成功");
	}
	
	/**
	 * 删除
	 * @param id userid
	 */
	@ApiOperation(value = "删除用户")
	@RequestMapping(value="/{id}",method= RequestMethod.DELETE)
	public Result delete(@PathVariable String id){
		Claims claims=(Claims) request.getAttribute("admin_claims");
		if(claims==null){
			return new Result(true,StatusCode.ACCESSERROR,"无权访问");
		}
		userService.deleteById(id);
		return new Result(true,StatusCode.OK,"删除成功");
	}

	/**
	 * 发送短信验证码
	 * @param mobile 手机号码
	 */
	@ApiOperation(value = "给指定手机号发送短信验证码")
	@RequestMapping(value="/sendsms/{mobile}",method=RequestMethod.POST)
	public Result sendsms(@PathVariable String mobile ){
		userService.sendSms(mobile);
		return new Result(true,StatusCode.OK,"发送成功");
	}

	/**
	 * 用户注册
	 * @param user 用户
	 */
	@ApiOperation(value = "用户注册")
	@RequestMapping(value="/register/{code}",method=RequestMethod.POST)
	public Result register( @RequestBody User user ,@PathVariable String code){
		userService.add(user,code);
		return new Result(true,StatusCode.OK,"注册成功");
	}

	/**
	 * 用户登陆
	 */
	@ApiOperation(value = "登录")
	@RequestMapping(value="/login",method=RequestMethod.POST)
	public Result login(@RequestBody Map<String,String> loginMap){
		User user = userService.findByMobileAndPassword(loginMap.get("mobile"),loginMap.get("password"));
		if(user!=null){
			String token = jwtUtil.createJWT(user.getId(), user.getNickname(), "user");
			Map<String, Object> map=new HashMap<>();
			map.put("token","Bearer "+token);
			//昵称
			map.put("name",user.getNickname());
			//头像
			map.put("avatar",user.getAvatar());
			return new Result(true,StatusCode.OK,"登陆成功",map);
		}else{
			return new Result(false,StatusCode.LOGINERROR,"用户名或密码错误");
		}
	}

	/**
	 * 增加粉丝数
	 * @param userid userid
	 * @param x 数量
	 */
	@ApiOperation(value = "增加粉丝数")
	@RequestMapping(value="/incfans/{userid}/{x}",method=RequestMethod.POST)
	public void incFanscount(@PathVariable String userid,@PathVariable int x){
		userService.incFanscount(userid,x);
	}

	/**
	 * 增加关注数
	 * @param userid userid
	 * @param x 数量
	 */
	@ApiOperation(value = "增加关注数量")
	@RequestMapping(value="/incfollow/{userid}/{x}",method=RequestMethod.POST)
	public void incFollowcount(@PathVariable String userid,@PathVariable int x){
		userService.incFollowcount(userid,x);
	}

	/**
	 * 根据ID查询用户名
	 * @param id ID
	 */
	@ApiOperation(value = "根据ID查询用户名")
	@RequestMapping(value="/nickname/{id}",method= RequestMethod.GET)
	public Result findNicknameById(@PathVariable String id){
		return new Result(true,StatusCode.OK,"查询成功",userService.findById(id).getNickname());
	}
}
