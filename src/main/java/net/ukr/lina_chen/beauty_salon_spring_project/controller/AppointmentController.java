package net.ukr.lina_chen.beauty_salon_spring_project.controller;

import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.*;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.AppointmentNotFoundException;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.DoubleTimeRequestException;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.MasterNotFoundException;
import net.ukr.lina_chen.beauty_salon_spring_project.service.AppointmentService;
import net.ukr.lina_chen.beauty_salon_spring_project.service.BeautyServiceImpl;
import net.ukr.lina_chen.beauty_salon_spring_project.service.MasterService;
import net.ukr.lina_chen.beauty_salon_spring_project.service.ServiceTypeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.ukr.lina_chen.beauty_salon_spring_project.controller.IConstants.*;

@Slf4j
@RequestMapping("/user/")
@Controller
@SessionAttributes({"appointment"})
public class AppointmentController {
    private final ServiceTypeService serviceTypeService;
    private final BeautyServiceImpl beautyServiceImpl;
    private final MasterService masterService;
    private final AppointmentService appointmentService;

    public AppointmentController(ServiceTypeService serviceTypeService, BeautyServiceImpl beautyServiceImpl,
                                 MasterService masterService, AppointmentService appointmentService) {
        this.serviceTypeService = serviceTypeService;
        this.beautyServiceImpl = beautyServiceImpl;
        this.masterService = masterService;
        this.appointmentService = appointmentService;
    }

    @ModelAttribute("appointment")
    public Appointment createAppointment() {
        return new Appointment();
    }

    @GetMapping("servicetypes")
    public String mastertypesPage(Model model,
                                  HttpServletRequest request,
                                  @RequestParam(value = ERROR, required = false) String error) {
        model.addAttribute(ERROR, error != null);
        model.addAttribute("servicetypes", serviceTypeService.findAll(
                isLocaleEn(request)));
        return "user/servicetypes.html";
    }

    @GetMapping("beautyservices/{serviceType}")
    public String beautyservicesPage(Model model,
                                     @PathVariable ServiceType serviceType,
                                     HttpServletRequest request) {

        model.addAttribute("beautyservices", beautyServiceImpl.findAllByServiceTypeId(serviceType.getId(),
                isLocaleEn(request)));
        return "user/beautyservices.html";
    }


    @GetMapping("masters/{beautyService}")
    public String mastersPage(Model model,
                              @PathVariable BeautyService beautyService,
                              @ModelAttribute("appointment") Appointment appointment,
                              @AuthenticationPrincipal User user,
                              HttpServletRequest request) throws MasterNotFoundException {
        appointment.setBeautyService(beautyService);
        log.info("Beautyservice is added to appointment: " + appointment.getBeautyService().getName());
        appointment.setUser(user);
        log.info("User is added to appointment: " + appointment.getUser().getName());
        System.out.println(beautyService.getServiceType().getId());
        model.addAttribute("masters", masterService.findAllByServiceType(
                beautyService.getServiceType().getId(), isLocaleEn(request)));
        return "user/masters.html";
    }

    @GetMapping("approve/{masterId}")
    public String approvePage(@PathVariable Long masterId,
                              @ModelAttribute("appointment") Appointment appointment) throws MasterNotFoundException {
        appointment.setMaster(masterService.getMasterAccordingBeautyService(
                masterId, appointment.getBeautyService().getServiceType().getId()));
        log.info("Master is added to appointment: id " + appointment.getMaster().getId());
        return "redirect:time";
    }

    @GetMapping("approve/time")
    public String schedulePage(Model model, HttpServletRequest request,
                               @ModelAttribute(APPOINTMENT) Appointment appointment,
                               @RequestParam(value = ERROR, required = false) String error) {
        model.addAttribute(ERROR, error != null);
        Master master = appointment.getMaster();
        model.addAttribute("master", masterService.getLocalizedDTO(appointment.getMaster(), isLocaleEn(request)));
        Map<String, List<LocalTime>> dateTime = getDateTime(request, master);
        model.addAttribute("workingHours",
                Stream.iterate(master.getTimeBegin(), curr -> curr.plusHours(ITERATE_UNIT)).
                        limit(ChronoUnit.HOURS.between(master.getTimeBegin(), master.getTimeEnd())).
                        collect(Collectors.toList()));
        model.addAttribute("dateTime", dateTime);
        return "user/time.html";
    }


