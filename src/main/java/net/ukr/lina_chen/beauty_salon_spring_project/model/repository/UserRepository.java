package net.ukr.lina_chen.beauty_salon_spring_project.model.repository;

import lombok.NonNull;
import net.ukr.lina_chen.beauty_salon_spring_project.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(@NonNull String email);

}