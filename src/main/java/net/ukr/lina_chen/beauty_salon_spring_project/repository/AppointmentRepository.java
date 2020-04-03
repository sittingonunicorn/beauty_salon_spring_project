package net.ukr.lina_chen.beauty_salon_spring_project.repository;

import lombok.NonNull;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Page<Appointment> findAppointmentsByMasterIdAndIsProvidedFalse(@NonNull Long masterId, Pageable pageable);

    Page<Appointment> findAppointmentsByUserId(@NonNull Long userId, Pageable pageable);

    Optional<Appointment> findAppointmentByMasterIdAndTimeAndDate(@NonNull Long master_id, LocalTime time, LocalDate date);

    Optional<Appointment> findAppointmentById(@NonNull Long id);

    Page<Appointment> findAppointmentsByIsProvidedFalse(Pageable pageable);


    @Transactional
    @Modifying
    @Query("update Appointment a set a.isProvided=:provided where a.id=:appointmentId")
    void setProvidedById(@NonNull Long appointmentId, @Param("provided") boolean provided);

}
