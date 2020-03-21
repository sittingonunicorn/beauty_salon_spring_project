package net.ukr.lina_chen.beauty_salon_spring_project.dto;

import lombok.*;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.User;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UsersDTO {
    private List<User> users;
}
