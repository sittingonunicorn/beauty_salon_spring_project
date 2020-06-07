package net.ukr.lina_chen.beauty_salon_spring_project.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "appointment")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "appointment_id")
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id", nullable = false)
    private Master master;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "beautyservice_id", nullable = false)
    private BeautyService beautyService;

    @Column(name = "time")
    private LocalTime time;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "provided")
    private boolean provided;

}
