package net.ukr.lina_chen.beauty_salon_spring_project.repository;

import lombok.NonNull;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.ArchiveAppointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ArchiveAppointmentRepository extends JpaRepository<ArchiveAppointment, Long> {

    Page<ArchiveAppointment> findArchiveAppointmentsByUserId(@NonNull Long userId, Pageable pageable);

    Page<ArchiveAppointment> findArchiveAppointmentsByMasterIdAndCommentIsNotNull(@NonNull Long masterId,
                                                                                  Pageable pageable);
    Optional <ArchiveAppointment> findById(@NonNull Long appointmentId);

    Page<ArchiveAppointment> findArchiveAppointmentsByCommentNotNull(Pageable pageable);

    @Transactional
    @Modifying
    @Query("update ArchiveAppointment a set a.comment=:comment where a.id=:appointmentId")
    void addCommentById(@NonNull Long appointmentId, @Param("comment") String comment);

}
