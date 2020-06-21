package net.ukr.lina_chen.beauty_salon_spring_project.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ArchiveAppointmentDTO extends AppointmentDTO {
    private String comment;

    public ArchiveAppointmentDTO(AppointmentDTO appointment, String comment) {
        super(appointment.getId(), appointment.getMasterName(), appointment.getUserName(), appointment.getBeautyService(),
                appointment.getTime(), appointment.getDate(), appointment.isProvided());
        this.comment = comment;
    }
}
