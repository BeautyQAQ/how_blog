package com.liushao.dao;

import com.liushao.pojo.Spit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpitDao extends JpaRepository<Spit, String> {
    /**
     * 根据上级ID查询吐槽列表（分页）
     * @param parentid
     * @param pageable
     * @return
     */
    Page<Spit> findByParentid(String parentid, Pageable pageable);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Spit s set s.comment = coalesce(s.comment, 0) + 1 where s._id = :id")
    int incrementComment(@Param("id") String id);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Spit s set s.thumbup = coalesce(s.thumbup, 0) + 1 where s._id = :id")
    int incrementThumbup(@Param("id") String id);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Spit s where s._id = :id")
    int deleteExistingById(@Param("id") String id);
}
