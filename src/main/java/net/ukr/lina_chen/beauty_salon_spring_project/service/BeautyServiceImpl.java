package net.ukr.lina_chen.beauty_salon_spring_project.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.BeautyService;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.BeautyServiceNotFoundException;
import net.ukr.lina_chen.beauty_salon_spring_project.repository.BeautyServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BeautyServiceImpl {

    private BeautyServiceRepository beautyServiceRepository;

    @Autowired
    public BeautyServiceImpl(BeautyServiceRepository beautyServiceRepository) {
        this.beautyServiceRepository = beautyServiceRepository;
    }

    public List<BeautyService> findAllByProfessionId(@NonNull Long professionId, HttpServletRequest request) {
        return beautyServiceRepository.findAllByProfessionIdAndLanguageCode(professionId, RequestContextUtils.getLocale(request).toString());
    }

    public BeautyService findBeautyServiceById(@NonNull Long beautyserviceId) throws BeautyServiceNotFoundException {
        return beautyServiceRepository.findById(beautyserviceId)
                .orElseThrow(()-> new BeautyServiceNotFoundException(
                        "beauty service with id " + beautyserviceId + " not found"));
    }

    public List<BeautyService> findAll(HttpServletRequest request) {
        return beautyServiceRepository.findAllByLanguageCode
                (RequestContextUtils.getLocale(request).toString());
    }
}

