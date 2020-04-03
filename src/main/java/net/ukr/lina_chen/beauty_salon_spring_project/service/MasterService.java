package net.ukr.lina_chen.beauty_salon_spring_project.service;

import net.ukr.lina_chen.beauty_salon_spring_project.entity.Master;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.User;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.MasterNotFoundException;
import net.ukr.lina_chen.beauty_salon_spring_project.repository.MasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class MasterService {

    private final MasterRepository masterRepository;

    @Autowired
    public MasterService(MasterRepository masterRepository) {
        this.masterRepository = masterRepository;
    }


    public List<Master> findAllByProfessionId(Long professionId, HttpServletRequest request) {
        return masterRepository.findAllByProfessionIdAndLanguageCode(professionId,
                RequestContextUtils.getLocale(request).toString());
    }

    public Master findMasterById(Long id) throws MasterNotFoundException {
        return masterRepository.findById(id).orElseThrow(()-> new MasterNotFoundException(
                "master with id " + id + " not found"));
    }

    public Master findMasterByUser(User user, HttpServletRequest request) throws MasterNotFoundException {
        return masterRepository.findMasterByUserAndLanguageCode(user, RequestContextUtils.getLocale(request).toString())
                .orElseThrow(()-> new MasterNotFoundException(
                "master with user id " + user.getId() + " not found"));
    }
}
