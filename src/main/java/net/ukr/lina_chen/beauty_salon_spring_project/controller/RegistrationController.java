package net.ukr.lina_chen.beauty_salon_spring_project.controller;

import net.ukr.lina_chen.beauty_salon_spring_project.entity.User;
import net.ukr.lina_chen.beauty_salon_spring_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Locale;
@Controller
public class RegistrationController {

    @Autowired
    private MessageSource messageSource;
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration.html";
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model, Locale locale) {
        if (((User)userService.loadUserByUsername(user.getEmail())).getEmail() != null) {
            model.addAttribute("emailError", messageSource.getMessage("email.not.unique", null, locale));
            return "registration.html";
        }
        userService.saveNewUser(user);
        return "redirect:/login";
    }
}
