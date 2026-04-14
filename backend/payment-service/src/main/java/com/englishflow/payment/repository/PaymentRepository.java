package com.englishflow.payment.repository;

import com.englishflow.payment.entity.Payment;
import com.englishflow.payment.enums.PaymentItemType;
import com.englishflow.payment.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(String orderId);

    Optional<Payment> findByToken(String token);

    List<Payment> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    List<Payment> findAllByOrderByCreatedAtDesc();

    List<Payment> findByStatusOrderByCreatedAtDesc(PaymentStatus status);

    List<Payment> findByItemTypeAndItemIdOrderByCreatedAtDesc(PaymentItemType itemType, Long itemId);

    @Query(value = "SELECT COUNT(*) FROM payments WHERE student_id = :studentId AND item_type = :itemType AND item_id = :itemId AND status = :status", nativeQuery = true)
    long countByStudentIdAndItemTypeAndItemIdAndStatus(
            @Param("studentId") Long studentId,
            @Param("itemType") String itemType,
            @Param("itemId") Long itemId,
            @Param("status") String status);

    @Query("SELECT p FROM Payment p WHERE p.studentId = :studentId AND p.itemType = :itemType AND p.itemId = :itemId AND p.status = :status ORDER BY p.createdAt DESC")
    Optional<Payment> findFirstByStudentIdAndItemTypeAndItemIdAndStatus(
            @Param("studentId") Long studentId,
            @Param("itemType") PaymentItemType itemType,
            @Param("itemId") Long itemId,
            @Param("status") PaymentStatus status);

    @Query(value = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE status = 'SUCCESS'", nativeQuery = true)
    BigDecimal sumSuccessfulPayments();

    @Query(value = "SELECT COUNT(*) FROM payments WHERE status = 'SUCCESS'", nativeQuery = true)
    long countSuccessfulPayments();
}
