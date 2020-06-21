package net.ukr.lina_chen.beauty_salon_spring_project.service;

import net.ukr.lina_chen.beauty_salon_spring_project.model.dto.MasterDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.model.entity.Master;
import net.ukr.lina_chen.beauty_salon_spring_project.model.entity.Role;
import net.ukr.lina_chen.beauty_salon_spring_project.model.entity.User;
import net.ukr.lina_chen.beauty_salon_spring_project.exceptions.MasterNotFoundException;
import net.ukr.lina_chen.beauty_salon_spring_project.model.repository.MasterRepository;
import net.ukr.lina_chen.beauty_salon_spring_project.model.service.MasterService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class MasterServiceTest {

    @Autowired
    MasterService masterService;

    @MockBean
    private MasterRepository masterRepository;

    private User user;

    private Master master;

    @Before
    public void createInstances(){
        user = User.builder()
                .email("inna@gmail.com")
                .name("Inna")
                .nameUkr("Інна")
                .password("")
                .role(Role.MASTER)
                .id(3L)
                .build();
        master=Master.builder()
                .id(1L)
                .serviceTypes(new HashSet<>())
                .timeBegin(LocalTime.of(8, 0))
                .timeEnd(LocalTime.of(20, 0))
                .user(user)
                .build();
    }

    @Test
    public void findAllByServiceType() {
        masterService.findAllByServiceType(1L);
        verify(masterRepository, times(1)).findAllByServiceTypesId(1L);
    }

    @Test (expected = MasterNotFoundException.class)
    public void getMasterAccordingBeautyServiceException() throws MasterNotFoundException {
        masterService.getMasterAccordingBeautyService(1L, 1L);
    }

    @Test
    public void getMasterAccordingBeautyService() throws MasterNotFoundException {
        when(masterRepository.findByIdAndServiceTypesId(1L, 1L)).thenReturn(Optional.ofNullable(master));
        masterService.getMasterAccordingBeautyService(1L, 1L);
        verify(masterRepository, times(1)).findByIdAndServiceTypesId(1L, 1L);
    }

    @Test
    public void findMasterByUser() throws MasterNotFoundException {
        when(masterRepository.findMasterByUser(user)).thenReturn(Optional.ofNullable(master));
        MasterDTO masterDTO = masterService.findMasterByUser(user);
        assertEquals(Optional.of("Інна"), Optional.of(masterDTO.getName()));
    }

    @Test
    public void findAll() {
        List<Master> masters = new ArrayList<Master>();
        masters.add(master);
        when(masterRepository.findAll()).thenReturn(masters);
        List<MasterDTO> mastersDTO = masterService.findAll();
        verify(masterRepository, times(1)).findAll();
        assertEquals("Inna", mastersDTO.get(0).getName());
    }
}