package net.ukr.lina_chen.beauty_salon_spring_project.entity;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@RequiredArgsConstructor
@Table(name = "archive_appointment")
public class ArchiveAppointment extends Appointment {

    @Column(name = "comment")
    private String comment;

    public ArchiveAppointment(Appointment appointment, String comment) {
        super(appointment.getId(), appointment.getMaster(), appointment.getUser(), appointment.getBeautyService(),
                appointment.getTime(), appointment.getDate(), appointment.isProvided());
        this.comment = comment;
    }
}
