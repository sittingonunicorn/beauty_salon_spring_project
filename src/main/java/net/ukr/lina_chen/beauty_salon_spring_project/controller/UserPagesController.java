package net.ukr.lina_chen.beauty_salon_spring_project.controller;

import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.*;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.DoubleTimeRequestException;
import net.ukr.lina_chen.beauty_salon_spring_project.service.AppointmentService;
import net.ukr.lina_chen.beauty_salon_spring_project.service.BeautyServiceImpl;
import net.ukr.lina_chen.beauty_salon_spring_project.service.MasterService;
import net.ukr.lina_chen.beauty_salon_spring_project.service.ProfessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequestMapping("/user/")
@Controller
@SessionAttributes({"appointment"})
public class UserPagesController {
    private ProfessionService professionService;
    private BeautyServiceImpl beautyServiceImpl;
    private MasterService masterService;
    private AppointmentService appointmentService;
    @Autowired
    private MessageSource messageSource;

    public UserPagesController(ProfessionService professionService, BeautyServiceImpl beautyServiceImpl, MasterService masterService, AppointmentService appointmentService) {
        this.professionService = professionService;
        this.beautyServiceImpl = beautyServiceImpl;
        this.masterService = masterService;
        this.appointmentService = appointmentService;
    }

    @ModelAttribute("appointment")
    public Appointment createAppointment() {
        return new Appointment();
    }

    @GetMapping("servicetypes")
    public String mastertypesPage(Model model, HttpServletRequest request) {
        model.addAttribute("servicetypes", professionService.findAll(request));
        return "user/servicetypes.html";
    }

    @GetMapping("beautyservices/{profession}")
    public String beautyservicesPage(Model model, HttpServletRequest request,
                                     @PathVariable Profession profession) {
        model.addAttribute("beautyservices", beautyServiceImpl.findAllByProfessionId(profession.getId(), request));
        return "user/beautyservices.html";
    }

    @GetMapping("masters/{beautyService}")
    public String mastersPage(Model model, HttpServletRequest request,
                              @PathVariable BeautyService beautyService,
                              @ModelAttribute("appointment") Appointment appointment,
                              @AuthenticationPrincipal User user) {
        appointment.setBeautyService(beautyService);
        log.info("Beautyservice is added to appointment: " + appointment.getBeautyService().getName());
        appointment.setUser(user);
        log.info("User is added to appointment: " + appointment.getUser().getName());
        model.addAttribute("masters", masterService.findAllByProfessionId(beautyService.getProfession().getId(), request));
        return "user/masters.html";
    }

    @GetMapping("approve/{master}")
    public String approvePage(@PathVariable Master master,
                              @ModelAttribute("appointment") Appointment appointment) {
        appointment.setMaster(master);
        log.info("Master is added to appointment: id " + appointment.getMaster().getId());
        return "redirect:time";
    }

    @GetMapping("approve/time")
    public String schedulePage(Model model, HttpServletRequest request,
                               @ModelAttribute("appointment") Appointment appointment,
                               @RequestParam(value = "failed", required = false) String failed, Locale locale) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
//                ResourceBundle.getBundle("messages", RequestContextUtils.getLocale(request)).
//                        getString("date.format"));
        model.addAttribute("failed", messageSource.getMessage("message.master.busy", null, locale));
        Master master = appointment.getMaster();
        model.addAttribute("master", master);
        model.addAttribute("days", Stream.iterate(LocalDate.now(), curr -> curr.plusDays(1))
                .limit(ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.now().plusDays(7)))
                .collect(Collectors.toList()));
        model.addAttribute("time",
                Stream.iterate(master.getTimeBegin(), curr -> curr.plusHours(1)).
                        limit(ChronoUnit.HOURS.between(master.getTimeBegin(), master.getTimeEnd())).
                        collect(Collectors.toList()));
        return "user/time.html";
    }


    @GetMapping("saveappointment")
    public String saveAppointment(@RequestParam(required = false) String day,
                                  @RequestParam(required = false) String seanceTime,
                                  @ModelAttribute("appointment") Appointment appointment,
                                  Model model) {

        appointment.setDate(LocalDate.parse(day));
        log.info("Date is added to appointment: " + appointment.getDate());
        appointment.setTime(LocalTime.parse(seanceTime));
        log.info("Time is added to appointment: " + appointment.getTime());
        try {
            appointmentService.createAppointment(appointment);
        } catch (DoubleTimeRequestException e) {
            model.addAttribute("master", appointment.getMaster().getId());
            model.addAttribute("failed", true);
            return "redirect:approve/time";
        }
        return "user/appointment.html";
    }

    @GetMapping("appointment")
    public String appointment(@ModelAttribute("appointment") Appointment appointment, Model model) {
        model.addAttribute("appointment", appointment);
        return "user/appointment.html";
    }
}
