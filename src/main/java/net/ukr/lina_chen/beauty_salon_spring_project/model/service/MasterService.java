package net.ukr.lina_chen.beauty_salon_spring_project.model.service;

import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.MasterNotFoundException;
import net.ukr.lina_chen.beauty_salon_spring_project.model.dto.MasterDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.model.entity.Master;
import net.ukr.lina_chen.beauty_salon_spring_project.model.entity.User;
import net.ukr.lina_chen.beauty_salon_spring_project.model.mapper.LocalizedDtoMapper;
import net.ukr.lina_chen.beauty_salon_spring_project.model.repository.MasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class MasterService {

    private final MasterRepository masterRepository;

    @Autowired
    public MasterService(MasterRepository masterRepository) {
        this.masterRepository = masterRepository;
    }


    public List<MasterDTO> findAllByServiceType(Long serviceType) {
        return masterRepository.findAllByServiceTypesId(serviceType)
                .stream()
                .map(m -> getLocalizedDTO().map(m))
                .collect(Collectors.toList());
    }

    public Master getMasterAccordingBeautyService(Long masterId, Long serviceType) throws MasterNotFoundException {
        return masterRepository.findByIdAndServiceTypesId(masterId, serviceType)
                .orElseThrow(() -> new MasterNotFoundException("Master id=" + masterId +
                        " doesn't accord this serviceType " + serviceType));


    }

    public MasterDTO findMasterByUser(User user) throws MasterNotFoundException {
        return getLocalizedDTO().map(masterRepository.findMasterByUser(user)
                .orElseThrow(() -> new MasterNotFoundException(
                        "master with user id " + user.getId() + " not found")));
    }

    public List<MasterDTO> findAll() {
        return masterRepository.findAll().stream()
                .map(m -> getLocalizedDTO().map(m))
                .collect(Collectors.toList());
    }

    public LocalizedDtoMapper<MasterDTO, Master> getLocalizedDTO() {
        boolean isLocaleEn = LocaleContextHolder.getLocale().equals(Locale.US);
        return master -> MasterDTO.builder()
                .id(master.getId())
                .name(isLocaleEn ? master.getUser().getName() : master.getUser().getNameUkr())
                .timeBegin(master.getTimeBegin())
                .timeEnd(master.getTimeEnd())
                .build();
    }
}
