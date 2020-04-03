package net.ukr.lina_chen.beauty_salon_spring_project.service;

import net.ukr.lina_chen.beauty_salon_spring_project.entity.Master;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.User;
import net.ukr.lina_chen.beauty_salon_spring_project.repository.MasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

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

    public Optional<Master> findMasterById(Long id) {
        return masterRepository.findById(id);
    }

    public Optional<Master> findMasterByUser(User user, HttpServletRequest request) {
        return masterRepository.findMasterByUserAndLanguageCode(user, RequestContextUtils.getLocale(request).toString());
    }
}
