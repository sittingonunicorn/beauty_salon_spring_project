package net.ukr.lina_chen.beauty_salon_spring_project.repository;

import lombok.NonNull;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAppointmentsByMasterId(@NonNull Long masterId);

    Optional<Appointment> findAppointmentByMasterIdAndTimeAndDate(Long master_id, LocalTime time, LocalDate date);

}