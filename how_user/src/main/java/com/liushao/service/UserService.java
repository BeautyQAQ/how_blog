package com.liushao.service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.liushao.util.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import com.liushao.dao.UserDao;
import com.liushao.pojo.User;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务层
 * @author huangshen
 */
@Service
@Slf4j
public class UserService {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private KafkaTemplate<String, Map<String, String>> kafkaTemplate;
	@Value("${messaging.sms.topic:sms}")
	private String smsTopic;

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private IdWorker idWorker;

	@Autowired
	private BCryptPasswordEncoder encoder;

	/**
	 * 查询全部列表
	 * @return
	 */
	public List<User> findAll() {
		return userDao.findAll();
	}

	
	/**
	 * 条件查询+分页
	 * @param whereMap
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<User> findSearch(Map<String, String> whereMap, int page, int size) {
		Specification<User> specification = createSpecification(whereMap);
		PageRequest pageRequest =  PageRequest.of(page-1, size);
		return userDao.findAll(specification, pageRequest);
	}

	
	/**
	 * 条件查询
	 * @param whereMap
	 * @return
	 */
	public List<User> findSearch(Map<String, String> whereMap) {
		Specification<User> specification = createSpecification(whereMap);
		return userDao.findAll(specification);
	}

	/**
	 * 根据ID查询实体
	 * @param id
	 * @return
	 */
	public User findById(String id) {
		return userDao.findById(id).orElseThrow(() ->
				new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "用户不存在"));
	}

	/**
	 * 增加
	 * @param user
	 */
	@Transactional
	public void add(User user) {
		user.setMobile(AccountValidation.name(user.getMobile(), 100, "登录标识"));
		user.setNickname(AccountValidation.name(user.getNickname(), 100, "昵称"));
		AccountValidation.password(user.getPassword());
		if (userDao.findByMobile(user.getMobile()) != null)
			throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "登录标识已存在");
		user.setPassword(encoder.encode(user.getPassword()));
		user.setId( idWorker.nextId()+"" );
		user.setRegdate(new Date()); user.setUpdatedate(new Date());
		user.setFanscount(0); user.setFollowcount(0); user.setOnline(0L);
		userDao.save(user);
	}

	/**
	 * 修改
	 * @param user
	 */
	@Transactional
	public void update(User user) {
		User stored = userDao.findById(user.getId()).orElseThrow(() ->
				new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "用户不存在"));
		if (user.getPassword() == null || user.getPassword().isEmpty()) user.setPassword(stored.getPassword());
		else { AccountValidation.password(user.getPassword()); user.setPassword(encoder.encode(user.getPassword())); }
		if (user.getMobile() == null) user.setMobile(stored.getMobile());
		else {
			user.setMobile(AccountValidation.name(user.getMobile(), 100, "登录标识"));
			User duplicate = userDao.findByMobile(user.getMobile());
			if (duplicate != null && !duplicate.getId().equals(stored.getId()))
				throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "登录标识已存在");
		}
		if (user.getNickname() == null) user.setNickname(stored.getNickname());
		else user.setNickname(AccountValidation.name(user.getNickname(), 100, "昵称"));
		user.setRegdate(stored.getRegdate()); user.setUpdatedate(new Date());
		user.setFanscount(stored.getFanscount()); user.setFollowcount(stored.getFollowcount()); user.setOnline(stored.getOnline());
		userDao.save(user);
	}

	/**
	 * 删除
	 * @param id
	 */
	public void deleteById(String id) {
		userDao.deleteById(id);
	}

	/**
	 * 发送短信验证码
	 * @param mobile 手机号
	 */
	public void sendSms(String mobile){
		//1.生成6位短信验证码
		Random random=new Random();
		int max=999999;//最大数
		int min=100000;//最小数
		int code = random.nextInt(max);//随机生成
		if(code<min){
			code=code+min;
		}
		//2.将验证码放入redis  //五分钟过期
		redisTemplate.opsForValue().set("smscode_"+mobile, code+"" ,5, TimeUnit.MINUTES );
		//3.将验证码和手机号发送到 Kafka，以手机号作为 key 保持同一手机号的消息顺序
		Map<String,String> map=new HashMap<>();
		map.put("mobile",mobile);
		map.put("code",code+"");
		try {
			// 等待 broker 确认，发送失败时由接口返回错误。
			kafkaTemplate.send(smsTopic, mobile, map).get(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("短信验证码消息发送被中断", e);
		} catch (ExecutionException | TimeoutException e) {
			throw new IllegalStateException("短信验证码消息发送失败，请稍后重试", e);
		}
	}

	/**
	 * 增加
	 * @param user 用户
	 * @param code 用户填写的验证码
	 */
	public void add(User user,String code) {
		//判断验证码是否正确
		String syscode = (String)redisTemplate.opsForValue().get("smscode_" + user.getMobile());
		//提取系统正确的验证码
		if(syscode==null){
			throw new RuntimeException("请点击获取短信验证码");
		}
		if(!syscode.equals(code)){
			throw new RuntimeException("验证码输入不正确");
		}
		//密码加密
		String newpassword = encoder.encode(user.getPassword());//加密后的密码
		user.setPassword(newpassword);

		user.setId( idWorker.nextId()+"" );
		user.setFollowcount(0);//关注数
		user.setFanscount(0);//粉丝数
		user.setOnline(0L);//在线时长
		user.setRegdate(new Date());//注册日期
		user.setUpdatedate(new Date());//更新日期
		user.setLastdate(new Date());//最后登陆日期
		userDao.save(user);
	}

	/**
	 * 根据手机号和密码查询用户
	 * @param mobile
	 * @param password
	 * @return
	 */
	public User findByMobileAndPassword(String mobile,String password){
		User user = userDao.findByMobile(mobile);
		if(user!=null && password != null && encoder.matches(password,user.getPassword())){
			return user;
		}else{
			return null;
		}
	}

	/**
	 * 更新粉丝数
	 * @param x
	 */
	@Transactional(rollbackFor = Exception.class)
	public void incFanscount(String userid,int x){
		userDao.incFanscount(userid,x);
	}

	/**
	 * 更新关注数
	 * @param x
	 */
	@Transactional(rollbackFor = Exception.class)
	public void incFollowcount(String userid,int x){
		userDao.incFollowcount(userid,x);
	}

	/**
	 * 动态条件构建
	 * @param searchMap
	 * @return
	 */
	private Specification<User> createSpecification(Map<String, String> searchMap) {

		return new Specification<User>() {

			/**
			 * 序列化ID
			 */
			private static final long serialVersionUID = 7549337138079817126L;

			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
                // ID
                if (searchMap.get("id")!=null && !"".equals(searchMap.get("id"))) {
                	predicateList.add(cb.like(root.get("id").as(String.class), "%"+(String)searchMap.get("id")+"%"));
                }
                // 手机号码
                if (searchMap.get("mobile")!=null && !"".equals(searchMap.get("mobile"))) {
                	predicateList.add(cb.like(root.get("mobile").as(String.class), "%"+(String)searchMap.get("mobile")+"%"));
                }
                // 密码
                if (searchMap.get("password")!=null && !"".equals(searchMap.get("password"))) {
                	predicateList.add(cb.like(root.get("password").as(String.class), "%"+(String)searchMap.get("password")+"%"));
                }
                // 昵称
                if (searchMap.get("nickname")!=null && !"".equals(searchMap.get("nickname"))) {
                	predicateList.add(cb.like(root.get("nickname").as(String.class), "%"+(String)searchMap.get("nickname")+"%"));
                }
                // 性别
                if (searchMap.get("sex")!=null && !"".equals(searchMap.get("sex"))) {
                	predicateList.add(cb.like(root.get("sex").as(String.class), "%"+(String)searchMap.get("sex")+"%"));
                }
                // 头像
                if (searchMap.get("avatar")!=null && !"".equals(searchMap.get("avatar"))) {
                	predicateList.add(cb.like(root.get("avatar").as(String.class), "%"+(String)searchMap.get("avatar")+"%"));
                }
                // E-Mail
                if (searchMap.get("email")!=null && !"".equals(searchMap.get("email"))) {
                	predicateList.add(cb.like(root.get("email").as(String.class), "%"+(String)searchMap.get("email")+"%"));
                }
                // 兴趣
                if (searchMap.get("interest")!=null && !"".equals(searchMap.get("interest"))) {
                	predicateList.add(cb.like(root.get("interest").as(String.class), "%"+(String)searchMap.get("interest")+"%"));
                }
                // 个性
                if (searchMap.get("personality")!=null && !"".equals(searchMap.get("personality"))) {
                	predicateList.add(cb.like(root.get("personality").as(String.class), "%"+(String)searchMap.get("personality")+"%"));
                }
				
				return cb.and( predicateList.toArray(new Predicate[predicateList.size()]));

			}
		};

	}

}
