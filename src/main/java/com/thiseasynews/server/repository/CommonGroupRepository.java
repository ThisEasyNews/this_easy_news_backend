package com.thiseasynews.server.repository;

import com.thiseasynews.server.entity.CommonGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommonGroupRepository extends JpaRepository<CommonGroup, String> {

    Optional<CommonGroup> findByIdAndStatusCode(String id, String statusCode);
}
