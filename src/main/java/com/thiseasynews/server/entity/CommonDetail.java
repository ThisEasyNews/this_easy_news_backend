package com.thiseasynews.server.entity;

import com.thiseasynews.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "common_detail")
public class CommonDetail extends BaseTimeEntity {

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private CommonGroup group;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "code_value", length = 5000)
    private String codeValue;

    @Column(name = "status_code", nullable = false, length = 50)
    private String statusCode;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;
}
