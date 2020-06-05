package net.ukr.lina_chen.beauty_salon_spring_project.service;

import net.ukr.lina_chen.beauty_salon_spring_project.dto.MasterDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Master;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.User;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.MasterNotFoundException;
import net.ukr.lina_chen.beauty_salon_spring_project.repository.MasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MasterService {

    private final MasterRepository masterRepository;

    @Autowired
    public MasterService(MasterRepository masterRepository) {
        this.masterRepository = masterRepository;
    }


    public List<MasterDTO> findAllByProfessionId(Long professionId, boolean isLocaleEn) {
        return masterRepository.findAllByProfessionId(professionId)
                .stream()
                .map(m -> getLocalizedDTO(m, isLocaleEn))
                .collect(Collectors.toList());
    }

//    public MasterDTO findMasterById(Long id, boolean isLocaleEn){
//        return getLocalizedDTO(masterRepository.findById(id).get(), isLocaleEn);
//    }

    public MasterDTO findMasterByUser(User user, boolean isLocaleEn) throws MasterNotFoundException {
        return getLocalizedDTO(masterRepository.findMasterByUser(user)
                .orElseThrow(() -> new MasterNotFoundException(
                        "master with user id " + user.getId() + " not found")), isLocaleEn);
    }

    public List<MasterDTO> findAll(boolean isLocaleEn){
        return masterRepository.findAll().stream()
                .map(m->getLocalizedDTO(m, isLocaleEn))
                .collect(Collectors.toList());
    }

    public MasterDTO getLocalizedDTO(Master master, boolean isLocaleEn) {
        return MasterDTO.builder()
                .id(master.getId())
                .name(isLocaleEn ? master.getUser().getName() : master.getUser().getNameUkr())
                .timeBegin(master.getTimeBegin())
                .timeEnd(master.getTimeEnd())
                .build();
    }
}
