package net.ukr.lina_chen.beauty_salon_spring_project.controller.utility;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({TYPE,ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = PasswordConfirmationValidator.class)
@Documented
public @interface PasswordMatches {
    String message() default "passwordsDontMatch";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
