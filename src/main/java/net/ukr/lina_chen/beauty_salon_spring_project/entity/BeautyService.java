package net.ukr.lina_chen.beauty_salon_spring_project.entity;


import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "beautyservices")
public class BeautyService {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "beautyservice_id")
    private Long id;

    @Column(name = "beautyservice_name_en", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "profession_id", nullable = false)
    private Profession profession;

    @Column(name = "beautyservice_name_ua", nullable = false)
    private String nameUkr;

}
