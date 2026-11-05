package com.liushao.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.liushao.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


import com.liushao.dao.AdminDao;
import com.liushao.pojo.Admin;

/**
 * 服务层
 * 
 * @author Administrator
 *
 */
@Service
public class AdminService {

	@Autowired
	private AdminDao adminDao;
	
	@Autowired
	private IdWorker idWorker;

	@Autowired
	private BCryptPasswordEncoder encoder;

	/**
	 * 查询全部列表
	 * @return
	 */
	public List<Admin> findAll() {
		return adminDao.findAll();
	}

	
	/**
	 * 条件查询+分页
	 * @param whereMap
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<Admin> findSearch(Map<String, String> whereMap, int page, int size) {
		Specification<Admin> specification = createSpecification(whereMap);
		PageRequest pageRequest =  PageRequest.of(page-1, size);
		return adminDao.findAll(specification, pageRequest);
	}

	
	/**
	 * 条件查询
	 * @param whereMap
	 * @return
	 */
	public List<Admin> findSearch(Map<String, String> whereMap) {
		Specification<Admin> specification = createSpecification(whereMap);
		return adminDao.findAll(specification);
	}

	/**
	 * 根据ID查询实体
	 * @param id
	 * @return
	 */
	public Admin findById(String id) {
		return adminDao.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "管理员不存在"));
	}

	/**
	 * 增加
	 * @param admin
	 */
	@Transactional
	public void add(Admin admin) {
		admin.setLoginname(AccountValidation.name(admin.getLoginname(), 100, "登录名"));
		AccountValidation.password(admin.getPassword());
		if (adminDao.findByLoginname(admin.getLoginname()) != null)
			throw new ResponseStatusException(HttpStatus.CONFLICT, "登录名已存在");
		admin.setState(state(admin.getState()));
		admin.setId(idWorker.nextId()+""); //主键值
		//密码加密
		String newpassword = encoder.encode(admin.getPassword());//加密后的密码
		admin.setPassword(newpassword);
		adminDao.save(admin);
	}

	/**
	 * 修改
	 * @param admin
	 */
	@Transactional
	public void update(Admin admin) {
		Admin stored = adminDao.findById(admin.getId()).orElseThrow(() ->
				new ResponseStatusException(HttpStatus.NOT_FOUND, "管理员不存在"));
		if (admin.getLoginname() != null) {
			String name = AccountValidation.name(admin.getLoginname(), 100, "登录名");
			Admin duplicate = adminDao.findByLoginname(name);
			if (duplicate != null && !duplicate.getId().equals(stored.getId()))
				throw new ResponseStatusException(HttpStatus.CONFLICT, "登录名已存在");
			stored.setLoginname(name);
		}
		if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
			AccountValidation.password(admin.getPassword());
			stored.setPassword(encoder.encode(admin.getPassword()));
		}
		if (admin.getState() != null) stored.setState(state(admin.getState()));
		adminDao.save(stored);
	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void bootstrap(String loginname, String password) {
		if (adminDao.count() != 0) throw new IllegalStateException("已存在管理员，拒绝重复初始化");
		Admin admin = new Admin(); admin.setLoginname(loginname); admin.setPassword(password); admin.setState("1");
		add(admin);
	}

	private String state(String state) {
		if (state == null) return "1";
		if (!"0".equals(state) && !"1".equals(state)) throw new IllegalArgumentException("状态只能是0或1");
		return state;
	}

	/**
	 * 删除
	 * @param id
	 */
	public void deleteById(String id) {
		adminDao.deleteById(id);
	}

	/**
	 * 根据登陆名和密码查询
	 * @param loginname
	 * @param password
	 * @return
	 */
	public Admin findByLoginnameAndPassword(String loginname, String password){
		Admin admin = adminDao.findByLoginname(loginname);
		if( admin!=null && "1".equals(admin.getState()) && password != null && encoder.matches(password,admin.getPassword())) {
			return admin;
		}else{
			return null;
		}
	}

	/**
	 * 动态条件构建
	 * @param searchMap
	 * @return
	 */
	private Specification<Admin> createSpecification(Map<String, String> searchMap) {

		return new Specification<Admin>() {

			/**
			 * 序列化ID
			 */
			private static final long serialVersionUID = -7055703182105883285L;

			@Override
			public Predicate toPredicate(Root<Admin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
                // ID
                if (searchMap.get("id")!=null && !"".equals(searchMap.get("id"))) {
                	predicateList.add(cb.like(root.get("id").as(String.class), "%"+(String)searchMap.get("id")+"%"));
                }
                // 登陆名称
                if (searchMap.get("loginname")!=null && !"".equals(searchMap.get("loginname"))) {
                	predicateList.add(cb.like(root.get("loginname").as(String.class), "%"+(String)searchMap.get("loginname")+"%"));
                }
                // 密码
                if (searchMap.get("password")!=null && !"".equals(searchMap.get("password"))) {
                	predicateList.add(cb.like(root.get("password").as(String.class), "%"+(String)searchMap.get("password")+"%"));
                }
                // 状态
                if (searchMap.get("state")!=null && !"".equals(searchMap.get("state"))) {
                	predicateList.add(cb.like(root.get("state").as(String.class), "%"+(String)searchMap.get("state")+"%"));
                }
				
				return cb.and( predicateList.toArray(new Predicate[predicateList.size()]));

			}
		};

	}

}
