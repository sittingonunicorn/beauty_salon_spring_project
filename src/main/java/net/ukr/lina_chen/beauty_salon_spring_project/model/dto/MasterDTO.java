package net.ukr.lina_chen.beauty_salon_spring_project.model.dto;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MasterDTO {
    private Long id;
    private String name;
    private LocalTime timeBegin;
    private LocalTime timeEnd;
}
