package net.ukr.lina_chen.beauty_salon_spring_project.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Slf4j
@Configuration
public class LocaleConfig implements WebMvcConfigurer {
    @Bean
    public LocaleResolver localeResolver() {
        return new NativeLocaleResolver();
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

//    @Bean
//    public ReloadableResourceBundleMessageSource messageSource(){
//        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
//        messageSource.setDefaultEncoding("UTF-8");
//        messageSource.setBasename("\\resources\\messages");
//        return messageSource;
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    protected static class NativeLocaleResolver extends SessionLocaleResolver {
        @Override
        public Locale resolveLocale(HttpServletRequest request) {
            String language = request.getParameter("lang");
            Locale locale;
            locale= new Locale("ua", "UA");
            if (!StringUtils.isEmpty(language)) {
                String[] split = language.split("_");
                locale = new Locale(split[0], split[1]);
            }
            return locale;
        }

        @Override
        public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        }

    }
}
