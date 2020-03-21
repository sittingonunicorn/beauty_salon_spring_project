package net.ukr.lina_chen.beauty_salon_spring_project.entity;


import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@Builder
@Entity
@Data
@Table(name = "schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "schedule_id")
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "master_id", nullable = false)
    private Master master;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "beautyservice_id", nullable = false)
    private BeautyService beautyService;

    @Column(name = "time")
    private LocalTime time;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "provided")
    private boolean isProvided;

}
