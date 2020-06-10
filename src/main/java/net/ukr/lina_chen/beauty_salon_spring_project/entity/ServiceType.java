package net.ukr.lina_chen.beauty_salon_spring_project.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
@Setter
@Table(name = "service_type")
public class ServiceType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "service_type_id")
    private Long id;

    @ManyToMany(mappedBy = "serviceTypes")
    private Set<Master> masters;

    @OneToMany( mappedBy = "serviceType",  cascade = CascadeType.MERGE)
    private Set<BeautyService> beautyServices;

    @Column(name = "beautyservices_type_en", nullable = false)
    private String beautyservicesType;

    @Column(name = "beautyservices_type_ua", nullable = false)
    private String beautyservicesTypeUkr;
}
