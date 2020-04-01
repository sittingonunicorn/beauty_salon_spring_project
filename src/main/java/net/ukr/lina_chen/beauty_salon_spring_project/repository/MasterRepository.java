package net.ukr.lina_chen.beauty_salon_spring_project.repository;

import lombok.NonNull;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Master;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasterRepository extends JpaRepository<Master, Long> {
    List<Master> findAllByProfessionIdAndLanguageCode(Long profession_id, String languageCode);
    Optional<Master> findById(Long id);
    public Optional <Master> findMasterByUserAndLanguageCode(@NonNull User user, String languageCode);
}
