package net.ukr.lina_chen.beauty_salon_spring_project.model.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.AppointmentNotFoundException;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.DoubleTimeRequestException;
import net.ukr.lina_chen.beauty_salon_spring_project.model.dto.AppointmentDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.model.dto.CreateAppointmentDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.model.entity.Appointment;
import net.ukr.lina_chen.beauty_salon_spring_project.model.entity.Master;
import net.ukr.lina_chen.beauty_salon_spring_project.model.entity.User;
import net.ukr.lina_chen.beauty_salon_spring_project.model.mapper.LocalizedDtoMapper;
import net.ukr.lina_chen.beauty_salon_spring_project.model.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
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
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public Page<AppointmentDTO> findAppointmentsForMaster(@NonNull Long masterId, Pageable pageable) {
        return appointmentRepository.findAppointmentsByMasterIdAndProvidedFalseOrderByDate(masterId, pageable)
                .map(a -> getLocalizedDTO().map(a));
    }

    public Page<AppointmentDTO> findAppointmentsForUser(@NonNull Long userId, Pageable pageable) {
        return appointmentRepository.findAppointmentsByUserIdOrderByDate(userId, pageable)
                .map(a -> getLocalizedDTO().map(a));
    }

    public Page<AppointmentDTO> findAllUpcomingAppointments(Pageable pageable) {
        return appointmentRepository.findAppointmentsByProvidedFalseOrderByDate(pageable)
                .map(a -> getLocalizedDTO().map(a));
    }

    public List<String> getMastersAppointmentDates(Long masterId) {
        return appointmentRepository.findMastersAppointmentDates(masterId)
                .stream().map(this::getLocalizedDate)
                .collect(Collectors.toList());
    }

    public Page<AppointmentDTO> getMastersDailyAppointments(Long masterId, String date, Pageable pageable) {
        return appointmentRepository.findAppointmentsByMasterIdAndDate(masterId,
                getLocalizedDate(date), pageable)
                .map(a -> getLocalizedDTO().map(a));
    }

    public List<Appointment> getMastersBusyTime(Long masterId) {
        return appointmentRepository.findAppointmentsByMasterId(masterId);
    }

    private boolean isTimeBusy(Long masterId, LocalTime time, LocalDate date) {
        return appointmentRepository.findAppointmentByMasterIdAndTimeAndDate(masterId, time, date).isPresent();
    }

    @Transactional
    public Long saveAppointment(Appointment appointment) throws DoubleTimeRequestException {
        if (isTimeBusy(appointment.getMaster().getId(),
                appointment.getTime(), appointment.getDate())) {
            throw new DoubleTimeRequestException(appointment.getMaster().getUser().getName());
        } else {
            Appointment created = appointmentRepository.save(appointment);
            log.info("Appointment's added to the schedule of master " + created.getMaster().getUser().getName());
            return created.getId();
        }
    }

    public Appointment createAppointment(CreateAppointmentDTO appointment, User user) {
        return Appointment.builder()
                .beautyService(appointment.getBeautyService())
                .date(appointment.getDate())
                .time(appointment.getTime())
                .master(appointment.getMaster())
                .user(user)
                .build();
    }

    public Appointment findAppointmentById(Long appointmentId) throws AppointmentNotFoundException {
        return appointmentRepository.findAppointmentById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));
    }

    public AppointmentDTO getLocalizedAppointmentById(Long appointmentId)
            throws AppointmentNotFoundException {
        return getLocalizedDTO().map(appointmentRepository.findAppointmentById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId)));
    }

    @Transactional
    public Appointment setAppointmentProvided(Long appointmentId) throws AppointmentNotFoundException {
        appointmentRepository.setProvidedById(appointmentId, true);
        return appointmentRepository.findAppointmentById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));
    }

    public void deleteAppointment(Appointment appointment) {
        appointmentRepository.delete(appointment);
    }

    private LocalizedDtoMapper<AppointmentDTO, Appointment> getLocalizedDTO() {
        boolean isLocaleEn = LocaleContextHolder.getLocale().equals(Locale.US);
        return appointment -> AppointmentDTO.builder()
                .beautyService(isLocaleEn ? appointment.getBeautyService().getName()
                        : appointment.getBeautyService().getNameUkr())
                .masterName(isLocaleEn ?
                        appointment.getMaster().getUser().getName()
                        : appointment.getMaster().getUser().getNameUkr())
                .userName(isLocaleEn ? appointment.getUser().getName()
                        : appointment.getUser().getNameUkr())
                .id(appointment.getId())
                .date(getLocalizedDate(appointment.getDate()))
                .time(appointment.getTime())
                .provided(appointment.isProvided())
                .build();
    }

    private String getLocalizedDate(LocalDate date) {
        boolean isLocaleEn = LocaleContextHolder.getLocale().equals(Locale.US);
        ResourceBundle bundle = ResourceBundle.getBundle("messages",
                isLocaleEn ? Locale.US : new Locale("ua", "UA"));
        return date.format(DateTimeFormatter.ofPattern(bundle.getString(DATE_FORMAT)));
    }

    private LocalDate getLocalizedDate(String date) {
        boolean isLocaleEn = LocaleContextHolder.getLocale().equals(Locale.US);
        ResourceBundle bundle = ResourceBundle.getBundle("messages",
                isLocaleEn ? Locale.US : new Locale("ua", "UA"));
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(bundle.getString(DATE_FORMAT)));
    }

    public Map<String, List<LocalTime>> getScheduleMap(HttpServletRequest request, Master master) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages",
                RequestContextUtils.getLocale(request));
        List<LocalDateTime> busyTime = getMastersBusySchedule(master.getId());
        Map<String, List<LocalTime>> dateTime = new LinkedHashMap<>();
        LocalDateTime now = LocalDateTime.now();
        for (LocalDate date : getScheduleDates(master)) {
            List<LocalTime> timeList = Stream.iterate(master.getTimeBegin(), time -> time.plusHours(ITERATE_UNIT))
                    .limit(ChronoUnit.HOURS.between(master.getTimeBegin(), master.getTimeEnd()))
                    .filter(time -> !busyTime.contains(LocalDateTime.of(date, time)))
                    .filter(time -> now.isBefore(LocalDateTime.of(date, time)))
                    .collect(Collectors.toList());
            dateTime.put(date.format(DateTimeFormatter.ofPattern(bundle.getString("date.format"))), timeList);
        }
        return dateTime;
    }

    private List<LocalDate> getScheduleDates(Master master) {
        LocalDate startDate = LocalDateTime.now().isBefore(
                LocalDateTime.of(LocalDate.now(), master.getTimeEnd())) ?
                LocalDate.now() : LocalDate.now().plusDays(ITERATE_UNIT);
        return Stream.iterate(startDate, date -> date.plusDays(ITERATE_UNIT))
                .limit(ChronoUnit.DAYS.between(startDate, startDate.plusDays(SCHEDULE_DAYS)))
                .collect(Collectors.toList());
    }

    private List<LocalDateTime> getMastersBusySchedule(Long masterId) {
        List<Appointment> appointments = getMastersBusyTime(masterId);
        return appointments.stream()
                .map(app -> LocalDateTime.of(app.getDate(),
                        app.getTime()))
                .collect(Collectors.toList());
    }
}
