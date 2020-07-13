package net.ukr.lina_chen.beauty_salon_spring_project.controller;

import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.AppointmentNotFoundException;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.DoubleTimeRequestException;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.MasterNotFoundException;
import net.ukr.lina_chen.beauty_salon_spring_project.model.dto.AppointmentDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.model.dto.CreateAppointmentDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.model.entity.*;
import net.ukr.lina_chen.beauty_salon_spring_project.model.service.AppointmentService;
import net.ukr.lina_chen.beauty_salon_spring_project.model.service.BeautyServiceImpl;
import net.ukr.lina_chen.beauty_salon_spring_project.model.service.MasterService;
import net.ukr.lina_chen.beauty_salon_spring_project.model.service.ServiceTypeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.ukr.lina_chen.beauty_salon_spring_project.controller.utility.IConstants.*;

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
    public CreateAppointmentDTO createAppointment() {
        return new CreateAppointmentDTO();
    }

    @GetMapping("servicetypes")
    public String mastertypesPage(Model model,
                                  @RequestParam(value = ERROR, required = false) String error) {
        model.addAttribute(ERROR, error != null);
        model.addAttribute("servicetypes", serviceTypeService.findAll());
        return "user/servicetypes.html";
    }

    @GetMapping("beautyservices/{serviceType}")
    public String beautyservicesPage(Model model,
                                     @PathVariable ServiceType serviceType) {
        model.addAttribute("beautyservices", beautyServiceImpl.findAllByServiceTypeId(serviceType.getId()));
        return "user/beautyservices.html";
    }


    @GetMapping("masters/{beautyService}")
    public String mastersPage(Model model,
                              @PathVariable BeautyService beautyService,
                              @ModelAttribute("appointment") CreateAppointmentDTO appointment) {
        appointment.setBeautyService(beautyService);
        log.info("Beautyservice is added to appointment: " + appointment.getBeautyService().getName());
        model.addAttribute("masters", masterService.findAllByServiceType(beautyService.getServiceType().getId()));
        return "user/masters.html";
    }

    @GetMapping("approve/{masterId}")
    public String approvePage(@PathVariable Long masterId,
                              @ModelAttribute("appointment") CreateAppointmentDTO appointment)
            throws MasterNotFoundException {
        appointment.setMaster(masterService.getMasterAccordingBeautyService(
                masterId, appointment.getBeautyService().getServiceType().getId()));
        log.info("Master is added to appointment: id " + appointment.getMaster().getId());
        return "redirect:time";
    }

    @GetMapping("approve/time")
    public String schedulePage(Model model, HttpServletRequest request,
                               @ModelAttribute(APPOINTMENT) CreateAppointmentDTO appointment,
                               @RequestParam(value = ERROR, required = false) String error) {
        model.addAttribute(ERROR, error != null);
        Master master = appointment.getMaster();
        model.addAttribute("master", masterService.getLocalizedDTO().map(appointment.getMaster()));
        Map<String, List<LocalTime>> dateTime = appointmentService.getScheduleMap(request, master);
        model.addAttribute("workingHours", masterService.getMastersWorkingHours(master));
        model.addAttribute("dateTime", dateTime);
        return "user/time.html";
    }


    @GetMapping("saveappointment")
    public String saveAppointment(@RequestParam(required = false) String day,
                                  @RequestParam(required = false) String seanceTime,
                                  @ModelAttribute(APPOINTMENT) CreateAppointmentDTO createAppointmentDTO,
                                  SessionStatus sessionStatus, HttpServletRequest request,
                                  @AuthenticationPrincipal User user)
            throws DoubleTimeRequestException {
        ResourceBundle bundle = ResourceBundle.getBundle("messages",
                RequestContextUtils.getLocale(request));
        createAppointmentDTO.setDate(LocalDate.parse(day, DateTimeFormatter.ofPattern(bundle.getString("date.format"))));
        log.info("Date is added to appointment: " + createAppointmentDTO.getDate());
        createAppointmentDTO.setTime(LocalTime.parse(seanceTime));
        log.info("Time is added to appointment: " + createAppointmentDTO.getTime());
        Appointment appointment = appointmentService.createAppointment(createAppointmentDTO, user);
        log.info("User is added to appointment: " + appointment.getUser().getName());
        Long id = appointmentService.saveAppointment(appointment);
        sessionStatus.setComplete();
        return "redirect:appointment/"+id;
    }

    @GetMapping(APPOINTMENT+"/{appointmentId}")
    public String appointment(@PathVariable Long appointmentId, Model model) throws AppointmentNotFoundException {
        model.addAttribute("appointmentDTO", appointmentService.getLocalizedAppointmentById(appointmentId));
        return "user/appointment.html";
    }

    @GetMapping("appointments")
    public String appointmentsPage(Model model, @AuthenticationPrincipal User user,
                                   @PageableDefault(size = 6) @SortDefault.SortDefaults({
                                           @SortDefault(sort = "date", direction = Sort.Direction.DESC),
                                           @SortDefault(sort = "time", direction = Sort.Direction.ASC)
                                   }) Pageable pageable,
                                   @RequestParam(value = ERROR, required = false) String error) {
        Page<AppointmentDTO> appointments = appointmentService.findAppointmentsForUser(
                user.getId(), pageable);
        model.addAttribute("pageNumbers", this.getPageNumbers(appointments.getTotalPages()));
        model.addAttribute("appointments", appointments);
        model.addAttribute("user", user);
        model.addAttribute(ERROR, error != null);
        return "user/appointments.html";
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
        log.warn(e.getLocalizedMessage());
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
