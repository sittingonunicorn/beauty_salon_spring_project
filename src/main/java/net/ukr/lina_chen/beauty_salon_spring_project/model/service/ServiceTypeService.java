package net.ukr.lina_chen.beauty_salon_spring_project.model.service;

import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.MasterNotFoundException;
import net.ukr.lina_chen.beauty_salon_spring_project.model.dto.ServicesTypeDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.model.entity.Master;
import net.ukr.lina_chen.beauty_salon_spring_project.model.entity.ServiceType;
import net.ukr.lina_chen.beauty_salon_spring_project.model.mapper.LocalizedDtoMapper;
import net.ukr.lina_chen.beauty_salon_spring_project.model.repository.ServiceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ServiceTypeService {
    private final ServiceTypeRepository serviceTypeRepository;

    @Autowired
    public ServiceTypeService(ServiceTypeRepository serviceTypeRepository) {
        this.serviceTypeRepository = serviceTypeRepository;
    }

    public List<ServicesTypeDTO> findAll() {
        return serviceTypeRepository.findAll().stream()
                .map(s -> getLocalizedDTO().map(s) )
                .sorted(Comparator.comparing(ServicesTypeDTO::getBeautyservicesType))
                .collect(Collectors.toList());
    }

    private LocalizedDtoMapper<ServicesTypeDTO, ServiceType> getLocalizedDTO() {
        boolean isLocaleEn = LocaleContextHolder.getLocale().equals(Locale.US);
        return serviceType -> ServicesTypeDTO.builder()
                .id(serviceType.getId())
                .beautyservicesType(isLocaleEn ? serviceType.getBeautyservicesType()
                        : serviceType.getBeautyservicesTypeUkr())
                .build();
    }

    public List <Master> findServiceTypesMasters (Long serviceTypeId) throws MasterNotFoundException {
        return new ArrayList<>(serviceTypeRepository.findById(serviceTypeId).orElseThrow(() -> new MasterNotFoundException(
                "masters with serviceType id " + serviceTypeId + " not found")).getMasters());
    }

}
