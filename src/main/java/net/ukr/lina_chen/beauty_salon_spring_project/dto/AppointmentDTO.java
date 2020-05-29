package net.ukr.lina_chen.beauty_salon_spring_project.dto;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AppointmentDTO {
    private Long id;
    private String masterName;
    private String userName;
    private String beautyService;
    private LocalTime time;
    private String date;
    private boolean provided;
}
