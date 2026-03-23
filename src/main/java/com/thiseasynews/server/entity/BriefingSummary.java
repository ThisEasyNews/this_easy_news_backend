package com.thiseasynews.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "BRIEFING_SUMMARY")
public class BriefingSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /** TYPE = 'BRIEFING' 인 NewsSummary */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BRIEFING_ID", nullable = false)
    private NewsSummary briefing;

    /** TYPE = 'GENERAL' 인 NewsSummary */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUMMARY_ID", nullable = false)
    private NewsSummary summary;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
}
