package net.ukr.lina_chen.beauty_salon_spring_project.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "professions")
public class Profession {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "profession_id")
    private Long id;

    @Column(name = "profession_name", nullable = false)
    private String name;

    @OneToMany( mappedBy = "profession",  cascade = CascadeType.MERGE)
    private Set<Master> masters;

//    @OneToMany( mappedBy = "profession",  cascade = CascadeType.MERGE)
//    private Set<Procedure> procedures;

}
