package net.ukr.lina_chen.beauty_salon_spring_project.controller;

import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Appointment;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Master;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Profession;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.User;
import net.ukr.lina_chen.beauty_salon_spring_project.service.BeautyServiceImpl;
import net.ukr.lina_chen.beauty_salon_spring_project.service.MasterService;
import net.ukr.lina_chen.beauty_salon_spring_project.service.ProfessionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequestMapping("/user/")
@Controller
public class UserPagesController {
    private ProfessionService professionService;
    private BeautyServiceImpl beautyServiceImpl;
    private MasterService masterService;

    public UserPagesController(ProfessionService professionService, BeautyServiceImpl beautyServiceImpl, MasterService masterService) {
        this.professionService = professionService;
        this.beautyServiceImpl = beautyServiceImpl;
        this.masterService = masterService;
    }


    @GetMapping("servicetypes")
    public String mastertypesPage(Model model, HttpServletRequest request) {
        model.addAttribute("servicetypes", professionService.findAll(request));
        return "user/servicetypes.html";
    }

    @GetMapping("beautyservices/{profession}")
    public String beautyservicesPage(Model model, HttpServletRequest request,
                                     @PathVariable Profession profession) {
        //  model.addAttribute("url", "/user/beautyservices/{profession}");
        model.addAttribute("beautyservices", beautyServiceImpl.findAllByProfessionId(profession.getId(), request));
        return "user/beautyservices.html";
    }

    @GetMapping("masters/{profession}")
    public String mastersPage(Model model, HttpServletRequest request,
                              @PathVariable Profession profession) {
        model.addAttribute("url", "/user/masters/{profession}");
        model.addAttribute("masters", masterService.findAllByProfessionId(profession.getId(), request));
        return "user/masters.html";
    }

    @GetMapping("time/{master}")
    public String schedulePage(Model model, HttpServletRequest request,
                               @PathVariable Master master,
                               @ModelAttribute Appointment appointment) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
//                ResourceBundle.getBundle("messages", RequestContextUtils.getLocale(request)).
//                        getString("date.format"));
        appointment.setMaster(master);
        model.addAttribute("days", Stream.iterate(LocalDate.now(), curr -> curr.plusDays(1)).
                limit(ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.now().plusDays(7))).collect(Collectors.toList()));
        model.addAttribute("time",
                Stream.iterate(master.getTimeBegin(), curr -> curr.plusHours(1)).
                        limit(ChronoUnit.HOURS.between(master.getTimeBegin(), master.getTimeEnd())).
                        collect(Collectors.toList()));
        return "user/time.html";
    }

    @GetMapping("saveappointment")
    public String saveAppointment(Model model, @RequestParam(required = false) LocalDate day,
                                  @RequestParam(required = false) LocalTime time,
                                  @ModelAttribute Appointment appointment, User user) {

        appointment.setDate(day);
        appointment.setTime(time);
        appointment.setUser(user);
        log.info(day.toString());
        return "user/appointment.html";
    }

    @GetMapping("appointment")
    public String appointment() {
        return "user/appointment.html";
    }
}