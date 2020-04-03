package net.ukr.lina_chen.beauty_salon_spring_project.controller;

import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Appointment;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.ArchiveAppointment;
import net.ukr.lina_chen.beauty_salon_spring_project.service.AppointmentService;
import net.ukr.lina_chen.beauty_salon_spring_project.service.ArchiveAppointmentService;
import static net.ukr.lina_chen.beauty_salon_spring_project.controller.Iconstants.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Slf4j
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/admin/")
@Controller
public class AdminPagesController {

    private AppointmentService appointmentService;
    private ArchiveAppointmentService archiveAppointmentService;

    @Autowired
    public AdminPagesController(AppointmentService appointmentService,
                                ArchiveAppointmentService archiveAppointmentService) {
        this.appointmentService = appointmentService;
        this.archiveAppointmentService = archiveAppointmentService;
    }


    @GetMapping("archiveappointments")
    public String archiveAppointmentsPage(Model model, @PageableDefault(sort = {"date", "time"},
            direction = Sort.Direction.ASC, size = 3) Pageable pageable) {
        Page<ArchiveAppointment> archiveAppointments = archiveAppointmentService.findAllProvidedAppointments(pageable);
        model.addAttribute("archiveAppointments", archiveAppointments);
        model.addAttribute("pageNumbers", this.getPageNumbers(archiveAppointments.getTotalPages()));
        return "admin/archiveappointments.html";
    }

    @GetMapping("appointments")
    public String appointmentsPage(Model model, @PageableDefault(sort = {"date", "time"},
            direction = Sort.Direction.ASC, size = 3) Pageable pageable) {
        Page<Appointment> appointments = appointmentService.findAllUpcomingAppointments(pageable);
        model.addAttribute("appointments", appointments);
        model.addAttribute("pageNumbers", this.getPageNumbers(appointments.getTotalPages()));
        return "admin/appointments.html";
    }

    private List<Integer> getPageNumbers(int totalPages) {
        List<Integer> pageNumbers = new ArrayList<>();
        if (totalPages > MIN_QUANTITY_PAGES) {
            pageNumbers = IntStream.rangeClosed(MIN_QUANTITY_PAGES, totalPages - ADJUSTMENT_FOR_PAGES)
                    .boxed()
                    .collect(Collectors.toList());
        }
        return pageNumbers;
    }

    @PostMapping("appointments")
    public String appointmentsOfMasterPage(Model model, @RequestParam Long masterId,
                                           @PageableDefault(sort = {"date", "time"},
                                                   direction = Sort.Direction.ASC, size = 3) Pageable pageable) {

        model.addAttribute("appointments", appointmentService.findAppointmentsForMaster(masterId, pageable));
        return "admin/appointments.html";
    }
}
