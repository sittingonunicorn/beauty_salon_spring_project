package net.ukr.lina_chen.beauty_salon_spring_project.repository;

import net.ukr.lina_chen.beauty_salon_spring_project.entity.Profession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfessionRepository extends JpaRepository<Profession, Long> {
    List<Profession> findAll();

}
