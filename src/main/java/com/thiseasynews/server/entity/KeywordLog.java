package com.thiseasynews.server.entity;

import com.thiseasynews.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "keyword_log",
       indexes = @Index(name = "idx_keyword_log_date", columnList = "target_date, mention_count DESC"),
       uniqueConstraints = @UniqueConstraint(name = "uq_keyword_date",
               columnNames = {"keyword_id", "target_date"}))
public class KeywordLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private NewsKeyword keyword;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "mention_count")
    private Integer mentionCount = 1;
}
