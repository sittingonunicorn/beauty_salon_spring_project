package net.ukr.lina_chen.beauty_salon_spring_project.model.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.model.dto.AppointmentDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.model.dto.ArchiveAppointmentDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.model.entity.ArchiveAppointment;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.AppointmentNotFoundException;
import net.ukr.lina_chen.beauty_salon_spring_project.model.mapper.LocalizedDtoMapper;
import net.ukr.lina_chen.beauty_salon_spring_project.model.repository.ArchiveAppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
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

    public Page<ArchiveAppointmentDTO> findArchiveAppointmentsForUser(@NonNull Long userId, Pageable pageable) {
        return archiveAppointmentRepository.findArchiveAppointmentsByUserId(userId, pageable)
                .map(a -> getLocalizedDTO().map(a));
    }

    public Page<ArchiveAppointmentDTO> findCommentsForMaster(@NonNull Long masterId, Pageable pageable) {
        return archiveAppointmentRepository.findArchiveAppointmentsByMasterIdAndCommentIsNotNull(masterId, pageable)
                .map(a -> getLocalizedDTO().map(a));
    }

    public void addComment(Long appointmentId, String comment) {
        archiveAppointmentRepository.addCommentById(appointmentId, comment);
    }

    public void save(ArchiveAppointment appointment) {
        archiveAppointmentRepository.save(appointment);
    }

    public ArchiveAppointmentDTO findById(Long appointmentId) throws AppointmentNotFoundException {
        return getLocalizedDTO().map(archiveAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId)));
    }

    public Page<ArchiveAppointmentDTO> findAllComments(Pageable pageable) {
        return archiveAppointmentRepository.findArchiveAppointmentsByCommentNotNull(pageable)
                .map(a -> getLocalizedDTO().map(a));
    }

    private LocalizedDtoMapper<ArchiveAppointmentDTO, ArchiveAppointment> getLocalizedDTO() {
        boolean isLocaleEn = LocaleContextHolder.getLocale().equals(Locale.US);
        return appointment -> new ArchiveAppointmentDTO(AppointmentDTO.builder()
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
