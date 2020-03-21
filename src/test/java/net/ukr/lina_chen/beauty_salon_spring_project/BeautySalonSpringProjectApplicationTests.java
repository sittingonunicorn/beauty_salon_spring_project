package net.ukr.lina_chen.beauty_salon_spring_project;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Base64;

@SpringBootTest
class BeautySalonSpringProjectApplicationTests {

    @Test
    void contextLoads() {
    }
    @Test
    public void testConfigureGlobal() throws Exception {
        String auth = "dXNlcjpwYXNzd29yZA==";
        System.out.println(new String(Base64.getDecoder().decode(auth)));

        System.out.println(new BCryptPasswordEncoder().encode("password"));
    }
}
