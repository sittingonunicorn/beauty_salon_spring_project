package net.ukr.lina_chen.beauty_salon_spring_project.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.dto.BeautyServiceDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.BeautyService;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.BeautyServiceNotFoundException;
import net.ukr.lina_chen.beauty_salon_spring_project.repository.BeautyServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BeautyServiceImpl {

    private BeautyServiceRepository beautyServiceRepository;

    @Autowired
    public BeautyServiceImpl(BeautyServiceRepository beautyServiceRepository) {
        this.beautyServiceRepository = beautyServiceRepository;
    }

    public List<BeautyServiceDTO> findAllByProfessionId(@NonNull Long professionId, boolean isLocaleEn) {
        return beautyServiceRepository.findAllByProfessionIdOrderByNameAsc(professionId)
                .stream()
                .map(b -> getLocalizedDTO(isLocaleEn, b))
                .sorted(Comparator.comparing(BeautyServiceDTO::getName))
                .collect(Collectors.toList());
    }

    public BeautyService findBeautyServiceById(@NonNull Long beautyserviceId) throws BeautyServiceNotFoundException {
        return beautyServiceRepository.findById(beautyserviceId)
                .orElseThrow(()-> new BeautyServiceNotFoundException(
                        "beauty service with id " + beautyserviceId + " not found"));
    }

    public List<BeautyService> findAll(HttpServletRequest request) {
        return beautyServiceRepository.findAll();
    }

    private BeautyServiceDTO getLocalizedDTO (boolean isLocaleEn, BeautyService beautyService){
        return BeautyServiceDTO.builder()
                .id(beautyService.getId())
                .name(isLocaleEn? beautyService.getName():beautyService.getNameUkr())
                .price(isLocaleEn? getPriceEn(beautyService.getPrice()):getPriceUa(beautyService.getPrice()))
                .professionId(beautyService.getProfession().getId())
                .build();
    }

    private String getPriceUa(BigDecimal price) {
        return String.format("%.2f грн",price.multiply(new BigDecimal(26)).
                round(new MathContext(0, RoundingMode.HALF_UP))) ;
    }

    private String getPriceEn(BigDecimal price) {
        return String.format("%.0f $",price) ;
    }
}

