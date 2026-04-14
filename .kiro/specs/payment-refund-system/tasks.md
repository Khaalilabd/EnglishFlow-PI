# Implementation Plan: Payment Refund System

## Overview

This plan implements a complete refund system for the payment service, enabling students to request refunds for courses and packs, administrators to review and process refunds, and automatic integration with Paymee gateway and enrollment reversal. The implementation follows a backend-first approach, then adds frontend components, and finally integrates email notifications.

## Tasks

- [x] 1. Set up backend refund infrastructure
  - [x] 1.1 Create RefundStatus enum
    - Create enum with values: PENDING, APPROVED, REJECTED, PROCESSING, COMPLETED, FAILED, CANCELLED
    - Location: `backend/payment-service/src/main/java/com/englishflow/payment/enums/RefundStatus.java`
    - _Requirements: 1.1, 3.2, 3.3, 4.2, 4.3, 4.4_
  
  - [x] 1.2 Create Refund entity with JPA annotations
    - Define all fields: id, payment, studentId, amount, status, reason, timestamps, adminId, rejectionReason, paymeeTransactionId, errorMessage
    - Add relationships to Payment entity
    - Add created/updated timestamps with @PrePersist and @PreUpdate
    - Location: `backend/payment-service/src/main/java/com/englishflow/payment/entity/Refund.java`
    - _Requirements: 1.2, 12.1, 12.2, 12.4_
  
  - [x] 1.3 Create database migration for refunds table
    - Create Flyway migration with refunds table schema
    - Add foreign key constraint to payments table
    - Add indexes on payment_id, student_id, status, requested_at
    - Add check constraint for status values
    - Location: `backend/payment-service/src/main/resources/db/migration/V{next}_create_refunds_table.sql`
    - _Requirements: 12.1, 12.2, 12.5_

- [x] 2. Implement refund data access layer
  - [x] 2.1 Create RefundRepository interface
    - Extend JpaRepository<Refund, Long>
    - Add custom query methods: findByStudentId, findByStatus, findByPaymentId
    - Add query for statistics: countByStatus, sumAmountByStatus
    - Add query with filters using @Query or Specification
    - Location: `backend/payment-service/src/main/java/com/englishflow/payment/repository/RefundRepository.java`
    - _Requirements: 7.1, 7.3, 7.5_
  
  - [ ]* 2.2 Write unit tests for RefundRepository
    - Test findByStudentId returns correct refunds
    - Test findByStatus filters correctly
    - Test statistics queries return accurate counts and sums
    - _Requirements: 7.1, 7.3, 7.5_

- [x] 3. Create DTOs for refund operations
  - [x] 3.1 Create CreateRefundRequest DTO
    - Fields: paymentId, reason
    - Add validation annotations: @NotNull for paymentId, @Size for reason
    - Location: `backend/payment-service/src/main/java/com/englishflow/payment/dto/CreateRefundRequest.java`
    - _Requirements: 1.1, 1.2_
  
  - [x] 3.2 Create RefundDTO for responses
    - Include all refund fields plus payment details (orderId, itemType, itemId, itemName)
    - Add student information (name, email)
    - Location: `backend/payment-service/src/main/java/com/englishflow/payment/dto/RefundDTO.java`
    - _Requirements: 3.1, 7.2_
  
  - [x] 3.3 Create RefundFilterDTO for queries
    - Fields: status, studentId, startDate, endDate, itemType
    - Location: `backend/payment-service/src/main/java/com/englishflow/payment/dto/RefundFilterDTO.java`
    - _Requirements: 7.3_
  
  - [x] 3.4 Create RefundStatsDTO for statistics
    - Fields: totalRefunds, pendingRefunds, approvedRefunds, completedRefunds, rejectedRefunds, failedRefunds, totalRefundAmount, completedRefundAmount
    - Location: `backend/payment-service/src/main/java/com/englishflow/payment/dto/RefundStatsDTO.java`
    - _Requirements: 7.5_

