package net.ukr.lina_chen.beauty_salon_spring_project.service;

import net.ukr.lina_chen.beauty_salon_spring_project.dto.ServicesTypeDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.repository.ServiceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceTypeService {
    private final ServiceTypeRepository serviceTypeRepository;

    @Autowired
    public ServiceTypeService(ServiceTypeRepository serviceTypeRepository) {
        this.serviceTypeRepository = serviceTypeRepository;
    }

    public List<ServicesTypeDTO> findAll(boolean isLocaleEn) {
        return serviceTypeRepository.findAll().stream()
                .map(p -> ServicesTypeDTO.builder()
                        .id(p.getId())
                        .beautyservicesType(isLocaleEn ? p.getBeautyservicesType() : p.getBeautyservicesTypeUkr())
                        .build())
                .sorted(Comparator.comparing(ServicesTypeDTO::getBeautyservicesType))
                .collect(Collectors.toList());
    }


}
