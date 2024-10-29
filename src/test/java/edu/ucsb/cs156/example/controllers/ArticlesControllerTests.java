package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.Articles;
import edu.ucsb.cs156.example.repositories.ArticlesRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(controllers = ArticlesController.class)
@Import(TestConfig.class)
public class ArticlesControllerTests extends ControllerTestCase {

    @MockBean
    ArticlesRepository articlesRepository;

    @MockBean
    UserRepository userRepository;


    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/articles/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all_articles() throws Exception {
        LocalDateTime dateAdded1 = LocalDateTime.parse("2022-01-01T00:00:00");
        LocalDateTime dateAdded2 = LocalDateTime.parse("2022-01-02T00:00:00");

        Articles article1 = Articles.builder()
                .title("Article 1")
                .url("https://example.com/1")
                .explanation("Explanation 1")
                .email("user1@example.com")
                .dateAdded(dateAdded1)
                .build();

        Articles article2 = Articles.builder()
                .title("Article 2")
                .url("https://example.com/2")
                .explanation("Explanation 2")
                .email("user2@example.com")
                .dateAdded(dateAdded2)
                .build();

        ArrayList<Articles> articles = new ArrayList<>(Arrays.asList(article1, article2));

        when(articlesRepository.findAll()).thenReturn(articles);

        MvcResult response = mockMvc.perform(get("/api/articles/all"))
                .andExpect(status().isOk())
                .andReturn();

        verify(articlesRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(articles);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    // Tests for POST /api/articles/post

    @Test
    public void logged_out_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/articles/post"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/articles/post"))
                .andExpect(status().is(403)); // only admins can post
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void an_admin_user_can_post_a_new_article() throws Exception {
        LocalDateTime dateAdded = LocalDateTime.parse("2022-01-01T00:00:00");

        Articles article = Articles.builder()
                .title("New Article")
                .url("https://example.com/new")
                .explanation("Explanation for new article")
                .email("admin@example.com")
                .dateAdded(dateAdded)
                .build();

        when(articlesRepository.save(any())).thenReturn(article);

        MvcResult response = mockMvc.perform(
                        post("/api/articles/post")
                                .param("title", "New Article")
                                .param("url", "https://example.com/new")
                                .param("explanation", "Explanation for new article")
                                .param("email", "admin@example.com")
                                .param("dateAdded", "2022-01-01T00:00:00")
                                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        verify(articlesRepository, times(1)).save(any());
        String expectedJson = mapper.writeValueAsString(article);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }
}
