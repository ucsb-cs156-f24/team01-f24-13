package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.Articles;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.ArticlesRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.time.LocalDateTime;

/**
 * This is a REST controller for Articles
 */

@Tag(name = "Articles")
@RequestMapping("/api/articles")
@RestController
@Slf4j
public class ArticlesController extends ApiController {

    @Autowired
    ArticlesRepository articlesRepository;

    /**
     * List all articles
     * 
     * @return an iterable of Articles
     */
    @Operation(summary = "List all articles")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<Articles> allArticles() {
        Iterable<Articles> articles = articlesRepository.findAll();
        return articles;
    }

    /**
     * Create a new article
     * 
     * @param title       the title of the article
     * @param url         the URL of the article
     * @param explanation a short explanation of the article
     * @param email       the email of the submitter
     * @param dateAdded   the date the article was added
     * @return the saved article
     */
    @Operation(summary = "Create a new article")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public Articles postArticle(
            @Parameter(name = "title") @RequestParam String title,
            @Parameter(name = "url") @RequestParam String url,
            @Parameter(name = "explanation") @RequestParam String explanation,
            @Parameter(name = "email") @RequestParam String email,
            @Parameter(name = "dateAdded", description = "date added in ISO format (YYYY-MM-DDTHH:MM:SS)") @RequestParam LocalDateTime dateAdded) {

        Articles article = Articles.builder()
                .title(title)
                .url(url)
                .explanation(explanation)
                .email(email)
                .dateAdded(dateAdded)
                .build();

        Articles savedArticle = articlesRepository.save(article);

        return savedArticle;
    }
}
