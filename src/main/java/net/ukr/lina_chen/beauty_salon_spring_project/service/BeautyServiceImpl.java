package net.ukr.lina_chen.beauty_salon_spring_project.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.BeautyService;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Profession;
import net.ukr.lina_chen.beauty_salon_spring_project.repository.BeautyServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<BeautyService> findAllByProfessionId(@NonNull Long profession) {
        return beautyServiceRepository.findAllByProfessionId(profession);
    }

    public Optional<BeautyService> findBeautyServiceById(@NonNull Long procedureId) {
        return beautyServiceRepository.findById(procedureId);
    }
}
