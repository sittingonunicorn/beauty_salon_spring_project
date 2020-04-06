package net.ukr.lina_chen.beauty_salon_spring_project.repository;

import lombok.NonNull;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.BeautyService;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Profession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeautyServiceRepository extends JpaRepository<BeautyService, Long> {
    Optional<BeautyService> findById(@NonNull Long id);

    List<BeautyService> findAllByProfessionIdAndLanguageCodeOrderByNameAsc(@NonNull Long professionId, @NonNull String languageCode);

    List<BeautyService> findAllByLanguageCode(@NonNull String languageCode);
}
