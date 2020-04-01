package net.ukr.lina_chen.beauty_salon_spring_project.controller;

import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Appointment;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Master;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.User;
import net.ukr.lina_chen.beauty_salon_spring_project.service.AppointmentService;
import net.ukr.lina_chen.beauty_salon_spring_project.service.MasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@PreAuthorize("hasAuthority('MASTER')")
@RequestMapping("/master/")
@Controller
public class MasterPagesController {

    private MasterService masterService;
    private AppointmentService appointmentService;

    @Autowired
    public MasterPagesController(MasterService masterService, AppointmentService appointmentService) {
        this.masterService = masterService;
        this.appointmentService = appointmentService;
    }

    @RequestMapping("appointments")
    public String appointmentsPage(Model model, @AuthenticationPrincipal User user, HttpServletRequest request,
                                   @RequestParam("page") Optional<Integer> page,
                                   @RequestParam("size") Optional<Integer> size) {
        Optional<Master> master = masterService.findMasterByUser(user, request);
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(3);
        Page<Appointment> appointments = appointmentService.findAppointmentsForMaster(
                master.get().getId(), PageRequest.of(currentPage - 1, pageSize));
        int totalPages = appointments.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("master", master.get());
        model.addAttribute("appointments", appointments);
        return "master/appointments.html";
    }
}
