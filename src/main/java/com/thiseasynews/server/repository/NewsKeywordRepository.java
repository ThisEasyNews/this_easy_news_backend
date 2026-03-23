package com.thiseasynews.server.repository;

import com.thiseasynews.server.entity.NewsKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NewsKeywordRepository extends JpaRepository<NewsKeyword, String> {

    Optional<NewsKeyword> findByIdAndStatusCode(String id, String statusCode);
}
