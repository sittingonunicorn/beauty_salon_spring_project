package net.ukr.lina_chen.beauty_salon_spring_project.service;

import net.ukr.lina_chen.beauty_salon_spring_project.dto.ProfessionDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.repository.ProfessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfessionService {
    private final ProfessionRepository professionRepository;

    @Autowired
    public ProfessionService(ProfessionRepository professionRepository) {
        this.professionRepository = professionRepository;
    }

    public List<ProfessionDTO> findAll(boolean isLocaleEn) {
        return professionRepository.findAll().stream()
                .map(p -> ProfessionDTO.builder()
                        .id(p.getId())
                        .beautyservicesType(isLocaleEn ? p.getBeautyservicesType() : p.getBeautyservicesTypeUkr())
                        .name(isLocaleEn ? p.getName() : p.getNameUkr())
                        .build())
                .sorted(Comparator.comparing(ProfessionDTO::getBeautyservicesType))
                .collect(Collectors.toList());
    }


}
