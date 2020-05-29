package net.ukr.lina_chen.beauty_salon_spring_project.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProfessionDTO {
    private Long id;
    private String name;
    private String beautyservicesType;
}
