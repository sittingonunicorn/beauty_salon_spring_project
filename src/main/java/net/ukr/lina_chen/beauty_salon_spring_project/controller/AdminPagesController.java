package net.ukr.lina_chen.beauty_salon_spring_project.controller;

import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Appointment;
import net.ukr.lina_chen.beauty_salon_spring_project.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/admin/")
@Controller
public class AdminPagesController {

    private AppointmentService appointmentService;

    @Autowired
    public AdminPagesController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }


    @RequestMapping("appointments")
    public String appointmentsPage(Model model) {
        model.addAttribute("appointments", appointmentService.findAllAppointments());
        return "admin/appointments.html";
    }
}
