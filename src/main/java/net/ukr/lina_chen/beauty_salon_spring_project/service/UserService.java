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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder bcryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder bcryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bcryptPasswordEncoder = bcryptPasswordEncoder;
    }

    public void saveNewUser(UserRegistrationDTO user) throws SQLException {
        User newUser = extractUserFromDto(user);
        log.info(user.getEmail());
        userRepository.save(newUser);
        log.info("User " + user.getEmail() + " is successfully registered.");
    }

    private User extractUserFromDto(UserRegistrationDTO user) {
        return User.builder()
                .email(user.getEmail())
                .name(user.getName())
                .nameUkr(user.getNameUkr())
                .password(bcryptPasswordEncoder.encode(user.getPassword()))
                .role(Role.USER)
                .build();
    }


    @Override
    public UserDetails loadUserByUsername(@NonNull String email){
        Optional<User> optional = userRepository.findByEmail(email);
        return optional.orElseGet(User::new);
    }

//    public Optional<User> findById(@NonNull Long id) {
//        return userRepository.findById(id);
//    }
//
//    public Optional<User> findByEmail(User user) {
//        return userRepository.findByEmail(user.getUsername());
//    }
//
//    private boolean isLocaleUa() {
//        return LocaleContextHolder.getLocale().equals(new Locale("ua_UA"));
//    }

}
