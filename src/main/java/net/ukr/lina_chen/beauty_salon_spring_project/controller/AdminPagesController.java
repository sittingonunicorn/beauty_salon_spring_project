package net.ukr.lina_chen.beauty_salon_spring_project.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequestMapping("/admin/")
@Controller
public class AdminPagesController {

    @RequestMapping("appointments")
    public String appointmentsPage (){

        return "admin/appointments.html";
    }
}
