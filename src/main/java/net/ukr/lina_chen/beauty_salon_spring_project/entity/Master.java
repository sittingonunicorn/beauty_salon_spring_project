package net.ukr.lina_chen.beauty_salon_spring_project.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalTime;

@Data
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

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "profession_id", nullable = false)
    private Profession profession;

    @Column(name = "time_begin", nullable = false)
    private LocalTime timeBegin;

    @Column(name = "time_end", nullable = false)
    private LocalTime timeEnd;

//    @OneToMany( mappedBy = "master",  cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
//    private Set<Comment> comments;


}
