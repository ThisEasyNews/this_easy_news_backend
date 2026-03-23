package com.thiseasynews.server.entity;

import com.thiseasynews.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "COMMON_DETAIL")
public class CommonDetail extends BaseTimeEntity {

    @Id
    @Column(name = "ID", length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID", nullable = false)
    private CommonGroup group;

    @Column(name = "NAME", nullable = false, length = 50)
    private String name;

    @Column(name = "CODE_VALUE", length = 5000)
    private String codeValue;

    @Column(name = "STATUS_CODE", nullable = false, length = 50)
    private String statusCode;

    @Column(name = "START_DATE")
    private LocalDateTime startDate;

    @Column(name = "END_DATE")
    private LocalDateTime endDate;
}