    private Map<String, List<LocalTime>> getDateTime(HttpServletRequest request, Master master) {
        return getScheduleMap(request, master, getDates(master), getMastersBusySchedule(master.getId()));
    }

    private Map<String, List<LocalTime>> getScheduleMap(HttpServletRequest request, Master master,
                                                        List<LocalDate> dates,
                                                        List<LocalDateTime> busyTime) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages",
                RequestContextUtils.getLocale(request));
        Map<String, List<LocalTime>> dateTime = new LinkedHashMap<>();
        LocalDateTime now = LocalDateTime.now();
        for (LocalDate date : dates) {
            List<LocalTime> timeList = Stream.iterate(master.getTimeBegin(), time -> time.plusHours(ITERATE_UNIT))
                    .limit(ChronoUnit.HOURS.between(master.getTimeBegin(), master.getTimeEnd()))
                    .filter(time -> !busyTime.contains(LocalDateTime.of(date, time)))
                    .filter(time -> now.isBefore(LocalDateTime.of(date, time)))
                    .collect(Collectors.toList());
            dateTime.put(date.format(DateTimeFormatter.ofPattern(bundle.getString("date.format"))), timeList);
        }
        return dateTime;
    }

    private List<LocalDate> getDates(Master master) {
        LocalDate startDate = LocalDateTime.now().isBefore(
                LocalDateTime.of(LocalDate.now(), master.getTimeEnd())) ?
                LocalDate.now() : LocalDate.now().plusDays(ITERATE_UNIT);
        return Stream.iterate(startDate, date -> date.plusDays(ITERATE_UNIT))
                .limit(ChronoUnit.DAYS.between(startDate, startDate.plusDays(SCHEDULE_DAYS)))
                .collect(Collectors.toList());
    }

    private List<LocalDateTime> getMastersBusySchedule(Long masterId) {
        List<Appointment> appointments = appointmentService.getMastersBusyTime(masterId);
        return appointments.stream()
                .map(app -> LocalDateTime.of(LocalDate.parse(app.getDate().toString(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        app.getTime()))
                .collect(Collectors.toList());
    }

    @GetMapping("saveappointment")
    public String saveAppointment(@RequestParam(required = false) String day,
                                  @RequestParam(required = false) String seanceTime,
                                  @ModelAttribute(APPOINTMENT) Appointment appointment,
                                  SessionStatus sessionStatus, HttpServletRequest request)
            throws DoubleTimeRequestException {
        ResourceBundle bundle = ResourceBundle.getBundle("messages",
                RequestContextUtils.getLocale(request));
        appointment.setDate(LocalDate.parse(day, DateTimeFormatter.ofPattern(bundle.getString("date.format"))));
        log.info("Date is added to appointment: " + appointment.getDate());
        appointment.setTime(LocalTime.parse(seanceTime));
        log.info("Time is added to appointment: " + appointment.getTime());
        Long id = appointmentService.createAppointment(appointment);
        sessionStatus.setComplete();
        createAppointment();
        return "redirect:appointment/"+id;
    }

    @GetMapping(APPOINTMENT+"/{appointmentId}")
    public String appointment(@PathVariable Long appointmentId, Model model,
                              HttpServletRequest request) throws AppointmentNotFoundException {
        model.addAttribute(APPOINTMENT, appointmentService.getLocalizedAppointmentById(appointmentId,
                isLocaleEn(request)));
        return "user/appointment.html";
    }

    private boolean isLocaleEn(HttpServletRequest request) {
        return RequestContextUtils.getLocale(request).equals(Locale.US);
    }


    @ExceptionHandler(DoubleTimeRequestException.class)
    String handleDoubleTimeRequestException(DoubleTimeRequestException e, Model model) {
        model.addAttribute(ERROR, true);
        return "redirect:/user/approve/time?error";
    }

    @ExceptionHandler(MasterNotFoundException.class)
    public String handleMasterNotFoundException(MasterNotFoundException e, Model model) {
        log.warn(e.getLocalizedMessage());
        model.addAttribute(ERROR, true);
        return "redirect:/user/servicetypes";
    }
}
