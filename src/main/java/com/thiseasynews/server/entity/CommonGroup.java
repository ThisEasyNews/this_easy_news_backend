package com.thiseasynews.server.entity;

import com.thiseasynews.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "common_group")
public class CommonGroup extends BaseTimeEntity {

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "status_code", nullable = false, length = 50)
    private String statusCode;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private List<CommonDetail> details = new ArrayList<>();
}
