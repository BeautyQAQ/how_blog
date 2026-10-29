package com.liushao.service;

import com.liushao.dao.SpitDao;
import com.liushao.pojo.Spit;
import com.liushao.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class SpitService {

    @Autowired
    private SpitDao spitDao;
    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部记录
     * @return
     */
    public List<Spit> findAll(){
        return spitDao.findAll();
    }
    /**
     * 根据主键查询实体
     * @param id
     * @return
     */
    public Spit findById(String id){
        Spit spit = spitDao.findById(id).get();
        return spit;
    }
    /**
     * 增加
     * @param spit
     */
    @Transactional
    public void add(Spit spit) {
        spit.set_id( idWorker.nextId()+"" );
        spit.setPublishtime(new Date());//发布日期
        spit.setVisits(0);//浏览量
        spit.setShare(0);//分享数
        spit.setThumbup(0);//点赞数
        spit.setComment(0);//回复数
        spit.setState("1");//状态
        if(spit.getParentid()!=null && !"".equals(spit.getParentid())){
            //如果存在上级ID,评论
            spitDao.incrementComment(spit.getParentid());
        }
        spitDao.save(spit);
    }
    /**
    * 修改
    * @param spit
    */
    @Transactional
    public void update(Spit spit) {
        spitDao.save(spit);
    }
    /**
     * 删除
     * @param id
     */
    @Transactional
    public void deleteById(String id) {
        spitDao.deleteExistingById(id);
    }

    /**
     * 根据上级ID查询吐槽列表
     * @param parentid
     * @param page
     * @param size
     * @return
     */
    public Page<Spit> findByParentid(String parentid, int page, int size){
        PageRequest pageRequest = PageRequest.of(page-1, size);
        return spitDao.findByParentid(parentid, pageRequest);
    }

    /**
     * 点赞
     * @param id
     */
    @Transactional
    public void updateThumbup(String id){
        spitDao.incrementThumbup(id);
    }
}
