package net.ukr.lina_chen.beauty_salon_spring_project.controller;

import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.config.LocaleConfig;
import net.ukr.lina_chen.beauty_salon_spring_project.dto.UserDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.User;
import net.ukr.lina_chen.beauty_salon_spring_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class PageController {
    private final UserService userService;
    private LocaleConfig localeConfig;

    @Autowired
    public PageController(UserService userService, LocaleConfig localeConfig) {
        this.userService = userService;
        this.localeConfig = localeConfig;
    }

    @GetMapping({"/index","/"})
    public /*@ResponseBody
    UsersDTO*/ String indexPage() {
        //return userService.getAllUsers();
//        model.addAttribute(userService.getAllUsers());
        return "index.html";
    }

    @RequestMapping("/main")
    public String mainPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("name", user.getName());
       // model.addAttribute("roles", user.getAuthorities().stream().map(Role::getAuthority).collect(joining(",")));
        return "main.html";
    }

    @RequestMapping("/login")
    public String getLogin(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        model.addAttribute("error", error != null);
        model.addAttribute("logout", logout != null);
        return "login.html";
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDTO currentUser;

        try {
            currentUser = (UserDTO) auth.getPrincipal();
        } catch (ClassCastException e) {
            return new User(); //this is likely wrong, there should be a better way to build dummy objects in Spring
        }

        return currentUser.getUser();
    }


}

