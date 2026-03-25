package com.thiseasynews.server.entity;

import com.thiseasynews.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "briefing_summary")
public class BriefingSummary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** TYPE = 'BRIEFING' 인 NewsSummary */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "briefing_id", nullable = false)
    private NewsSummary briefing;

    /** TYPE = 'GENERAL' 인 NewsSummary */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "summary_id", nullable = false)
    private NewsSummary summary;
}
