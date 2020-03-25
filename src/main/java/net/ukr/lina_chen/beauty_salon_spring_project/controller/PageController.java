package net.ukr.lina_chen.beauty_salon_spring_project.controller;

import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Profession;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.User;
import net.ukr.lina_chen.beauty_salon_spring_project.service.BeautyServiceImpl;
import net.ukr.lina_chen.beauty_salon_spring_project.service.ProfessionService;
import net.ukr.lina_chen.beauty_salon_spring_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class PageController {
    //private UserService userService;
    private ProfessionService professionService;
    private BeautyServiceImpl beautyServiceImpl;

    @Autowired
    public PageController(UserService userService, ProfessionService professionService, BeautyServiceImpl beautyServiceImpl) {
      //  this.userService = userService;
        this.professionService = professionService;
        this.beautyServiceImpl = beautyServiceImpl;
    }

    @GetMapping({"/index", "/"})
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
        model.addAttribute("email", user.getEmail());
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

    @GetMapping("/user/servicetypes")
    public String mastertypesPage(Model model, HttpServletRequest request) {
        model.addAttribute("servicetypes", professionService.findAll(request));
        return "user/servicetypes.html";
    }

//    @GetMapping("/user/beautyservices")
//    public String beautyservicesPage(Model model, HttpServletRequest request) {
//        model.addAttribute("beautyservices", beautyServiceImpl.findAll(request));
//        return "user/beautyservices.html";
//    }

    @GetMapping("/user/beautyservices/{profession}")
    public String beautyservicesPage(Model model, HttpServletRequest request,
                                @PathVariable Profession profession) {
        model.addAttribute("url", "/user/beautyservices/{profession}");
        model.addAttribute("beautyservices", beautyServiceImpl.findAllByProfessionId(profession.getId(), request));
        return "/user/beautyservices.html";
    }

//    private User getCurrentUser() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        User currentUser;
//
//        try {
//            currentUser = (User) auth.getPrincipal();
//        } catch (ClassCastException e) {
//            return new User();
//        }
//        return currentUser.getUser();
//    }


}

