package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBDiningCommons;
import edu.ucsb.cs156.example.entities.UCSBOrganization;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBOrganizationController.class)
@Import(TestConfig.class)
public class UCSBOrganizationControllerTests extends ControllerTestCase
{
    @MockBean
    UCSBOrganizationRepository ucsbOrganizationRepository;

    @MockBean
    UserRepository userRepository;

    // Original Get tests
    
    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
            mockMvc.perform(get("/api/ucsborganization/all"))
                            .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
            mockMvc.perform(get("/api/ucsborganization/all"))
                            .andExpect(status().is(200)); // logged
    }

    @Test
     public void logged_out_users_cannot_get_by_id() throws Exception {
	    mockMvc.perform(get("/api/ucsborganization?orgCode=acm"))
			    .andExpect(status().is(403)); // logged out users can't get by id
     }

     @WithMockUser(roles = { "USER" })
     @Test
     public void logged_in_user_can_get_all_ucsborganizations() throws Exception {

             // arrange

             UCSBOrganization zpr = UCSBOrganization.builder()
                             .orgCode("zpr")
                             .orgTranslationShort("Zeta Phi Rho")
                             .orgTranslation("Zeta Phi Rho")
                             .inactive(false)
                             .build();
             
             UCSBOrganization cdt = UCSBOrganization.builder()
                             .orgCode("cdt")
                             .orgTranslationShort("Chi Delts")
                             .orgTranslation("Chi Delta Theta")
                             .inactive(false)
                             .build();

             ArrayList<UCSBOrganization> expectedOrganizations = new ArrayList<>();
             expectedOrganizations.addAll(Arrays.asList(zpr, cdt));

             when(ucsbOrganizationRepository.findAll()).thenReturn(expectedOrganizations);

             // act
             MvcResult response = mockMvc.perform(get("/api/ucsborganization/all"))
                             .andExpect(status().isOk()).andReturn();

             // assert

             verify(ucsbOrganizationRepository, times(1)).findAll();
             String expectedJson = mapper.writeValueAsString(expectedOrganizations);
             String responseString = response.getResponse().getContentAsString();
             assertEquals(expectedJson, responseString);
     }

    // Original Post tests

    @Test
    public void logged_out_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/ucsborganization/post"))
                            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/ucsborganization/post"))
                            .andExpect(status().is(403)); // only admins can post
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_org() throws Exception {
                // arrange

                UCSBOrganization zpr = UCSBOrganization.builder()
                                .orgCode("ZPR")
                                .orgTranslationShort("Zeta Phi Rho")
                                .orgTranslation("Zeta Phi Rho")
                                .inactive(true)
                                .build();

                when(ucsbOrganizationRepository.save(eq(zpr))).thenReturn(zpr);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/ucsborganization/post?orgCode=ZPR&orgTranslationShort=Zeta Phi Rho&orgTranslation=Zeta Phi Rho&inactive=true")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbOrganizationRepository, times(1)).save(zpr);
                String expectedJson = mapper.writeValueAsString(zpr);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
     }

     // Get endpoint for single record tests

     @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                UCSBOrganization zpr = UCSBOrganization.builder()
                                .orgCode("zpr")
                                .orgTranslationShort("Zeta Phi Rho")
                                .orgTranslation("Zeta Phi Rho")
                                .inactive(false)
                                .build();

                when(ucsbOrganizationRepository.findById(eq("zpr"))).thenReturn(Optional.of(zpr));

                MvcResult response = mockMvc.perform(get("/api/ucsborganization?orgCode=zpr"))
                                .andExpect(status().isOk()).andReturn();

                verify(ucsbOrganizationRepository, times(1)).findById(eq("zpr"));
                String expectedJson = mapper.writeValueAsString(zpr);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                when(ucsbOrganizationRepository.findById(eq("cdt"))).thenReturn(Optional.empty());

                MvcResult response = mockMvc.perform(get("/api/ucsborganization?orgCode=cdt"))
                                .andExpect(status().isNotFound()).andReturn();

                verify(ucsbOrganizationRepository, times(1)).findById(eq("cdt"));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("UCSBOrganization with id cdt not found", json.get("message"));
        }
}
