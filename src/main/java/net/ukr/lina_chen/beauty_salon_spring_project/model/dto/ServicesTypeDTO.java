package net.ukr.lina_chen.beauty_salon_spring_project.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ServicesTypeDTO {
    private Long id;
    private String beautyservicesType;
}
