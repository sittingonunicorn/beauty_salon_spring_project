package net.ukr.lina_chen.beauty_salon_spring_project.model.repository;

import lombok.NonNull;
import net.ukr.lina_chen.beauty_salon_spring_project.model.entity.Master;
import net.ukr.lina_chen.beauty_salon_spring_project.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasterRepository extends JpaRepository<Master, Long> {

    List<Master> findAllByServiceTypesId(Long serviceType);

    Optional<Master> findById(Long id);

    Optional<Master> findMasterByUser(@NonNull User user);

    Optional<Master> findByIdAndServiceTypesId(Long masterId, Long serviceType);
}
