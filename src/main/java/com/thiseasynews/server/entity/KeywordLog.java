package com.thiseasynews.server.entity;

import com.thiseasynews.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "KEYWORD_LOG",
       indexes = @Index(name = "idx_keyword_log_date", columnList = "TARGET_DATE, MENTION_COUNT DESC"),
       uniqueConstraints = @UniqueConstraint(name = "uq_keyword_date",
               columnNames = {"KEYWORD_ID", "TARGET_DATE"}))
public class KeywordLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KEYWORD_ID", nullable = false)
    private NewsKeyword keyword;

    @Column(name = "TARGET_DATE", nullable = false)
    private LocalDate targetDate;

    @Column(name = "MENTION_COUNT")
    private Integer mentionCount = 1;
}
