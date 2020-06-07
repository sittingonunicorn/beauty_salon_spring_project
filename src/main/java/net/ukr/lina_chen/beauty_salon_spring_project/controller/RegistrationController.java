package net.ukr.lina_chen.beauty_salon_spring_project.controller;

import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.dto.UserRegistrationDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.User;
import net.ukr.lina_chen.beauty_salon_spring_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Controller
public class RegistrationController {

    private final MessageSource messageSource;
    private final UserService userService;

    @Autowired
    public RegistrationController(MessageSource messageSource, UserService userService) {
        this.messageSource = messageSource;
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userRegistrationDTO", new UserRegistrationDTO());
        return "registration.html";
    }

    @PostMapping("/registration")
    public String addUser(@Valid @ModelAttribute UserRegistrationDTO userRegistrationDTO, Model model,
                          Locale locale, HttpServletRequest request) throws BindException {
        User user = (User) userService.loadUserByUsername(userRegistrationDTO.getEmail());
        if (Optional.ofNullable(user.getEmail()).isPresent()) {
            model.addAttribute("emailError", messageSource.getMessage("email.not.unique", null, locale));
            return "registration.html";
        }
        try {
            userService.saveNewUser(userRegistrationDTO);
        } catch (DataAccessException| SQLException e) {
            log.info(e.getLocalizedMessage());
        }
        return "redirect:/login";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public String handleValidationExceptions(BindException ex, Locale locale,
                                             Model model) {
        Set<String> errors = new HashSet<>();
        ex.getBindingResult().getAllErrors().forEach(error ->
                errors.add(messageSource.getMessage(error.getDefaultMessage(), null, locale)));
        model.addAttribute("errors", errors);
        return "registration.html";
    }
}