- [x] 4. Implement Paymee refund integration
  - [x] 4.1 Create Paymee refund request/response DTOs
    - Create PaymeeRefundRequest with amount and transaction_id fields
    - Create PaymeeRefundResponse with status, message, code, data fields
    - Create PaymeeRefundData with refund_transaction_id, status, amount
    - Location: `backend/payment-service/src/main/java/com/englishflow/payment/dto/paymee/`
    - _Requirements: 4.1, 4.3, 4.4_
  
  - [x] 4.2 Implement PaymeeClient refund method
    - Add refundPayment method to existing PaymeeClient or create new client
    - Use RestTemplate or WebClient to call Paymee refund API
    - Handle authentication with API token
    - Map response to PaymeeRefundResponse
    - Add error handling for network failures
    - Location: `backend/payment-service/src/main/java/com/englishflow/payment/client/PaymeeClient.java`
    - _Requirements: 4.1, 4.3, 4.4_
  
  - [ ]* 4.3 Write unit tests for PaymeeClient refund method
    - Mock Paymee API responses for success and failure scenarios
    - Test error handling for network failures
    - Test authentication header inclusion
    - _Requirements: 4.1, 4.3, 4.4_

- [x] 5. Implement courses service integration for unenrollment
  - [x] 5.1 Create CoursesServiceClient for enrollment reversal
    - Add method unenrollFromCourse(studentId, courseId)
    - Add method unenrollFromPack(studentId, packId)
    - Use FeignClient or RestTemplate to call courses-service
    - Add error handling and logging
    - Location: `backend/payment-service/src/main/java/com/englishflow/payment/client/CoursesServiceClient.java`
    - _Requirements: 5.1, 5.2, 5.3, 5.4_
  
  - [x] 5.2 Add unenrollment endpoints to courses-service
    - Create DELETE /api/enrollments/unenroll endpoint with studentId and courseId params
    - Create DELETE /api/pack-enrollments/unenroll endpoint with studentId and packId params
    - Implement logic to remove enrollment and related progress data
    - Add authorization checks (admin or system service)
    - Location: `backend/courses-service/src/main/java/com/englishflow/courses/controller/EnrollmentController.java`
    - _Requirements: 5.1, 5.2, 5.3_
  
  - [ ]* 5.3 Write integration tests for unenrollment endpoints
    - Test course unenrollment removes enrollment
    - Test pack unenrollment removes all course enrollments
    - Test authorization checks
    - _Requirements: 5.1, 5.2, 5.3_

- [x] 6. Checkpoint - Ensure infrastructure tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [x] 7. Implement core RefundService business logic
  - [x] 7.1 Create RefundService interface and implementation class
    - Define all service methods from design document
    - Inject RefundRepository, PaymentRepository, PaymeeClient, CoursesServiceClient
    - Add @Service and @Transactional annotations
    - Location: `backend/payment-service/src/main/java/com/englishflow/payment/service/RefundService.java`
    - _Requirements: All requirements_
  
  - [x] 7.2 Implement createRefundRequest method
    - Validate payment exists and status is SUCCESS
    - Check no existing refund for payment
    - Validate refund eligibility (time window, progress threshold)
    - Create refund entity with PENDING status
    - Save and return RefundDTO
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 2.1, 2.2, 2.3, 2.4_
  
  - [x] 7.3 Implement validateRefundEligibility method
    - Check payment date within 7-day window
    - For COURSE: check progress <= 30%
    - For PACK: check all course progress <= 30%
    - Throw appropriate exceptions with error messages
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_
  
  - [x] 7.4 Implement approveRefund method
    - Verify refund status is PENDING
    - Update status to APPROVED
    - Record adminId and approvedAt timestamp
    - Automatically trigger processRefund
    - Return RefundDTO
    - _Requirements: 3.2, 11.2_
  
  - [x] 7.5 Implement rejectRefund method
    - Verify refund status is PENDING
    - Update status to REJECTED
    - Record adminId, rejectedAt timestamp, and rejection reason
    - Return RefundDTO
    - _Requirements: 3.3, 3.5_
  
  - [x] 7.6 Implement processRefund method
    - Update status to PROCESSING
    - Call PaymeeClient to initiate refund
    - On success: update status to COMPLETED, record paymeeTransactionId and completedAt
    - On failure: update status to FAILED, record error message
    - Trigger reverseEnrollment on completion
    - _Requirements: 4.1, 4.2, 4.3, 4.4_
  
  - [x] 7.7 Implement reverseEnrollment method
    - Determine item type (COURSE or PACK)
    - Call CoursesServiceClient unenrollment method
    - Log errors but don't fail refund if unenrollment fails
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5_
  
  - [x] 7.8 Implement cancelRefund method
    - Verify refund status is PENDING or APPROVED
    - Verify requesting student owns the refund
    - Update status to CANCELLED
    - Record cancelledAt timestamp
    - Return RefundDTO
    - _Requirements: 9.1, 9.2, 9.3, 9.5_
  
  - [x] 7.9 Implement query methods
    - Implement getRefundById with authorization check
    - Implement getRefundsByStudent
    - Implement getAllRefunds with filtering
    - Implement getRefundStatistics
    - _Requirements: 7.1, 7.2, 7.3, 7.5_
  
  - [ ]* 7.10 Write unit tests for RefundService
    - Test createRefundRequest validates eligibility
    - Test approveRefund updates status correctly
    - Test rejectRefund records rejection reason
    - Test processRefund calls Paymee and handles responses
    - Test reverseEnrollment calls courses service
    - Test cancelRefund validates status
    - Mock all external dependencies
    - _Requirements: All requirements_

