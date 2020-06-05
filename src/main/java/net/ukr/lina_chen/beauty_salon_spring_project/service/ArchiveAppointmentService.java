package net.ukr.lina_chen.beauty_salon_spring_project.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.dto.AppointmentDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.dto.ArchiveAppointmentDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.ArchiveAppointment;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.AppointmentNotFoundException;
import net.ukr.lina_chen.beauty_salon_spring_project.repository.ArchiveAppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

import static net.ukr.lina_chen.beauty_salon_spring_project.controller.IConstants.DATE_FORMAT;

@Slf4j
@Service
public class ArchiveAppointmentService {
    private ArchiveAppointmentRepository archiveAppointmentRepository;

    @Autowired
    public ArchiveAppointmentService(ArchiveAppointmentRepository archiveAppointmentRepository) {
        this.archiveAppointmentRepository = archiveAppointmentRepository;
    }

    public Page<ArchiveAppointmentDTO> findArchiveAppointmentsForUser(@NonNull Long userId, Pageable pageable,
                                                                      boolean isLocaleEn) {
        return archiveAppointmentRepository.findArchiveAppointmentsByUserId(userId, pageable)
                .map(a -> getLocalizedDto(a, isLocaleEn));
    }

    public Page<ArchiveAppointmentDTO> findCommentsForMaster(@NonNull Long masterId, Pageable pageable,
                                                             boolean isLocaleEn) {
        return archiveAppointmentRepository.findArchiveAppointmentsByMasterIdAndCommentIsNotNull(masterId, pageable)
                .map(a -> getLocalizedDto(a, isLocaleEn));
    }

    public void addComment(Long appointmentId, String comment) {
        archiveAppointmentRepository.addCommentById(appointmentId, comment);
    }

    public void save(ArchiveAppointment appointment) {
        archiveAppointmentRepository.save(appointment);
    }

    public ArchiveAppointmentDTO findById(Long appointmentId, boolean isLocaleEn) throws AppointmentNotFoundException {
        return getLocalizedDto(archiveAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(
                        "archive appointment with id " + appointmentId + " not found")), isLocaleEn);
    }

    public Page<ArchiveAppointmentDTO> findAllComments(Pageable pageable, boolean isLocaleEn) {
        return archiveAppointmentRepository.findArchiveAppointmentsByCommentNotNull(pageable)
                .map(a -> getLocalizedDto(a, isLocaleEn));
    }

    private ArchiveAppointmentDTO getLocalizedDto(ArchiveAppointment appointment, boolean isLocaleEn) {
        return new ArchiveAppointmentDTO(AppointmentDTO.builder()
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
                .build(), appointment.getComment());
    }

    private String getLocalizedDate(LocalDate date, boolean isLocaleEn) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages",
                isLocaleEn ? Locale.US : new Locale("ua", "UA"));
        return date.format(DateTimeFormatter.ofPattern(bundle.getString(DATE_FORMAT)));
    }
}
