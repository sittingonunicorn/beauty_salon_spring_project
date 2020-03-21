package net.ukr.lina_chen.beauty_salon_spring_project.service;

import lombok.NonNull;
import net.ukr.lina_chen.beauty_salon_spring_project.dto.UsersDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.User;
import net.ukr.lina_chen.beauty_salon_spring_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    public Optional<User> findByUsername(User user) {
        //TODO check for user availability. password check
        return userRepository.findByUsername(user.getUsername());
    }

    public UsersDTO getAllUsers() {
        //TODO checking for an empty user list
        return new UsersDTO(userRepository.findAll());
    }
//    public void saveNewUser(User user) {
//        try {
//            userRepository.save(user);
//        } catch (Exception ex) {
//            System.out.println("Error: duplicate user email");
//        }
//
//    }


    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found."));
    }

//    public Optional<User> findById(@NonNull Long id) {
//        return userRepository.findById(id);
//    }
}
