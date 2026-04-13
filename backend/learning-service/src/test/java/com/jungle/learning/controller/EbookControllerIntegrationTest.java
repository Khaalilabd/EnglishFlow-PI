package com.jungle.learning.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jungle.learning.model.Ebook;
import com.jungle.learning.repository.EbookRepository;
import com.jungle.learning.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EbookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EbookRepository ebookRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String userToken;
    private String adminToken;
    private Ebook testEbook;

    @BeforeEach
    void setUp() {
        ebookRepository.deleteAll();

        // Generate JWT tokens
        userToken = jwtUtil.generateToken("testuser", "ROLE_USER", 1L);
        adminToken = jwtUtil.generateToken("admin", "ROLE_ADMIN", 2L);

        // Create test ebook
        testEbook = new Ebook();
        testEbook.setTitle("Integration Test Ebook");
        testEbook.setDescription("Test Description");
        testEbook.setFileUrl("test-file.pdf");
        testEbook.setFileSize(1024L);
        testEbook.setMimeType("application/pdf");
        testEbook.setLevel(Ebook.Level.B1);
        testEbook.setCategory(Ebook.Category.GRAMMAR);
        testEbook.setIsFree(true);
        testEbook.setDownloadCount(0);
        testEbook.setStatus(Ebook.PublishStatus.PUBLISHED);
        testEbook.setCreatedBy(1L);
        testEbook.setCreatedAt(LocalDateTime.now());
        testEbook = ebookRepository.save(testEbook);
    }

    @Test
    void getAllEbooks_WithValidToken_ShouldReturnEbooks() throws Exception {
        mockMvc.perform(get("/learning/ebooks")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Integration Test Ebook")));
    }

    @Test
    void getAllEbooks_WithoutToken_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/learning/ebooks"))
                .andExpect(status().isForbidden()); // Spring Security returns 403 for missing auth
    }

    @Test
    void getFreeEbooks_ShouldReturnOnlyFreeEbooks() throws Exception {
        // Create paid ebook
        Ebook paidEbook = new Ebook();
        paidEbook.setTitle("Paid Ebook");
        paidEbook.setDescription("Paid");
        paidEbook.setFileUrl("paid.pdf");
        paidEbook.setFileSize(2048L);
        paidEbook.setMimeType("application/pdf");
        paidEbook.setLevel(Ebook.Level.A1);
        paidEbook.setCategory(Ebook.Category.VOCABULARY);
        paidEbook.setIsFree(false);
        paidEbook.setPrice(new BigDecimal("9.99"));
        paidEbook.setDownloadCount(0);
        paidEbook.setStatus(Ebook.PublishStatus.PUBLISHED);
        paidEbook.setCreatedBy(1L);
        ebookRepository.save(paidEbook);

        mockMvc.perform(get("/learning/ebooks/free")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].free", is(true)));
    }

    @Test
    void getEbookById_WithValidId_ShouldReturnEbook() throws Exception {
        mockMvc.perform(get("/learning/ebooks/" + testEbook.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Integration Test Ebook")))
                .andExpect(jsonPath("$.level", is("B1")));
    }

    @Test
    void getEbookById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/learning/ebooks/999")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteEbook_WithAdminToken_ShouldDeleteEbook() throws Exception {
        mockMvc.perform(delete("/learning/ebooks/" + testEbook.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/learning/ebooks/" + testEbook.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getEbooksByLevel_ShouldReturnFilteredEbooks() throws Exception {
        mockMvc.perform(get("/learning/ebooks/level/B1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].level", is("B1")));
    }

    @Test
    void approveEbook_WithAdminToken_ShouldApproveEbook() throws Exception {
        // Create pending ebook
        Ebook pendingEbook = new Ebook();
        pendingEbook.setTitle("Pending Ebook");
        pendingEbook.setDescription("Pending");
        pendingEbook.setFileUrl("pending.pdf");
        pendingEbook.setFileSize(1024L);
        pendingEbook.setMimeType("application/pdf");
        pendingEbook.setLevel(Ebook.Level.A1);
        pendingEbook.setCategory(Ebook.Category.GENERAL);
        pendingEbook.setIsFree(true);
        pendingEbook.setDownloadCount(0);
        pendingEbook.setStatus(Ebook.PublishStatus.PENDING);
        pendingEbook.setCreatedBy(1L);
        pendingEbook = ebookRepository.save(pendingEbook);

        mockMvc.perform(post("/learning/ebooks/" + pendingEbook.getId() + "/approve")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PUBLISHED")));
    }

    @Test
    void rejectEbook_WithAdminToken_ShouldRejectEbook() throws Exception {
        // Create pending ebook
        Ebook pendingEbook = new Ebook();
        pendingEbook.setTitle("Pending Ebook");
        pendingEbook.setDescription("Pending");
        pendingEbook.setFileUrl("pending.pdf");
        pendingEbook.setFileSize(1024L);
        pendingEbook.setMimeType("application/pdf");
        pendingEbook.setLevel(Ebook.Level.A1);
        pendingEbook.setCategory(Ebook.Category.GENERAL);
        pendingEbook.setIsFree(true);
        pendingEbook.setDownloadCount(0);
        pendingEbook.setStatus(Ebook.PublishStatus.PENDING);
        pendingEbook.setCreatedBy(1L);
        pendingEbook = ebookRepository.save(pendingEbook);

        mockMvc.perform(post("/learning/ebooks/" + pendingEbook.getId() + "/reject")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("reason", "Not suitable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("REJECTED")));
    }

    @Test
    void getPendingEbooks_WithAdminToken_ShouldReturnPendingEbooks() throws Exception {
        // Create pending ebook
        Ebook pendingEbook = new Ebook();
        pendingEbook.setTitle("Pending Ebook");
        pendingEbook.setDescription("Pending");
        pendingEbook.setFileUrl("pending.pdf");
        pendingEbook.setFileSize(1024L);
        pendingEbook.setMimeType("application/pdf");
        pendingEbook.setLevel(Ebook.Level.A1);
        pendingEbook.setCategory(Ebook.Category.GENERAL);
        pendingEbook.setIsFree(true);
        pendingEbook.setDownloadCount(0);
        pendingEbook.setStatus(Ebook.PublishStatus.PENDING);
        pendingEbook.setCreatedBy(1L);
        ebookRepository.save(pendingEbook);

        mockMvc.perform(get("/learning/ebooks/pending")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("PENDING")));
    }
}
