package net.ukr.lina_chen.beauty_salon_spring_project.controller;

import net.ukr.lina_chen.beauty_salon_spring_project.model.dto.ArchiveAppointmentDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.model.dto.MasterDTO;
import net.ukr.lina_chen.beauty_salon_spring_project.model.entity.User;
import net.ukr.lina_chen.beauty_salon_spring_project.model.service.ArchiveAppointmentService;
import net.ukr.lina_chen.beauty_salon_spring_project.model.service.MasterService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
class MasterPagesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArchiveAppointmentService archiveAppointmentService;

    @MockBean
    private MasterService masterService;

    @Test
    void masterAppointmentsPageUnauthorized() throws Exception {
        mockMvc.perform(get("/master/appointments"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithUserDetails("anna@gmail.com")
    void masterAppointmentsPageFromUser() throws Exception {
        mockMvc.perform(get("/master/appointments"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithUserDetails("inna@gmail.com")
    void masterAppointmentsPageMaster() throws Exception {
        mockMvc.perform(get("/master/appointments"))
                .andExpect(status().isOk());
    }

//    @Test
//    @WithUserDetails("inna@gmail.com")
//    void makeProvidedWithAppointmentId_whenMockMVC_thenResponseOK() throws Exception {
//        mockMvc.perform(get("/master/makeprovided")
//                .param("appointmentId", String.valueOf(14L))).andExpect((status().is3xxRedirection()))
//                .andExpect(redirectedUrl("/master/appointments"));
//    }

    @Test
    @WithUserDetails("inna@gmail.com")
    void archiveAppointmentsPage() throws Exception {
        when(masterService.findMasterByUser(isA(User.class))).thenReturn(MasterDTO.builder().id(1L).build());
        List<ArchiveAppointmentDTO> appointments = new ArrayList<>();
        @SuppressWarnings("unchecked")
        Page<ArchiveAppointmentDTO> page = new PageImpl(appointments);
        when(archiveAppointmentService.findCommentsForMaster(eq(1L), isA(Pageable.class)))
                .thenReturn(page);
        mockMvc.perform(get("/master/comments"))
                .andExpect(status().isOk())
                .andExpect(view().name("master/comments.html"));
    }
}