package net.ukr.lina_chen.beauty_salon_spring_project.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Appointment;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.ArchiveAppointment;
import net.ukr.lina_chen.beauty_salon_spring_project.repository.ArchiveAppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ArchiveAppointmentService {
    private ArchiveAppointmentRepository archiveAppointmentRepository;

    @Autowired
    public ArchiveAppointmentService(ArchiveAppointmentRepository archiveAppointmentRepository) {
        this.archiveAppointmentRepository = archiveAppointmentRepository;
    }

    public Page<ArchiveAppointment> findArchiveAppointmentsForUser(@NonNull Long userId, Pageable pageable) {
        return archiveAppointmentRepository.findArchiveAppointmentsByUserId(userId, pageable);
    }

    public void addComment(Long appointmentId, String comment) {
        archiveAppointmentRepository.addCommentById(appointmentId, comment);
    }

    public void save(ArchiveAppointment appointment){
        archiveAppointmentRepository.save(appointment);
    }

    public Optional<ArchiveAppointment> findById(Long appointmentId){
        return archiveAppointmentRepository.findById(appointmentId);
    }
}
