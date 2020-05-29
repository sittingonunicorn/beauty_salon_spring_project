package net.ukr.lina_chen.beauty_salon_spring_project.dto;

import lombok.Data;
import net.ukr.lina_chen.beauty_salon_spring_project.controller.utility.PasswordMatches;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

import static net.ukr.lina_chen.beauty_salon_spring_project.controller.utility.RegexContainer.*;

@Data
@PasswordMatches
public class UserRegistrationDTO {

    @Email(message = "wrongEmailFormat")
    private String email;

    @Pattern(regexp = PASSWORD_REGEX, message = "wrongPasswordFormat")
    private String password;

    @Pattern(regexp = PASSWORD_REGEX, message = "wrongPasswordFormat")
    private String confirmPassword;

    @Pattern(regexp = NAME_REGEX_LAT, message = "wrongNameFormat")
    private String name;

    @Pattern(regexp = NAME_REGEX_UA, message = "wrongNameUkrFormat")
    private String nameUkr;
}
