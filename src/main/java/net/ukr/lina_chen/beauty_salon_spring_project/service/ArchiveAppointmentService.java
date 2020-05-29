package net.ukr.lina_chen.beauty_salon_spring_project.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.ArchiveAppointment;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.AppointmentNotFoundException;
import net.ukr.lina_chen.beauty_salon_spring_project.repository.ArchiveAppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public Page<ArchiveAppointment> findCommentsForMaster(@NonNull Long masterId, Pageable pageable) {
        return archiveAppointmentRepository.findArchiveAppointmentsByMasterIdAndCommentIsNotNull(masterId, pageable);
    }

    public void addComment(Long appointmentId, String comment) {
        archiveAppointmentRepository.addCommentById(appointmentId, comment);
    }

    public void save(ArchiveAppointment appointment){
        archiveAppointmentRepository.save(appointment);
    }

    public ArchiveAppointment findById(Long appointmentId) throws AppointmentNotFoundException {
        return archiveAppointmentRepository.findById(appointmentId)
                .orElseThrow(()-> new AppointmentNotFoundException(
                        "archive appointment with id " + appointmentId + " not found"));
    }

    public Page<ArchiveAppointment> findAllProvidedAppointments(Pageable pageable) {
        return archiveAppointmentRepository.findAll(pageable);
    }
}
