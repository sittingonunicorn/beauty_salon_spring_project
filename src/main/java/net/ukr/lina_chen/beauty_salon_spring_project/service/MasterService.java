package net.ukr.lina_chen.beauty_salon_spring_project.service;

import net.ukr.lina_chen.beauty_salon_spring_project.entity.Master;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Profession;
import net.ukr.lina_chen.beauty_salon_spring_project.repository.MasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MasterService{

    private final MasterRepository masterRepository;

    @Autowired
    public MasterService(MasterRepository masterRepository) {
        this.masterRepository = masterRepository;
    }


    List <Master> findAllByProfession(Long professionId){
        return masterRepository.findAllByProfessionId(professionId);
    }
}
