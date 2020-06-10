package net.ukr.lina_chen.beauty_salon_spring_project.repository;

import net.ukr.lina_chen.beauty_salon_spring_project.entity.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {

}

