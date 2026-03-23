package com.thiseasynews.server.entity;

import com.thiseasynews.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "NEWS_KEYWORD")
public class NewsKeyword extends BaseTimeEntity {

    @Id
    @Column(name = "ID", length = 50)
    private String id;

    @Column(name = "KEYWORD", nullable = false, unique = true, length = 50)
    private String keyword;

    @Column(name = "STATUS_CODE", nullable = false, length = 50)
    private String statusCode;

    @Column(name = "START_DATE")
    private LocalDateTime startDate;

    @Column(name = "END_DATE")
    private LocalDateTime endDate;
}
