package net.ukr.lina_chen.beauty_salon_spring_project.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Appointment;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.DoubleTimeRequestException;
import net.ukr.lina_chen.beauty_salon_spring_project.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Slf4j
@Service
public class AppointmentService {

    private AppointmentRepository appointmentRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public Page<Appointment> findAppointmentsForMaster(@NonNull Long masterId, Pageable pageable) {
        return appointmentRepository.findAppointmentsByMasterIdAndIsProvidedFalse(masterId , pageable);
    }

    public List<Appointment> findAllAppointments() {
        return appointmentRepository.findAll();
    }

    public boolean isTimeBusy(Long master_id, LocalTime time, LocalDate date) {
        return appointmentRepository.findAppointmentByMasterIdAndTimeAndDate(master_id, time, date).isPresent();
    }

    @Transactional
    public void createAppointment(Appointment appointment) throws DoubleTimeRequestException {
        if (isTimeBusy(appointment.getMaster().getId(),
                appointment.getTime(), appointment.getDate())) {
            log.warn("The master is busy at this time");
            throw new DoubleTimeRequestException(appointment.getMaster().getUser().getName() + " is busy at this time");
        } else {
            appointmentRepository.save(appointment);
            log.info("The appointment is successfully added to Master's " +
                    appointment.getMaster().getUser().getName() + " schedule");
        }

    }

}
