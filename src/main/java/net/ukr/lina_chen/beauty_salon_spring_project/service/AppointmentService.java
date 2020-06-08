package net.ukr.lina_chen.beauty_salon_spring_project.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.dto.AppointmentDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Appointment;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.AppointmentNotFoundException;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.DoubleTimeRequestException;
import net.ukr.lina_chen.beauty_salon_spring_project.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static net.ukr.lina_chen.beauty_salon_spring_project.controller.IConstants.DATE_FORMAT;


@Slf4j
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public Page<AppointmentDTO> findAppointmentsForMaster(@NonNull Long masterId, Pageable pageable,
                                                          boolean isLocaleEn) {
        return appointmentRepository.findAppointmentsByMasterIdAndProvidedFalseOrderByDate(masterId, pageable)
                .map(a -> getLocalizedDto(isLocaleEn, a));
    }

    public Page<AppointmentDTO> findAppointmentsForUser(@NonNull Long userId, Pageable pageable,
                                                        boolean isLocaleEn) {
        return appointmentRepository.findAppointmentsByUserIdOrderByDate(userId, pageable)
                .map(a -> getLocalizedDto(isLocaleEn, a));
    }

    public Page<AppointmentDTO> findAllUpcomingAppointments(Pageable pageable,
                                                            boolean isLocaleEn) {
        return appointmentRepository.findAppointmentsByProvidedFalseOrderByDate(pageable)
                .map(a -> getLocalizedDto(isLocaleEn, a));
    }

    public List<LocalDate> getMastersAppointmentDates (Long masterId){
        return appointmentRepository.findMastersAppointmentDates(masterId);
    }

    public Page<AppointmentDTO> getMastersDailyAppointments(Long masterId, LocalDate date, Pageable pageable,
                                                            boolean isLocaleEn){
        return appointmentRepository.findAppointmentsByMasterIdAndDate(masterId,date,pageable)
                .map(a->getLocalizedDto(isLocaleEn, a));
    }

    public List<Appointment> busyTime(Long masterId) {
        return appointmentRepository.findAppointmentsByMasterId(masterId);
    }

    private boolean isTimeBusy(Long masterId, LocalTime time, LocalDate date) {
        return appointmentRepository.findAppointmentByMasterIdAndTimeAndDate(masterId, time, date).isPresent();
    }

    @Transactional
    public Appointment createAppointment(Appointment appointment) throws DoubleTimeRequestException {
        if (isTimeBusy(appointment.getMaster().getId(),
                appointment.getTime(), appointment.getDate())) {
            log.warn("The master is busy at this time");
            throw new DoubleTimeRequestException(appointment.getMaster().getUser().getName() + " is busy at this time");
        } else {
            return appointmentRepository.save(appointment);
        }
    }

    public Appointment findAppointmentById(Long id) throws AppointmentNotFoundException {
        return appointmentRepository.findAppointmentById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("appointment with id " + id + " not found"));
    }

    public AppointmentDTO getLocalizedAppointmentById(Long id, boolean isLocaleEn) throws AppointmentNotFoundException {
        return getLocalizedDto(isLocaleEn, appointmentRepository.findAppointmentById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("appointment with id " + id + " not found")));
    }

    public void setAppointmentProvided(Long appointmentId) {
        appointmentRepository.setProvidedById(appointmentId, true);
    }

    public void deleteAppointment(Appointment appointment) {
        appointmentRepository.delete(appointment);
    }

    private AppointmentDTO getLocalizedDto(boolean isLocaleEn, Appointment appointment) {
        return AppointmentDTO.builder()
                .beautyService(isLocaleEn ? appointment.getBeautyService().getName()
                        : appointment.getBeautyService().getNameUkr())
                .masterName(isLocaleEn ?
                        appointment.getMaster().getUser().getName()
                        : appointment.getMaster().getUser().getNameUkr())
                .userName(isLocaleEn ? appointment.getUser().getName()
                        : appointment.getUser().getNameUkr())
                .id(appointment.getId())
                .date(getLocalizedDate(appointment.getDate(), isLocaleEn))
                .time(appointment.getTime())
                .provided(appointment.isProvided())
                .build();
    }

    private String getLocalizedDate(LocalDate date, boolean isLocaleEn) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages",
                isLocaleEn ? Locale.US : new Locale("ua", "UA"));
        return date.format(DateTimeFormatter.ofPattern(bundle.getString(DATE_FORMAT)));
    }
}
