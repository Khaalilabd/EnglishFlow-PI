package com.englishflow.complaints.integration;

import com.englishflow.complaints.entity.Complaint;
import com.englishflow.complaints.enums.ComplaintCategory;
import com.englishflow.complaints.enums.ComplaintStatus;
import com.englishflow.complaints.enums.TargetRole;
import com.englishflow.complaints.repository.ComplaintRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ComplaintIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private String studentToken;
    private String adminToken;
    private Long studentUserId = 100L;
    private Long adminUserId = 1L;

    @BeforeEach
    void setUp() {
        // Clean up
        complaintRepository.deleteAll();

        // Generate JWT tokens
        studentToken = generateToken(studentUserId, "STUDENT", "student@example.com");
        adminToken = generateToken(adminUserId, "ADMIN", "admin@example.com");
    }

    @Test
    void createComplaint_WithValidToken_ReturnsCreated() throws Exception {
        // Arrange
        Complaint complaint = new Complaint();
        complaint.setUserId(studentUserId);
        complaint.setSubject("Test Complaint");
        complaint.setDescription("This is a detailed test complaint description with more than 20 characters");
        complaint.setCategory(ComplaintCategory.TECHNICAL);
        complaint.setTargetRole(TargetRole.SUPPORT);

        // Act & Assert
        mockMvc.perform(post("/complaints")
                .header("Authorization", "Bearer " + studentToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(complaint)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subject").value("Test Complaint"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.userId").value(studentUserId));
    }

    @Test
    void createComplaint_WithoutToken_ReturnsUnauthorized() throws Exception {
        // Arrange
        Complaint complaint = new Complaint();
        complaint.setUserId(studentUserId);
        complaint.setSubject("Test Complaint");
        complaint.setDescription("This is a detailed test complaint description");
        complaint.setCategory(ComplaintCategory.TECHNICAL);
        complaint.setTargetRole(TargetRole.SUPPORT);

        // Act & Assert
        mockMvc.perform(post("/complaints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(complaint)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createComplaint_WithShortDescription_ReturnsBadRequest() throws Exception {
        // Arrange
        Complaint complaint = new Complaint();
        complaint.setUserId(studentUserId);
        complaint.setSubject("Test");
        complaint.setDescription("Short"); // Less than 20 characters
        complaint.setCategory(ComplaintCategory.TECHNICAL);
        complaint.setTargetRole(TargetRole.SUPPORT);

        // Act & Assert
        mockMvc.perform(post("/complaints")
                .header("Authorization", "Bearer " + studentToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(complaint)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getComplaintsByUserId_AsOwner_ReturnsComplaints() throws Exception {
        // Arrange - Create a complaint
        Complaint complaint = new Complaint();
        complaint.setUserId(studentUserId);
        complaint.setSubject("My Complaint");
        complaint.setDescription("This is my complaint description with sufficient length");
        complaint.setCategory(ComplaintCategory.TECHNICAL);
        complaint.setTargetRole(TargetRole.SUPPORT);
        complaint.setStatus(ComplaintStatus.OPEN);
        complaintRepository.save(complaint);

        // Act & Assert
        mockMvc.perform(get("/complaints/user/" + studentUserId)
                .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].subject").value("My Complaint"));
    }

    @Test
    void getAllComplaints_AsAdmin_ReturnsAllComplaints() throws Exception {
        // Arrange - Create multiple complaints
        Complaint complaint1 = new Complaint();
        complaint1.setUserId(studentUserId);
        complaint1.setSubject("Complaint 1");
        complaint1.setDescription("First complaint description with sufficient length");
        complaint1.setCategory(ComplaintCategory.TECHNICAL);
        complaint1.setTargetRole(TargetRole.SUPPORT);
        complaint1.setStatus(ComplaintStatus.OPEN);
        complaintRepository.save(complaint1);

        Complaint complaint2 = new Complaint();
        complaint2.setUserId(200L);
        complaint2.setSubject("Complaint 2");
        complaint2.setDescription("Second complaint description with sufficient length");
        complaint2.setCategory(ComplaintCategory.PEDAGOGICAL);
        complaint2.setTargetRole(TargetRole.TUTOR);
        complaint2.setStatus(ComplaintStatus.OPEN);
        complaintRepository.save(complaint2);

        // Act & Assert
        mockMvc.perform(get("/complaints")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateComplaint_AsAdmin_ReturnsUpdated() throws Exception {
        // Arrange - Create a complaint
        Complaint complaint = new Complaint();
        complaint.setUserId(studentUserId);
        complaint.setSubject("Original Subject");
        complaint.setDescription("Original description with sufficient length for validation");
        complaint.setCategory(ComplaintCategory.TECHNICAL);
        complaint.setTargetRole(TargetRole.SUPPORT);
        complaint.setStatus(ComplaintStatus.OPEN);
        complaint = complaintRepository.save(complaint);

        // Update details
        Complaint updateDetails = new Complaint();
        updateDetails.setStatus(ComplaintStatus.IN_PROGRESS);
        updateDetails.setResponse("Admin is working on this");
        updateDetails.setResponderId(adminUserId);

        // Act & Assert
        mockMvc.perform(put("/complaints/" + complaint.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void deleteComplaint_AsAdmin_ReturnsNoContent() throws Exception {
        // Arrange - Create a complaint
        Complaint complaint = new Complaint();
        complaint.setUserId(studentUserId);
        complaint.setSubject("To Delete");
        complaint.setDescription("This complaint will be deleted with sufficient length");
        complaint.setCategory(ComplaintCategory.TECHNICAL);
        complaint.setTargetRole(TargetRole.SUPPORT);
        complaint.setStatus(ComplaintStatus.OPEN);
        complaint = complaintRepository.save(complaint);

        // Act & Assert
        mockMvc.perform(delete("/complaints/" + complaint.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void getComplaintsByStatus_ReturnsFilteredComplaints() throws Exception {
        // Arrange - Create complaints with different statuses
        Complaint openComplaint = new Complaint();
        openComplaint.setUserId(studentUserId);
        openComplaint.setSubject("Open Complaint");
        openComplaint.setDescription("Open complaint description with sufficient length");
        openComplaint.setCategory(ComplaintCategory.TECHNICAL);
        openComplaint.setTargetRole(TargetRole.SUPPORT);
        openComplaint.setStatus(ComplaintStatus.OPEN);
        complaintRepository.save(openComplaint);

        Complaint closedComplaint = new Complaint();
        closedComplaint.setUserId(studentUserId);
        closedComplaint.setSubject("Closed Complaint");
        closedComplaint.setDescription("Closed complaint description with sufficient length");
        closedComplaint.setCategory(ComplaintCategory.TECHNICAL);
        closedComplaint.setTargetRole(TargetRole.SUPPORT);
        closedComplaint.setStatus(ComplaintStatus.RESOLVED);
        complaintRepository.save(closedComplaint);

        // Act & Assert
        mockMvc.perform(get("/complaints/status/OPEN")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("OPEN"));
    }

    private String generateToken(Long userId, String role, String email) {
        Key signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(signingKey)
                .compact();
    }
}
