package net.ukr.lina_chen.beauty_salon_spring_project.controller;

import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Master;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Profession;
import net.ukr.lina_chen.beauty_salon_spring_project.service.BeautyServiceImpl;
import net.ukr.lina_chen.beauty_salon_spring_project.service.MasterService;
import net.ukr.lina_chen.beauty_salon_spring_project.service.ProfessionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
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

    @GetMapping("schedule/{master}")
    public String schedulePage(Model model, HttpServletRequest request,
                               @RequestParam(required = false) Long masterId,
                               @PathVariable Master master) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                ResourceBundle.getBundle("messages", RequestContextUtils.getLocale(request)).
                        getString("date.format"));
//        Optional<Master> masterCurr = masterService.findMasterById(master.getId());
        model.addAttribute("days",  Stream.iterate(LocalDate.now(), curr -> curr.plusDays(1)).
                limit(ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.now().plusDays(7))).collect(Collectors.toList()));
        model.addAttribute("time",
                Stream.iterate(master.getTimeBegin(), curr -> curr.plusHours(1)).
                        limit(ChronoUnit.HOURS.between(master.getTimeBegin(), master.getTimeEnd())).
                        collect(Collectors.toList()));
        return "user/schedule.html";
    }


}
