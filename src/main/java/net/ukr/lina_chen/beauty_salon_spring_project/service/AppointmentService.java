package net.ukr.lina_chen.beauty_salon_spring_project.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Appointment;
import net.ukr.lina_chen.beauty_salon_spring_project.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<Appointment> findAppointmentsForMaster(@NonNull Long masterId) {
        return appointmentRepository.findAppointmentsByMasterId(masterId);
    }

    public boolean isTimeBusy(Long master_id, LocalTime time, LocalDate date) {
        return appointmentRepository.findAppointmentByMasterIdAndTimeAndDate(master_id, time, date).isPresent();
    }


    public void createAppointment(Appointment appointment) {
//        if (isTimeBusy(appointment.getMaster().getId(),
//                appointment.getTime(), appointment.getDate())) {
//            log.warn("The master is busy at this time");
//            //TODO Make an exception
//        } else {
            appointmentRepository.save(appointment);
            log.info("The appointment is successfully added to Master's " +
                    appointment.getMaster().getUser().getName() + " schedule");
//        }

    }

}
