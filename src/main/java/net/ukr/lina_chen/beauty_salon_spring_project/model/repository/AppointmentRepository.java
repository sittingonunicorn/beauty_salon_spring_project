package net.ukr.lina_chen.beauty_salon_spring_project.model.repository;

import lombok.NonNull;
import net.ukr.lina_chen.beauty_salon_spring_project.model.entity.Appointment;
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
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Page<Appointment> findAppointmentsByMasterIdAndProvidedFalseOrderByDate(@NonNull Long masterId, Pageable pageable);

    Page<Appointment> findAppointmentsByUserIdAndProvidedOrderByDate(@NonNull Long userId, boolean IsProvided,
                                                                     Pageable pageable);

    Optional<Appointment> findAppointmentByMasterIdAndTimeAndDate(@NonNull Long masterId, LocalTime time, LocalDate date);

    List<Appointment> findAppointmentsByMasterId(@NonNull Long masterId);

    Optional<Appointment> findAppointmentById(@NonNull Long id);

    Page<Appointment> findAppointmentsByProvidedFalseOrderByDate(Pageable pageable);

    @Query("select distinct a.date from Appointment a where a.master.id = :masterId and a.provided=false order by a.date")
    List<LocalDate> findMastersAppointmentDates(@NonNull Long masterId);

    Page<Appointment> findAppointmentsByMasterIdAndDate(Long masterId, LocalDate date, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update Appointment a set a.provided=:provided where a.id=:appointmentId")
    void setProvidedById(@NonNull Long appointmentId, @Param("provided") boolean provided);

}
