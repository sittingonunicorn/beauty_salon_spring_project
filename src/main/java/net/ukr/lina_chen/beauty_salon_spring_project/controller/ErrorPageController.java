package net.ukr.lina_chen.beauty_salon_spring_project.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.GetMapping;

public class ErrorPageController implements ErrorController {

    @GetMapping("/error")
    public String getErrorPage(){
        return "error.html";
    }

    @GetMapping("/error_page")
    public String redirectErrorPage(){
        return "redirect:/error";
    }

    @Override
    public String getErrorPath() {
        return "/error_page";
    }
}