- [x] 8. Implement RefundController REST API
  - [x] 8.1 Create RefundController with student endpoints
    - POST /api/refunds - createRefundRequest
    - GET /api/refunds/my-refunds - getMyRefunds
    - GET /api/refunds/{id} - getRefundById
    - DELETE /api/refunds/{id}/cancel - cancelRefund
    - Extract studentId from JWT authentication
    - Add @PreAuthorize for STUDENT role
    - Location: `backend/payment-service/src/main/java/com/englishflow/payment/controller/RefundController.java`
    - _Requirements: 1.1, 7.1, 7.2, 9.1, 11.1_
  
  - [x] 8.2 Add admin endpoints to RefundController
    - GET /api/refunds - getAllRefunds with filters
    - PUT /api/refunds/{id}/approve - approveRefund
    - PUT /api/refunds/{id}/reject - rejectRefund (with rejection reason in body)
    - POST /api/refunds/{id}/process - manually trigger processRefund
    - GET /api/refunds/statistics - getStatistics
    - Add @PreAuthorize for ADMIN role
    - _Requirements: 3.1, 3.2, 3.3, 7.3, 7.5, 10.1, 10.2, 10.3, 10.4, 10.5, 11.2_
  
  - [x] 8.3 Add exception handling for refund operations
    - Create custom exceptions: RefundNotEligibleException, RefundNotFoundException, InvalidRefundStatusException
    - Add @ExceptionHandler methods in GlobalExceptionHandler
    - Return appropriate HTTP status codes and error messages
    - Location: `backend/payment-service/src/main/java/com/englishflow/payment/exception/`
    - _Requirements: 1.3, 1.4, 2.2, 3.5, 9.3, 11.3, 11.4_
  
  - [ ]* 8.4 Write integration tests for RefundController
    - Test student can create refund for own payment
    - Test student cannot create refund for other's payment
    - Test admin can approve/reject refunds
    - Test non-admin cannot approve/reject
    - Test filtering and statistics endpoints
    - _Requirements: 11.1, 11.2, 11.3, 11.4_

