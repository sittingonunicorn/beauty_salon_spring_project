package net.ukr.lina_chen.beauty_salon_spring_project.entity;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "archive_appointment")
public class ArchiveAppointment extends Appointment {

    @Column(name = "comment")
    private String comment;
}
