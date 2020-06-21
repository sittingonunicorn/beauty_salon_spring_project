package net.ukr.lina_chen.beauty_salon_spring_project.controller;

import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.model.dto.AppointmentDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.model.dto.ArchiveAppointmentDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.model.service.AppointmentService;
import net.ukr.lina_chen.beauty_salon_spring_project.model.service.ArchiveAppointmentService;
import net.ukr.lina_chen.beauty_salon_spring_project.model.service.MasterService;
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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.ukr.lina_chen.beauty_salon_spring_project.controller.IConstants.ADJUSTMENT_FOR_PAGES;
import static net.ukr.lina_chen.beauty_salon_spring_project.controller.IConstants.MIN_QUANTITY_PAGES;


@Slf4j
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/admin/")
@Controller
public class AdminPagesController {

    private final AppointmentService appointmentService;
    private final ArchiveAppointmentService archiveAppointmentService;
    private final MasterService masterService;

    @Autowired
    public AdminPagesController(AppointmentService appointmentService,
                                ArchiveAppointmentService archiveAppointmentService, MasterService masterService) {
        this.appointmentService = appointmentService;
        this.archiveAppointmentService = archiveAppointmentService;
        this.masterService = masterService;
    }


    @GetMapping("archiveappointments")
    public String archiveAppointmentsPage(Model model, @PageableDefault(sort = {"date", "time"},
            direction = Sort.Direction.ASC, size = 7) Pageable pageable,
                                          @RequestParam(value = "master", required = false) String masterId) {

        Page<ArchiveAppointmentDTO> archiveAppointments = masterId != null ?
                archiveAppointmentService.findCommentsForMaster(Long.valueOf(masterId), pageable)
                : archiveAppointmentService.findAllComments(pageable);
        model.addAttribute("masters", masterService.findAll());
        model.addAttribute("archiveAppointments", archiveAppointments);
        model.addAttribute("pageNumbers", this.getPageNumbers(archiveAppointments.getTotalPages()));
        return "admin/archiveappointments.html";
    }

    @GetMapping("appointments")
    public String appointmentsPage(Model model, @PageableDefault(sort = {"date", "time"},
            direction = Sort.Direction.ASC, size = 7) Pageable pageable,
                                   HttpServletRequest request) {
        Page<AppointmentDTO> appointments = appointmentService.findAllUpcomingAppointments(pageable);
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
                                                   direction = Sort.Direction.ASC, size = 6) Pageable pageable,
                                           HttpServletRequest request) {

        model.addAttribute("appointments", appointmentService.findAppointmentsForMaster(masterId, pageable));

        return "admin/appointments.html";
    }
}
