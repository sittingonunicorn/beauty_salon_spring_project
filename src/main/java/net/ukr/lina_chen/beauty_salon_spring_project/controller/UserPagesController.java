package net.ukr.lina_chen.beauty_salon_spring_project.controller;

import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.*;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.AppointmentNotFoundException;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.DoubleTimeRequestException;
import net.ukr.lina_chen.beauty_salon_spring_project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static net.ukr.lina_chen.beauty_salon_spring_project.controller.IConstants.*;


@Slf4j
@RequestMapping("/user/")
@Controller
@SessionAttributes({"appointment"})
public class UserPagesController {
    private final ProfessionService professionService;
    private final BeautyServiceImpl beautyServiceImpl;
    private final MasterService masterService;
    private final AppointmentService appointmentService;
    private final ArchiveAppointmentService archiveAppointmentService;

    @Autowired
    private MessageSource messageSource;

    public UserPagesController(ProfessionService professionService, BeautyServiceImpl beautyServiceImpl, MasterService masterService, AppointmentService appointmentService, ArchiveAppointmentService archiveAppointmentService) {
        this.professionService = professionService;
        this.beautyServiceImpl = beautyServiceImpl;
        this.masterService = masterService;
        this.appointmentService = appointmentService;
        this.archiveAppointmentService = archiveAppointmentService;
    }

    @ModelAttribute("appointment")
    public Appointment createAppointment() {
        return new Appointment();
    }

    @GetMapping("servicetypes")
    public String mastertypesPage(Model model,
                                  HttpServletRequest request) {
        model.addAttribute("servicetypes", professionService.findAll(
                isLocaleEn(request)));
        return "user/servicetypes.html";
    }


    @GetMapping("beautyservices/{profession}")
    public String beautyservicesPage(Model model,
                                     @PathVariable Profession profession,
                                     HttpServletRequest request) {

        model.addAttribute("beautyservices", beautyServiceImpl.findAllByProfessionId(profession.getId(),
                isLocaleEn(request)));
        return "user/beautyservices.html";
    }

    @GetMapping("masters/{beautyService}")
    public String mastersPage(Model model,
                              @PathVariable BeautyService beautyService,
                              @ModelAttribute("appointment") Appointment appointment,
                              @AuthenticationPrincipal User user,
                              HttpServletRequest request) {
        appointment.setBeautyService(beautyService);
        log.info("Beautyservice is added to appointment: " + appointment.getBeautyService().getName());
        appointment.setUser(user);
        log.info("User is added to appointment: " + appointment.getUser().getName());
        model.addAttribute("masters", masterService.findAllByProfessionId(
                beautyService.getProfession().getId(), isLocaleEn(request)));
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
                               @ModelAttribute(APPOINTMENT) Appointment appointment,
                               @RequestParam(value = ERROR, required = false) String error) {
        model.addAttribute(ERROR, error != null);
        Master master = appointment.getMaster();
        model.addAttribute("master", masterService.getLocalizedDTO(appointment.getMaster(), isLocaleEn(request)));
        Map<LocalDate, List<LocalTime>> dateTime =getDateTime(request, master);
        model.addAttribute("workingHours",
                Stream.iterate(master.getTimeBegin(), curr -> curr.plusHours(ITERATE_UNIT)).
                        limit(ChronoUnit.HOURS.between(master.getTimeBegin(), master.getTimeEnd())).
                        collect(Collectors.toList()));
        model.addAttribute("dateTime", dateTime);
        return "user/time.html";
    }

    @GetMapping("saveappointment")
    public String saveAppointment(@RequestParam(required = false) String day,
                                  @RequestParam(required = false) String seanceTime,
                                  @ModelAttribute(APPOINTMENT) Appointment appointment,
                                  SessionStatus sessionStatus)
            throws DoubleTimeRequestException {
        appointment.setDate(LocalDate.parse(day));
        log.info("Date is added to appointment: " + appointment.getDate());
        appointment.setTime(LocalTime.parse(seanceTime));
        log.info("Time is added to appointment: " + appointment.getTime());
        appointmentService.createAppointment(appointment);
        sessionStatus.setComplete();
        createAppointment();
        return "user/appointment.html";
    }

