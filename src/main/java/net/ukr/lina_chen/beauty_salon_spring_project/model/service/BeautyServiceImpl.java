package net.ukr.lina_chen.beauty_salon_spring_project.model.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.model.dto.BeautyServiceDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.model.entity.BeautyService;
import net.ukr.lina_chen.beauty_salon_spring_project.model.mapper.LocalizedDtoMapper;
import net.ukr.lina_chen.beauty_salon_spring_project.model.repository.BeautyServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BeautyServiceImpl {

    private BeautyServiceRepository beautyServiceRepository;

    @Autowired
    public BeautyServiceImpl(BeautyServiceRepository beautyServiceRepository) {
        this.beautyServiceRepository = beautyServiceRepository;
    }

    public List<BeautyServiceDTO> findAllByServiceTypeId(@NonNull Long serviceTypeId) {
        return beautyServiceRepository.findAllByServiceTypeIdOrderByNameAsc(serviceTypeId)
                .stream()
                .map(b -> getLocalizedDTO().map(b))
                .sorted(Comparator.comparing(BeautyServiceDTO::getName))
                .collect(Collectors.toList());
    }

    private LocalizedDtoMapper<BeautyServiceDTO, BeautyService> getLocalizedDTO() {
        boolean isLocaleEn = LocaleContextHolder.getLocale().equals(Locale.US);
        return beautyService -> BeautyServiceDTO.builder()
                .id(beautyService.getId())
                .name(isLocaleEn? beautyService.getName():beautyService.getNameUkr())
                .price(isLocaleEn? getPriceEn(beautyService.getPrice()):getPriceUa(beautyService.getPrice()))
                .professionId(beautyService.getServiceType().getId())
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

