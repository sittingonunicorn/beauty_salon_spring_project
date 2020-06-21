package net.ukr.lina_chen.beauty_salon_spring_project.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "masters")
public class Master {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "master_id")
    private Long id;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(name = "master_service_type",
            joinColumns = @JoinColumn(name = "master_id"),
            inverseJoinColumns = @JoinColumn(name = "service_type_id"))
    private Set<ServiceType> serviceTypes;

    @Column(name = "time_begin", nullable = false)
    private LocalTime timeBegin;

    @Column(name = "time_end", nullable = false)
    private LocalTime timeEnd;

}

