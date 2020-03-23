package net.ukr.lina_chen.beauty_salon_spring_project.service;

import net.ukr.lina_chen.beauty_salon_spring_project.entity.Profession;
import net.ukr.lina_chen.beauty_salon_spring_project.repository.ProfessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessionService {
    private ProfessionRepository professionRepository;

    @Autowired
    public ProfessionService(ProfessionRepository professionRepository) {
        this.professionRepository = professionRepository;
    }

    public List<Profession> findAll() {
        return professionRepository.findAll();
    }
}
