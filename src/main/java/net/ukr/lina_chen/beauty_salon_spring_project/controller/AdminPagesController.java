package net.ukr.lina_chen.beauty_salon_spring_project.controller;

import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.service.AppointmentService;
import net.ukr.lina_chen.beauty_salon_spring_project.service.MasterService;
import net.ukr.lina_chen.beauty_salon_spring_project.service.ProfessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/admin/")
@Controller
public class AdminPagesController {

    private AppointmentService appointmentService;
    private ProfessionService professionService;
    private MasterService masterService;

    @Autowired
    public AdminPagesController(AppointmentService appointmentService, ProfessionService professionService, MasterService masterService) {
        this.appointmentService = appointmentService;
        this.professionService = professionService;
        this.masterService = masterService;
    }


    @GetMapping("appointments")
    public String appointmentsPage(Model model,  @PageableDefault(sort = {"date", "time"},
            direction = Sort.Direction.ASC, size = 3) Pageable pageable) {
        model.addAttribute("appointments", appointmentService.findAllAppointments(pageable));
        return "admin/appointments.html";
    }

    @PostMapping("appointments")
    public String appointmentsOfMasterPage(Model model, @RequestParam Long masterId,
                                           @PageableDefault(sort = {"date", "time"},
                                                   direction = Sort.Direction.ASC, size = 3) Pageable pageable) {

        model.addAttribute("appointments", appointmentService.findAppointmentsForMaster(masterId, pageable));
        return "admin/appointments.html";
    }
}
