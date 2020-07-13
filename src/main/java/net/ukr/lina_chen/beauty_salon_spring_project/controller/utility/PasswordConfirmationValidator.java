package net.ukr.lina_chen.beauty_salon_spring_project.controller.utility;

import net.ukr.lina_chen.beauty_salon_spring_project.model.dto.UserRegistrationDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordConfirmationValidator implements ConstraintValidator <PasswordMatches, Object> {

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext context) {
        UserRegistrationDTO user = (UserRegistrationDTO) o;
        return user.getPassword().equals(user.getConfirmPassword());
    }
}
