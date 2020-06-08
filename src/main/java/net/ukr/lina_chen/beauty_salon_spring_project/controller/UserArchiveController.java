package net.ukr.lina_chen.beauty_salon_spring_project.controller;

import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.dto.ArchiveAppointmentDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.User;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.AppointmentNotFoundException;
import net.ukr.lina_chen.beauty_salon_spring_project.service.ArchiveAppointmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.ukr.lina_chen.beauty_salon_spring_project.controller.IConstants.*;


@Slf4j
@RequestMapping("/user/")
@Controller
public class UserArchiveController {
    private final ArchiveAppointmentService archiveAppointmentService;

    public UserArchiveController(ArchiveAppointmentService archiveAppointmentService) {
        this.archiveAppointmentService = archiveAppointmentService;
    }

    @GetMapping("archiveappointments")
    public String appointmentsPage(Model model, @AuthenticationPrincipal User user, HttpServletRequest request,
                                   @PageableDefault(sort = {"date", "time"}, direction = Sort.Direction.DESC,
                                           size = 6) Pageable pageable,
                                   @RequestParam(value = ERROR, required = false) String error) {
        Page<ArchiveAppointmentDTO> archiveAppointments = archiveAppointmentService.findArchiveAppointmentsForUser(
                user.getId(), pageable, isLocaleEn(request));
        model.addAttribute("pageNumbers", this.getPageNumbers(archiveAppointments.getTotalPages()));
        model.addAttribute("archiveAppointments", archiveAppointments);
        model.addAttribute("user", user);
        model.addAttribute(ERROR, error != null);
        return "user/archiveappointments.html";
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

    @GetMapping("comment")
    public String leaveComment(@RequestParam Long appointmentId, Model model, HttpServletRequest request)
            throws AppointmentNotFoundException {
        model.addAttribute(APPOINTMENT, archiveAppointmentService.findById(appointmentId, isLocaleEn(request)));
        return "user/comment.html";
    }

    private boolean isLocaleEn(HttpServletRequest request) {
        return RequestContextUtils.getLocale(request).equals(Locale.US);
    }
    @PostMapping("comment")
    public String submitComment(@RequestParam Long appointmentId, @RequestParam String comment, Model model,
                                HttpServletRequest request)
            throws AppointmentNotFoundException {
        archiveAppointmentService.addComment(appointmentId, comment);
        model.addAttribute(APPOINTMENT, archiveAppointmentService.findById(appointmentId, isLocaleEn(request)));
        return "redirect:archiveappointments";
    }

    @ExceptionHandler(AppointmentNotFoundException.class)
    String handleAppointmentNotFoundException(AppointmentNotFoundException e, Model model) {
        model.addAttribute(ERROR, true);
        return "redirect:/user/archiveappointments?error";
    }
}
