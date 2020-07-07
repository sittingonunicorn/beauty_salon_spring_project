package net.ukr.lina_chen.beauty_salon_spring_project.controller;

import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.controller.utility.MailService;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.AppointmentNotFoundException;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.MasterNotFoundException;
import net.ukr.lina_chen.beauty_salon_spring_project.model.dto.AppointmentDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.model.dto.ArchiveAppointmentDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.model.dto.MasterDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.model.entity.Appointment;
import net.ukr.lina_chen.beauty_salon_spring_project.model.entity.ArchiveAppointment;
import net.ukr.lina_chen.beauty_salon_spring_project.model.entity.User;
import net.ukr.lina_chen.beauty_salon_spring_project.model.service.AppointmentService;
import net.ukr.lina_chen.beauty_salon_spring_project.model.service.ArchiveAppointmentService;
import net.ukr.lina_chen.beauty_salon_spring_project.model.service.MasterService;
import net.ukr.lina_chen.beauty_salon_spring_project.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.ukr.lina_chen.beauty_salon_spring_project.controller.IConstants.*;

@Slf4j
@PreAuthorize("hasAuthority('MASTER')")
@RequestMapping("/master/")
@Controller
public class MasterPagesController {

    private final MasterService masterService;
    private final UserService userService;
    private final AppointmentService appointmentService;
    private final ArchiveAppointmentService archiveAppointmentService;
    private final MailService mailService;

    @Autowired
    public MasterPagesController(MasterService masterService, UserService userService, AppointmentService appointmentService,
                                 ArchiveAppointmentService archiveAppointmentService, MailService mailService) {
        this.masterService = masterService;
        this.userService = userService;
        this.appointmentService = appointmentService;
        this.archiveAppointmentService = archiveAppointmentService;
        this.mailService = mailService;
    }

    @GetMapping("appointments")
    public String appointmentsPage(Model model, Principal principal,
                                   @PageableDefault(sort = {"date", "time"},
                                           direction = Sort.Direction.ASC, size = 6) Pageable pageable,
                                   @RequestParam(value = "error", required = false) String error,
                                   @RequestParam(value = "date", required = false) String date)
            throws MasterNotFoundException {
        User user = userService.findByEmail(principal.getName());
        MasterDTO master = masterService.findMasterByUser(user);
        Page<AppointmentDTO> appointments = date != null ?
                appointmentService.getMastersDailyAppointments(master.getId(), date, pageable)
                : appointmentService.findAppointmentsForMaster(master.getId(), pageable);
        model.addAttribute("pageNumbers", this.getPageNumbers(appointments.getTotalPages()));
        model.addAttribute("master", master);
        model.addAttribute("appointments", appointments);
        model.addAttribute("error", error != null);
        model.addAttribute("dates", appointmentService.getMastersAppointmentDates(master.getId()));
        return "master/appointments.html";
    }

    @Transactional(rollbackFor = Exception.class)
    @GetMapping("makeprovided")
    public String makeProvided(@RequestParam Long appointmentId) throws AppointmentNotFoundException {
        Appointment appointment = appointmentService.setAppointmentProvided(appointmentId);
        ArchiveAppointment archiveAppointment = new ArchiveAppointment(appointment, null);
        archiveAppointmentService.save(archiveAppointment);
        appointmentService.deleteAppointment(appointment);
        mailService.send(archiveAppointment.getUser().getEmail(), archiveAppointment.getMaster().getId());
        return "redirect:/master/appointments";
    }

    @GetMapping("comments")
    public String archiveAppointmentsPage(Model model, Principal principal,
                                          @PageableDefault(sort = {"date", "time"},
                                                  direction = Sort.Direction.DESC, size = 6) Pageable pageable)
            throws MasterNotFoundException {
        User user = userService.findByEmail(principal.getName());
        MasterDTO master = masterService.findMasterByUser(user);
        Page<ArchiveAppointmentDTO> archiveAppointments =
                archiveAppointmentService.findCommentsForMaster(master.getId(), pageable);
        model.addAttribute("archiveAppointments", archiveAppointments);
        model.addAttribute("pageNumbers", this.getPageNumbers(archiveAppointments.getTotalPages()));
        return "master/comments.html";
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

    @ExceptionHandler(MasterNotFoundException.class)
    public String handleMasterNotFoundException(MasterNotFoundException e, Model model) {
        log.warn(e.getLocalizedMessage());
        model.addAttribute("error", true);
        return "redirect:master/appointments?error";
    }

    @ExceptionHandler(AppointmentNotFoundException.class)
    String handleAppointmentNotFoundException(AppointmentNotFoundException e, Model model) {
        model.addAttribute(ERROR, true);
        return "redirect:/master/appointments";
    }
}