    @GetMapping("appointment")
    public String appointment(@ModelAttribute(APPOINTMENT) Appointment appointment, Model model) {
        model.addAttribute("appointment", appointment);
        return "user/appointment.html";
    }


    @GetMapping("archiveappointments")
    public String appointmentsPage(Model model, @AuthenticationPrincipal User user, HttpServletRequest request,
                                   @PageableDefault(sort = {"date", "time"},
                                           direction = Sort.Direction.ASC, size = 6) Pageable pageable,
                                   @RequestParam(value = ERROR, required = false) String error) {
        Page<ArchiveAppointment> archiveAppointments = archiveAppointmentService.findArchiveAppointmentsForUser(
                user.getId(), pageable);
        model.addAttribute("pageNumbers", this.getPageNumbers(archiveAppointments.getTotalPages()));
        model.addAttribute("user", user);
        model.addAttribute(ERROR, error != null);
        model.addAttribute("archiveAppointments", archiveAppointments);
        return "user/archiveappointments.html";
    }

    @GetMapping("comment")
    public String leaveComment(@RequestParam Long appointmentId, Model model)
            throws AppointmentNotFoundException {
        model.addAttribute("appointment", archiveAppointmentService.findById(appointmentId));
        return "user/comment.html";
    }

    @PostMapping("comment")
    public String submitComment(@RequestParam Long appointmentId, Model model, @RequestParam String comment)
            throws AppointmentNotFoundException {
        archiveAppointmentService.addComment(appointmentId, comment);
        model.addAttribute("appointment", archiveAppointmentService.findById(appointmentId));
        return "redirect:archiveappointments";
    }

    private boolean isLocaleEn(HttpServletRequest request) {
        return RequestContextUtils.getLocale(request).equals(Locale.US);
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

    @ExceptionHandler(DoubleTimeRequestException.class)
    String handleDoubleTimeRequestException(DoubleTimeRequestException e, Model model) {
        model.addAttribute(ERROR, true);
        return "redirect:approve/time?error";
    }

    @ExceptionHandler(AppointmentNotFoundException.class)
    String handleAppointmentNotFoundException(AppointmentNotFoundException e, Model model) {
        model.addAttribute(ERROR, true);
        return "redirect:archiveappointments?error";
    }

    private Map<LocalDate, List<LocalTime>> getDateTime(HttpServletRequest request, Master master) {
        Long masterId = master.getId();
        List<LocalDate> dates = getDates(master);
        List<LocalDateTime> busyTime = getMastersBusySchedule(request, masterId);
        return getScheduleMap(master, dates, busyTime);
    }

    private Map<LocalDate, List<LocalTime>> getScheduleMap(Master master, List<LocalDate> dates,
                                                           List<LocalDateTime> busyTime) {
        Map<LocalDate, List<LocalTime>> dateTime = new LinkedHashMap<>();
        LocalDateTime now = LocalDateTime.now();
        for (LocalDate date : dates) {
            List<LocalTime> timeList = Stream.iterate(master.getTimeBegin(), time -> time.plusHours(ITERATE_UNIT))
                    .limit(ChronoUnit.HOURS.between(master.getTimeBegin(), master.getTimeEnd()))
                    .filter(time -> !busyTime.contains(LocalDateTime.of(date, time)))
                    .filter(time -> now.isBefore(LocalDateTime.of(date, time)))
                    .collect(Collectors.toList());
            dateTime.put(date, timeList);
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

    private List<LocalDateTime> getMastersBusySchedule(HttpServletRequest request, Long masterId) {
//        ResourceBundle bundle = ResourceBundle.getBundle("messages",
//                RequestContextUtils.getLocale(request));
        List<Appointment> appointments = appointmentService.busyTime(masterId);
        return appointments.stream()
                .map(app -> LocalDateTime.of(LocalDate.parse(app.getDate().toString(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        app.getTime()))
                .collect(Collectors.toList());
    }
}
