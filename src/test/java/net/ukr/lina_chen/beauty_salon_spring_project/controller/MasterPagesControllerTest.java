package net.ukr.lina_chen.beauty_salon_spring_project.controller;

import net.ukr.lina_chen.beauty_salon_spring_project.controller.utility.MailService;
import net.ukr.lina_chen.beauty_salon_spring_project.model.service.ArchiveAppointmentService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
//@WithMockUser("inna@gmail.com")
class MasterPagesControllerTest {

    @Autowired
    private MasterPagesController masterPagesController;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArchiveAppointmentService archiveAppointmentService;
    @MockBean
    private MailService mailService;

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


    @Test
    @WithUserDetails("inna@gmail.com")
    void makeProvidedWithAppointmentId_whenMockMVC_thenResponseOK() throws Exception {
        mockMvc.perform(get("/master/makeprovided")
                .param("appointmentId", String.valueOf(14L))).andExpect((status().is3xxRedirection()))
                .andExpect(redirectedUrl("/master/appointments"));
    }

    @Test
    @WithUserDetails("inna@gmail.com")
    void archiveAppointmentsPage() throws Exception {
//        when(masterService.findMasterByUser(any(), anyBoolean())).thenReturn(MasterDTO.builder().id(1L).build());
//        when(archiveAppointmentService.findCommentsForMaster(1L, any(), anyBoolean()))
//                .thenReturn();
        mockMvc.perform(get("/master/comments"))
                .andExpect(status().isOk());
//        System.out.println(masterService);
//        System.out.println(archiveAppointmentService);
//        verify(masterService, times(1)).findMasterByUser(
//                (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), true);
//        MasterDTO master = masterService.findMasterByUser(
//                (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), true);
//        verify(archiveAppointmentService, times(1)).findCommentsForMaster(master.getId(), any(), anyBoolean());
    }

    @Test
    void handleMasterNotFoundException() {
    }

    @Test
    void handleAppointmentNotFoundException() {
    }
}