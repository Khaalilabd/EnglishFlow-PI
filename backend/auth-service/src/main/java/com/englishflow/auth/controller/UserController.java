package com.englishflow.auth.controller;

import com.englishflow.auth.dto.AuthResponse;
import com.englishflow.auth.dto.UpdateUserRequest;
import com.englishflow.auth.dto.UserDetailsResponse;
import com.englishflow.auth.dto.UserDTO;
import com.englishflow.auth.dto.UserIdsRequest;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.repository.UserRepository;
import com.englishflow.auth.service.FileStorageService;
import com.englishflow.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/users", "/auth/users", "/api/users"})
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        try {
            Sort.Direction direction = Sort.Direction.fromString(sortDirection);
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<UserDTO> users = userService.getAllUsersPaginated(pageable);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Failed to fetch users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/students")
    public ResponseEntity<List<Map<String, Object>>> getStudentsForMessaging() {
        try {
            // Retourner uniquement les étudiants actifs pour la messagerie
            List<Map<String, Object>> students = userRepository.findByRoleAndIsActive(User.Role.STUDENT, true).stream()
                .map(user -> {
                    Map<String, Object> studentMap = new java.util.HashMap<>();
                    studentMap.put("id", user.getId());
                    studentMap.put("firstName", user.getFirstName());
                    studentMap.put("lastName", user.getLastName());
                    studentMap.put("email", user.getEmail());
                    studentMap.put("profilePhotoUrl", user.getProfilePhoto() != null ? user.getProfilePhoto() : "");
                    return studentMap;
                })
                .collect(Collectors.toList());
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            log.error("Failed to fetch students: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(List.of());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new com.englishflow.auth.exception.UserNotFoundException(id));

        // Update fields if provided
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getProfilePhoto() != null) {
            user.setProfilePhoto(request.getProfilePhoto());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }
        if (request.getPostalCode() != null) {
            user.setPostalCode(request.getPostalCode());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getCin() != null) {
            user.setCin(request.getCin());
        }
        if (request.getEnglishLevel() != null) {
            user.setEnglishLevel(request.getEnglishLevel());
        }
        if (request.getYearsOfExperience() != null) {
            user.setYearsOfExperience(request.getYearsOfExperience());
        }

        user = userRepository.save(user);

        return ResponseEntity.ok(new AuthResponse(
                null, // No new token
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.getProfilePhoto(),
                user.getPhone(),
                user.isProfileCompleted()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new com.englishflow.auth.exception.UserNotFoundException(id));
        
        return ResponseEntity.ok(UserDTO.fromEntity(user));
    }
    
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String role) {
        try {
            log.info("Fetching users with role: {}", role);
            User.Role userRole = User.Role.valueOf(role.toUpperCase());
            List<User> users = userRepository.findByRole(userRole);
            List<UserDTO> userDTOs = users.stream()
                    .map(UserDTO::fromEntity)
                    .collect(Collectors.toList());
            log.info("Found {} users with role {}", userDTOs.size(), role);
            return ResponseEntity.ok(userDTOs);
        } catch (IllegalArgumentException e) {
            log.error("Invalid role: {}", role);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error fetching users by role: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/public/tutors")
    public ResponseEntity<List<UserDTO>> getPublicTutors() {
        try {
            log.info("Public endpoint: Fetching active tutors");
            List<User> tutors = userRepository.findByRoleAndIsActive(User.Role.TUTOR, true);
            List<UserDTO> tutorDTOs = tutors.stream()
                    .map(UserDTO::fromEntity)
                    .collect(Collectors.toList());
            log.info("Found {} active tutors", tutorDTOs.size());
            return ResponseEntity.ok(tutorDTOs);
        } catch (Exception e) {
            log.error("Error fetching public tutors: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/by-role/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByRolePublic(@PathVariable String role) {
        try {
            log.info("Public endpoint: Fetching users with role: {}", role);
            User.Role userRole = User.Role.valueOf(role.toUpperCase());
            List<User> users = userRepository.findByRoleAndIsActive(userRole, true);
            List<UserDTO> userDTOs = users.stream()
                    .map(UserDTO::fromEntity)
                    .collect(Collectors.toList());
            log.info("Found {} active users with role {}", userDTOs.size(), role);
            return ResponseEntity.ok(userDTOs);
        } catch (IllegalArgumentException e) {
            log.error("Invalid role: {}", role);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error fetching users by role: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/public")
    public ResponseEntity<UserDTO> getUserByIdPublic(@PathVariable Long id) {
        log.info("Public endpoint called for user ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new com.englishflow.auth.exception.UserNotFoundException(id));
        
        log.info("Found user: {} {}", user.getFirstName(), user.getLastName());
        return ResponseEntity.ok(UserDTO.fromEntity(user));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<UserDetailsResponse>> getUsersByIds(@RequestBody UserIdsRequest request) {
        System.out.println("📥 Received batch request for user IDs: " + request.getUserIds());
        
        if (request.getUserIds() == null || request.getUserIds().isEmpty()) {
            System.out.println("⚠️ Empty or null user IDs list");
            return ResponseEntity.ok(List.of());
        }
        
        List<User> users = userRepository.findAllById(request.getUserIds());
        System.out.println("✅ Found " + users.size() + " users in database");
        
        List<UserDetailsResponse> response = users.stream()
                .map(user -> {
                    System.out.println("  - User ID " + user.getId() + ": " + user.getFirstName() + " " + user.getLastName());
                    return UserDetailsResponse.fromEntity(user);
                })
                .collect(Collectors.toList());
        
        System.out.println("📤 Returning " + response.size() + " user details");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/upload-photo")
    public ResponseEntity<Map<String, String>> uploadProfilePhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        
        try {
            // Valider le fichier
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Please select a file"));
            }
            
            // Vérifier le type
            if (!fileStorageService.isValidImageFile(file)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Only image files are allowed"));
            }
            
            // Vérifier la taille (max 5MB)
            long maxSize = 5 * 1024 * 1024; // 5MB
            if (!fileStorageService.isValidFileSize(file, maxSize)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "File size must be less than 5MB"));
            }
            
            // Trouver l'utilisateur
            User user = userRepository.findById(id)
                .orElseThrow(() -> new com.englishflow.auth.exception.UserNotFoundException(id));
            
            // Supprimer l'ancienne photo si elle existe
            if (user.getProfilePhoto() != null) {
                fileStorageService.deleteFile(user.getProfilePhoto());
            }
            
            // Sauvegarder le nouveau fichier
            String photoUrl = fileStorageService.storeFile(file);
            
            // Mettre à jour l'utilisateur
            user.setProfilePhoto(photoUrl);
            userRepository.save(user);
            
            return ResponseEntity.ok(Map.of("profilePhoto", photoUrl));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to upload file: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/photo")
    public ResponseEntity<Map<String, String>> deleteProfilePhoto(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id)
                .orElseThrow(() -> new com.englishflow.auth.exception.UserNotFoundException(id));
            
            // Supprimer le fichier physique
            if (user.getProfilePhoto() != null) {
                fileStorageService.deleteFile(user.getProfilePhoto());
            }
            
            // Mettre à jour l'utilisateur
            user.setProfilePhoto(null);
            userRepository.save(user);
            
            return ResponseEntity.ok(Map.of("message", "Profile photo deleted successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete photo: " + e.getMessage()));
        }
    }

    // ========== Professional Documents Endpoints ==========

    @PostMapping("/{id}/upload-document")
    public ResponseEntity<Map<String, Object>> uploadProfessionalDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType) {
        
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Please select a file"));
            }
            
            // Vérifier la taille (max 50MB)
            long maxSize = 50 * 1024 * 1024; // 50MB
            if (!fileStorageService.isValidFileSize(file, maxSize)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "File size must be less than 50MB"));
            }
            
            User user = userRepository.findById(id)
                .orElseThrow(() -> new com.englishflow.auth.exception.UserNotFoundException(id));
            
            // Sauvegarder le fichier
            String filePath = fileStorageService.storeFile(file);
            
            // Créer l'enregistrement du document
            com.englishflow.auth.entity.ProfessionalDocument document = com.englishflow.auth.entity.ProfessionalDocument.builder()
                .userId(user.getId())
                .fileName(file.getOriginalFilename())
                .filePath(filePath)
                .documentType(documentType)
                .fileSize(file.getSize())
                .build();
            
            com.englishflow.auth.repository.ProfessionalDocumentRepository documentRepository = 
                userService.getProfessionalDocumentRepository();
            document = documentRepository.save(document);
            
            return ResponseEntity.ok(Map.of(
                "id", document.getId(),
                "fileName", document.getFileName(),
                "filePath", document.getFilePath(),
                "documentType", document.getDocumentType(),
                "uploadedAt", document.getUploadedAt().toString()
            ));
            
        } catch (Exception e) {
            log.error("Failed to upload document: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to upload document: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/documents")
    public ResponseEntity<List<Map<String, Object>>> getUserDocuments(@PathVariable Long id) {
        try {
            com.englishflow.auth.repository.ProfessionalDocumentRepository documentRepository = 
                userService.getProfessionalDocumentRepository();
            
            List<com.englishflow.auth.entity.ProfessionalDocument> documents = documentRepository.findByUserId(id);
            
            List<Map<String, Object>> response = documents.stream()
                .map(doc -> Map.of(
                    "id", (Object) doc.getId(),
                    "fileName", doc.getFileName(),
                    "filePath", doc.getFilePath(),
                    "documentType", doc.getDocumentType(),
                    "fileSize", doc.getFileSize(),
                    "uploadedAt", doc.getUploadedAt().toString()
                ))
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to fetch documents: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/documents/{documentId}")
    public ResponseEntity<Map<String, String>> deleteDocument(@PathVariable Long documentId) {
        try {
            com.englishflow.auth.repository.ProfessionalDocumentRepository documentRepository = 
                userService.getProfessionalDocumentRepository();
            
            com.englishflow.auth.entity.ProfessionalDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
            
            // Supprimer le fichier physique
            fileStorageService.deleteFile(document.getFilePath());
            
            // Supprimer l'enregistrement
            documentRepository.delete(document);
            
            return ResponseEntity.ok(Map.of("message", "Document deleted successfully"));
            
        } catch (Exception e) {
            log.error("Failed to delete document: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete document: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/professional")
    public ResponseEntity<Map<String, String>> updateProfessionalProfile(
            @PathVariable Long id,
            @RequestBody Map<String, Object> professionalData) {
        
        try {
            User user = userRepository.findById(id)
                .orElseThrow(() -> new com.englishflow.auth.exception.UserNotFoundException(id));
            
            // Mettre à jour les champs professionnels avec gestion des types
            if (professionalData.containsKey("yearsOfExperience")) {
                Object yearsObj = professionalData.get("yearsOfExperience");
                if (yearsObj != null) {
                    Integer years = yearsObj instanceof Integer ? (Integer) yearsObj : Integer.parseInt(yearsObj.toString());
                    user.setYearsOfExperience(years);
                }
            }
            if (professionalData.containsKey("englishLevel")) {
                String level = (String) professionalData.get("englishLevel");
                user.setEnglishLevel(level);
            }
            
            // Note: Les autres champs (education, certifications, etc.) sont stockés dans TutorApplication
            // Si l'utilisateur a un applicationId, on peut mettre à jour l'application
            if (user.getApplicationId() != null && professionalData.size() > 2) {
                // Mettre à jour l'application de recrutement si elle existe
                com.englishflow.auth.entity.TutorApplication application = 
                    userService.getTutorApplicationRepository().findById(user.getApplicationId()).orElse(null);
                
                if (application != null) {
                    if (professionalData.containsKey("education")) {
                        application.setEducation((String) professionalData.get("education"));
                    }
                    if (professionalData.containsKey("certifications")) {
                        application.setCertifications((String) professionalData.get("certifications"));
                    }
                    if (professionalData.containsKey("workExperience")) {
                        application.setWorkExperience((String) professionalData.get("workExperience"));
                    }
                    if (professionalData.containsKey("teachingPhilosophy")) {
                        application.setTeachingPhilosophy((String) professionalData.get("teachingPhilosophy"));
                    }
                    if (professionalData.containsKey("availability")) {
                        application.setAvailability((String) professionalData.get("availability"));
                    }
                    if (professionalData.containsKey("specializations")) {
                        application.setSpecializations((String) professionalData.get("specializations"));
                    }
                    if (professionalData.containsKey("yearsOfExperience")) {
                        Object yearsObj = professionalData.get("yearsOfExperience");
                        if (yearsObj != null) {
                            Integer years = yearsObj instanceof Integer ? (Integer) yearsObj : Integer.parseInt(yearsObj.toString());
                            application.setYearsOfExperience(years);
                        }
                    }
                    if (professionalData.containsKey("englishLevel")) {
                        application.setEnglishLevel((String) professionalData.get("englishLevel"));
                    }
                    
                    userService.getTutorApplicationRepository().save(application);
                }
            }
            
            userRepository.save(user);
            
            return ResponseEntity.ok(Map.of("message", "Professional profile updated successfully"));
            
        } catch (Exception e) {
            log.error("Failed to update professional profile: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update professional profile: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/change-password-first-login")
    public ResponseEntity<Map<String, String>> changePasswordFirstLogin(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        try {
            String newPassword = request.get("newPassword");
            
            if (newPassword == null || newPassword.length() < 8) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Password must be at least 8 characters long"));
            }
            
            User user = userRepository.findById(id)
                .orElseThrow(() -> new com.englishflow.auth.exception.UserNotFoundException(id));
            
            // Vérifier que l'utilisateur doit changer son mot de passe
            if (!user.isMustChangePassword()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Password change not required for this user"));
            }
            
            // Mettre à jour le mot de passe
            user.setPassword(userService.encodePassword(newPassword));
            user.setMustChangePassword(false);
            userRepository.save(user);
            
            log.info("Password changed successfully for user {} on first login", id);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
            
        } catch (Exception e) {
            log.error("Failed to change password on first login: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to change password: " + e.getMessage()));
        }
    }
}
