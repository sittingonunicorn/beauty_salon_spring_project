package net.ukr.lina_chen.beauty_salon_spring_project.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.dto.UsersDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Role;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.User;
import net.ukr.lina_chen.beauty_salon_spring_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    private final PasswordEncoder bcryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder bcryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bcryptPasswordEncoder = bcryptPasswordEncoder;
    }

    public Optional<User> findByEmail(User user) {
        //TODO check for user availability. password check
        return userRepository.findByEmail(user.getUsername());
    }

    public UsersDTO getAllUsers() {
        //TODO checking for an empty user list
        return new UsersDTO(userRepository.findAll());
    }

    public void saveNewUser(User user) {
        user.setRole(Role.USER);
        user.setPassword(bcryptPasswordEncoder.encode(user.getPassword()));
        log.info(user.getEmail());
        try {
            userRepository.save(user);
            log.info("User " + user.getEmail() + " is successfully registered.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        Optional<User> optional = userRepository.findByEmail(email);
        User user = optional.orElseGet(User::new);/*.orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found."))*/;
        return user;
    }

    public Optional<User> findById(@NonNull Long id) {
        return userRepository.findById(id);
    }
}
