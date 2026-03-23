package com.thiseasynews.server.repository;

import com.thiseasynews.server.entity.CommonDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommonDetailRepository extends JpaRepository<CommonDetail, String> {

    /**
     * 그룹 ID 기준 활성 항목 조회 (이름 오름차순)
     */
    @Query("SELECT d FROM CommonDetail d " +
           "WHERE d.group.id = :groupId " +
           "AND d.statusCode = 'PUBLISHED' " +
           "ORDER BY d.name ASC")
    List<CommonDetail> findPublishedByGroupId(@Param("groupId") String groupId);
}