- [x] 9. Checkpoint - Ensure backend tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [x] 10. Implement frontend refund models and service
  - [x] 10.1 Create frontend refund models
    - Create RefundStatus enum
    - Create Refund interface matching RefundDTO
    - Create CreateRefundRequest interface
    - Create RefundFilter interface
    - Create RefundStats interface
    - Location: `frontend/src/app/core/models/refund.model.ts`
    - _Requirements: 1.1, 7.2, 7.3, 7.5_
  
  - [x] 10.2 Create RefundService for API calls
    - Implement createRefundRequest(request): Observable<Refund>
    - Implement getMyRefunds(): Observable<Refund[]>
    - Implement getRefundById(id): Observable<Refund>
    - Implement cancelRefund(id): Observable<void>
    - Implement getAllRefunds(filter?): Observable<Refund[]>
    - Implement approveRefund(id): Observable<Refund>
    - Implement rejectRefund(id, reason): Observable<Refund>
    - Implement getStatistics(): Observable<RefundStats>
    - Location: `frontend/src/app/core/services/refund.service.ts`
    - _Requirements: 1.1, 3.2, 3.3, 7.1, 7.3, 7.5, 9.1_
  
  - [ ]* 10.3 Write unit tests for RefundService
    - Mock HttpClient responses
    - Test all service methods call correct endpoints
    - Test error handling
    - _Requirements: All requirements_

- [x] 11. Implement student refund request component
  - [x] 11.1 Create refund request component structure
    - Generate component with routing
    - Add to student panel routes
    - Create component HTML template and styles
    - Location: `frontend/src/app/pages/student-panel/refund-request/`
    - _Requirements: 1.1, 10.1_
  
  - [x] 11.2 Implement refund request form logic
    - Load student's successful payments
    - Display payment details (item name, amount, date)
    - Show eligibility status for each payment
    - Add refund request form with reason textarea
    - Implement submitRefundRequest method calling RefundService
    - Add confirmation dialog before submission
    - Show success/error messages
    - _Requirements: 1.1, 1.2, 1.5, 2.1, 2.2, 2.3, 2.4_
  
  - [ ]* 11.3 Write unit tests for refund request component
    - Test component loads payments
    - Test form submission calls service
    - Test validation and error handling
    - _Requirements: 1.1, 1.2_

- [x] 12. Implement student refund history component
  - [x] 12.1 Create refund history component structure
    - Generate component with routing
    - Add to student panel routes
    - Create component HTML template with table/cards
    - Location: `frontend/src/app/pages/student-panel/refund-history/`
    - _Requirements: 7.1, 7.2_
  
  - [x] 12.2 Implement refund history display logic
    - Load refunds using RefundService.getMyRefunds()
    - Display refund list with status badges
    - Add filter by status dropdown
    - Implement viewRefundDetails modal
    - Implement cancelRefund with confirmation
    - Show status-specific actions (cancel button for PENDING/APPROVED)
    - _Requirements: 7.1, 7.2, 9.1, 9.2, 9.3_
  
  - [ ]* 12.3 Write unit tests for refund history component
    - Test component loads refund history
    - Test filtering works correctly
    - Test cancel action calls service
    - _Requirements: 7.1, 9.1_

- [x] 13. Implement admin refund management component
  - [x] 13.1 Create admin refund management component structure
    - Generate component with routing
    - Add to admin panel routes
    - Create component HTML template with table and filters
    - Location: `frontend/src/app/pages/admin-panel/refund-management/`
    - _Requirements: 10.1, 10.2, 10.3, 10.4, 10.5_
  
  - [x] 13.2 Implement refund list and filtering
    - Load all refunds using RefundService.getAllRefunds()
    - Add filter controls (status, date range, student, item type)
    - Add sorting by date, amount, status
    - Display refund table with all relevant columns
    - Add pagination if needed
    - _Requirements: 10.1, 10.2, 10.3_
  
  - [x] 13.3 Implement refund detail view and actions
    - Create refund detail modal showing complete information
    - Display payment history and enrollment status
    - Add approve button with confirmation
    - Add reject button with reason input dialog
    - Implement approveRefund and rejectRefund methods
    - Show success/error messages
    - Refresh list after actions
    - _Requirements: 3.1, 3.2, 3.3, 10.4, 10.5_
  
  - [x] 13.4 Implement refund statistics dashboard
    - Load statistics using RefundService.getStatistics()
    - Display cards with key metrics (total refunds, amounts, by status)
    - Add charts if desired (optional)
    - _Requirements: 7.5_
  
  - [ ]* 13.5 Write unit tests for admin refund management component
    - Test component loads refunds
    - Test filtering and sorting
    - Test approve/reject actions
    - Test statistics display
    - _Requirements: 10.1, 10.2, 10.3, 10.4, 10.5_

