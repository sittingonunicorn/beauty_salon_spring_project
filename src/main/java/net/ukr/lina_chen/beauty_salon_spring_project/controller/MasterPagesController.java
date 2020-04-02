package net.ukr.lina_chen.beauty_salon_spring_project.controller;

import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Appointment;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.ArchiveAppointment;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Master;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.User;
import net.ukr.lina_chen.beauty_salon_spring_project.service.AppointmentService;
import net.ukr.lina_chen.beauty_salon_spring_project.service.ArchiveAppointmentService;
import net.ukr.lina_chen.beauty_salon_spring_project.service.MasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    private ArchiveAppointmentService archiveAppointmentService;

    @Autowired
    public MasterPagesController(MasterService masterService, AppointmentService appointmentService,
                                 ArchiveAppointmentService archiveAppointmentService) {
        this.masterService = masterService;
        this.appointmentService = appointmentService;
        this.archiveAppointmentService = archiveAppointmentService;
    }

    @RequestMapping("appointments")
    public String appointmentsPage(Model model, @AuthenticationPrincipal User user, HttpServletRequest request,
                                   @PageableDefault(sort = {"date", "time"},
                                           direction = Sort.Direction.ASC, size = 3) Pageable pageable) {
        Optional<Master> master = masterService.findMasterByUser(user, request);
        Page<Appointment> appointments = appointmentService.findAppointmentsForMaster(
                master.get().getId(), pageable);
        int totalPages = appointments.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(0, totalPages-1)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("master", master.get());
        model.addAttribute("appointments", appointments);
        return "master/appointments.html";
    }

    @Transactional
    @GetMapping("makeprovided")
    public String makeProvided(@RequestParam Long appointmentId) {
        Appointment appointment = appointmentService.findAppointmentById(appointmentId).get();
        appointmentService.setAppointmentProvided(appointmentId);
        ArchiveAppointment archiveAppointment= new ArchiveAppointment(appointment, null);
        archiveAppointmentService.save(archiveAppointment);
        appointmentService.deleteAppointment(appointment);
        return "redirect:/master/appointments";
    }
}
