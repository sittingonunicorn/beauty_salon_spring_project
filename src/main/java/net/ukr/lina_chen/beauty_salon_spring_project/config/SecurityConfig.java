package net.ukr.lina_chen.beauty_salon_spring_project.config;

import net.ukr.lina_chen.beauty_salon_spring_project.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserService userService;

    @Bean
    public PasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(bcryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/static/css/**").permitAll()
                .antMatchers("/registration", "/login", "/index").anonymous()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").permitAll()
                .defaultSuccessUrl("/main", true)
                .failureUrl("/login?error")
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll();
    }
}