- [x] 14. Implement email notification system
  - [x] 14.1 Create email templates for refund notifications
    - Create refund-request-created.html template
    - Create refund-approved.html template
    - Create refund-rejected.html template
    - Create refund-completed.html template
    - Create refund-failed.html template
    - Location: `backend/payment-service/src/main/resources/templates/email/`
    - _Requirements: 1.5, 8.1, 8.2, 8.3, 8.4, 8.5_
  
  - [x] 14.2 Implement NotificationService for refund emails
    - Create or extend NotificationService with refund notification methods
    - Implement sendRefundRequestCreated(refund)
    - Implement sendRefundApproved(refund)
    - Implement sendRefundRejected(refund)
    - Implement sendRefundCompleted(refund)
    - Implement sendRefundFailed(refund)
    - Use JavaMailSender or existing email service
    - Make email sending asynchronous with @Async
    - Location: `backend/payment-service/src/main/java/com/englishflow/payment/service/NotificationService.java`
    - _Requirements: 1.5, 8.1, 8.2, 8.3, 8.4, 8.5_
  
  - [x] 14.3 Integrate notifications into RefundService
    - Call sendRefundRequestCreated in createRefundRequest
    - Call sendRefundApproved in approveRefund
    - Call sendRefundRejected in rejectRefund
    - Call sendRefundCompleted in processRefund (on success)
    - Call sendRefundFailed in processRefund (on failure)
    - Add error handling to not fail operations if email fails
    - _Requirements: 1.5, 3.4, 4.5, 8.1, 8.2, 8.3, 8.4, 8.5_
  
  - [ ]* 14.4 Write unit tests for notification integration
    - Test notifications are triggered at correct lifecycle events
    - Test email failures don't break refund operations
    - Mock email service
    - _Requirements: 1.5, 8.1, 8.2, 8.3, 8.4, 8.5_

- [x] 15. Add refund navigation and access control
  - [x] 15.1 Add refund routes to frontend routing
    - Add student refund routes to app.routes.ts
    - Add admin refund routes to app.routes.ts
    - Add route guards for role-based access
    - _Requirements: 11.1, 11.2_
  
  - [x] 15.2 Add refund menu items to navigation
    - Add "Request Refund" and "Refund History" to student sidebar
    - Add "Refund Management" to admin sidebar
    - Location: `frontend/src/app/shared/layout/app-sidebar/app-sidebar.component.ts`
    - _Requirements: 10.1, 11.1_

- [x] 16. Final checkpoint - Integration testing and verification
  - [x] 16.1 Test complete refund workflow end-to-end
    - Test student creates refund request
    - Test admin approves refund
    - Test Paymee integration (use sandbox)
    - Test enrollment reversal
    - Test email notifications
    - _Requirements: All requirements_
  
  - [x] 16.2 Verify security and authorization
    - Test students can only access own refunds
    - Test only admins can approve/reject
    - Test API endpoints have correct role restrictions
    - _Requirements: 11.1, 11.2, 11.3, 11.4, 11.5_
  
  - [x] 16.3 Test error scenarios
    - Test refund for non-existent payment
    - Test refund outside time window
    - Test refund with high progress
    - Test duplicate refund requests
    - Test Paymee API failures
    - Test enrollment reversal failures
    - _Requirements: 1.3, 1.4, 2.2, 3.5, 4.4, 5.4_

- [x] 17. Final checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional testing tasks and can be skipped for faster MVP
- The implementation uses Java/Spring Boot for backend and Angular/TypeScript for frontend
- Paymee integration uses manual refund workflow (admins mark as completed after processing in merchant dashboard)
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation at key milestones
- Email notifications are implemented last to avoid blocking core functionality
- Security and authorization are verified throughout implementation
