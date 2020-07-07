package net.ukr.lina_chen.beauty_salon_spring_project.model.dto;

import lombok.*;
import net.ukr.lina_chen.beauty_salon_spring_project.model.entity.BeautyService;
import net.ukr.lina_chen.beauty_salon_spring_project.model.entity.Master;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CreateAppointmentDTO {

    private Master master;
    private BeautyService beautyService;
    private LocalTime time;
    private LocalDate date;
}
