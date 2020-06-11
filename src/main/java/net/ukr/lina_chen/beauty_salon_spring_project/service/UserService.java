package net.ukr.lina_chen.beauty_salon_spring_project.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.dto.UserRegistrationDTO;
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

/**
 * Class uses repository for connection with database to deal with user entity and BCrypt encoder for user password.
 *
 * @author Lina Chentsova
 */
@Slf4j
@Service
public class UserService implements UserDetailsService {
    /**
     * UserRepository bean.
     */
    private final UserRepository userRepository;
    /**
     * BCryptPasswordEncoder bean to code/decode user password.
     */
    private final PasswordEncoder bcryptPasswordEncoder;

    /**
     * Constructor with all args.
     */
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder bcryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bcryptPasswordEncoder = bcryptPasswordEncoder;
    }


    /**
     * Method to save new user to database after registration.
     * @param user - UserRegistrationDTO from registration form.
     */
    public void saveNewUser(UserRegistrationDTO user) {
        User newUser = extractUserFromDto(user);
        userRepository.save(newUser);
    }


    /**
     * Method to convert UserRegistrationDTO from registration form to user entity.
     * @param user  - UserRegistrationDTO from registration form.
     * @return user entity.
     */
    private User extractUserFromDto(UserRegistrationDTO user) {
        return User.builder()
                .email(user.getEmail())
                .name(user.getName())
                .nameUkr(user.getNameUkr())
                .password(bcryptPasswordEncoder.encode(user.getPassword()))
                .role(Role.USER)
                .build();
    }


    /**
     * Method for authentication of User. It gets user data from database by email.
     * @param email - user email.
     * @return UserDetails for Spring Security.
     */
    @Override
    public UserDetails loadUserByUsername(@NonNull String email) {
        Optional<User> optional = userRepository.findByEmail(email);
        return optional.orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));
    }
}
