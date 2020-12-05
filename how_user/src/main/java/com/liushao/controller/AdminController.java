package com.liushao.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.liushao.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Claims;

import com.liushao.pojo.Admin;
import com.liushao.service.AdminService;
import com.liushao.entity.PageResult;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
/**
 * 控制器层
 * @author Administrator
 *
 */
@Api(tags = "Admin管理员模块")
@RestController
@CrossOrigin
@RequestMapping("/user/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private JwtUtil jwtUtil;

	/**
	 * 查询全部数据
	 */
	@ApiOperation(value = "查询全部管理员数据")
	@RequestMapping(method= RequestMethod.GET)
	public Result findAll(){
		return new Result(true,StatusCode.OK,"查询成功",adminService.findAll());
	}
	
	/**
	 * 根据ID查询
	 * @param id ID
	 */
	@ApiOperation(value = "根据id查询数据")
	@RequestMapping(value="/{id}",method= RequestMethod.GET)
	public Result findById(@PathVariable String id){
		return new Result(true,StatusCode.OK,"查询成功",adminService.findById(id));
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
	public Result findSearch(@RequestBody Map searchMap , @PathVariable int page, @PathVariable int size){
		Page<Admin> pageList = adminService.findSearch(searchMap, page, size);
		return  new Result(true,StatusCode.OK,"查询成功",  new PageResult<Admin>(pageList.getTotalElements(), pageList.getContent()) );
	}

	/**
     * 根据条件查询
     * @param searchMap 查询对象
     */
	@ApiOperation(value = "条件查询")
    @RequestMapping(value="/search",method = RequestMethod.POST)
    public Result findSearch( @RequestBody Map searchMap){
        return new Result(true,StatusCode.OK,"查询成功",adminService.findSearch(searchMap));
    }
	
	/**
	 * 增加
	 * @param admin 管理员
	 */
	@ApiOperation(value = "增加管理员")
	@RequestMapping(method=RequestMethod.POST)
	public Result add(@RequestBody Admin admin  ){
		adminService.add(admin);
		return new Result(true,StatusCode.OK,"增加成功");
	}
	
	/**
	 * 修改
	 * @param admin 修改管理员
	 */
	@ApiOperation(value = "修改管理员")
	@RequestMapping(value="/{id}",method= RequestMethod.PUT)
	public Result update(@RequestBody Admin admin, @PathVariable String id ){
		admin.setId(id);
		adminService.update(admin);		
		return new Result(true,StatusCode.OK,"修改成功");
	}
	
	/**
	 * 删除
	 * @param id 管理员ID
	 */
	@ApiOperation(value = "删除管理员")
	@RequestMapping(value="/{id}",method= RequestMethod.DELETE)
	public Result delete(@PathVariable String id ){
		adminService.deleteById(id);
		return new Result(true,StatusCode.OK,"删除成功");
	}

	/**
	 * 管理员用户登陆
	 */
	@ApiOperation(value = "管理员登录")
	@RequestMapping(value="/login",method=RequestMethod.POST)
	public Result login(@RequestBody Map<String,String> loginMap){
		Admin admin = adminService.findByLoginnameAndPassword(loginMap.get("loginname"), loginMap.get("password"));
		if(admin!=null){
			//生成token
			String token = jwtUtil.createJWT(admin.getId(), admin.getLoginname(), "admin");
			Map<String, Object> map=new HashMap<>();
			map.put("token","Bearer "+token);
			//登陆名
			map.put("name",admin.getLoginname());
			return new Result(true,StatusCode.OK,"登陆成功",map);
		}else{
			return new Result(false,StatusCode.LOGINERROR,"用户名或密码错误");
		}
	}

	/**
	 * 获取管理员信息
	 * @param request 请求
	 * @return result
	 */
	@ApiOperation(value = "获取管理员信息")
	@RequestMapping(value="/info",method=RequestMethod.GET)
	public Result adminInfo(HttpServletRequest request) {
		String token = request.getHeader("Authorization");	
		Claims claims = jwtUtil.parseJWT(token);	
		Map<String, Object> map=new HashMap<>();
		map.put("avatar","");
		//角色
		map.put("roles",claims.get("roles"));
		return new Result(true,StatusCode.OK,"登陆成功",map);
	}

}
