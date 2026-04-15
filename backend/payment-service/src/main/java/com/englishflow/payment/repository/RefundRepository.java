package com.englishflow.payment.repository;

import com.englishflow.payment.entity.Refund;
import com.englishflow.payment.enums.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for Refund entity.
 * Provides data access methods for refund operations including
 * custom queries for filtering and statistics.
 */
public interface RefundRepository extends JpaRepository<Refund, Long> {

    /**
     * Find all refunds for a specific student ordered by request date descending.
     *
     * @param studentId the student ID
     * @return list of refunds for the student
     */
    List<Refund> findByStudentIdOrderByRequestedAtDesc(Long studentId);

    /**
     * Find all refunds with a specific status ordered by request date descending.
     *
     * @param status the refund status
     * @return list of refunds with the given status
     */
    List<Refund> findByStatusOrderByRequestedAtDesc(RefundStatus status);

    /**
     * Find all refunds for a specific payment.
     *
     * @param paymentId the payment ID
     * @return list of refunds for the payment
     */
    @Query("SELECT r FROM Refund r WHERE r.payment.id = :paymentId ORDER BY r.requestedAt DESC")
    List<Refund> findByPaymentId(@Param("paymentId") Long paymentId);

    /**
     * Count refunds by status.
     *
     * @param status the refund status
     * @return count of refunds with the given status
     */
    long countByStatus(RefundStatus status);

    /**
     * Sum the total amount of refunds by status.
     *
     * @param status the refund status
     * @return total amount of refunds with the given status, or 0 if none
     */
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM Refund r WHERE r.status = :status")
    BigDecimal sumAmountByStatus(@Param("status") RefundStatus status);

    /**
     * Find all refunds ordered by request date descending.
     *
     * @return list of all refunds
     */
    List<Refund> findAllByOrderByRequestedAtDesc();

    /**
     * Check if a refund exists for a specific payment with a specific status.
     *
     * @param paymentId the payment ID
     * @param status the refund status
     * @return true if a refund exists, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Refund r WHERE r.payment.id = :paymentId AND r.status = :status")
    boolean existsByPaymentIdAndStatus(@Param("paymentId") Long paymentId, @Param("status") RefundStatus status);

    /**
     * Check if any refund exists for a specific payment (excluding REJECTED and CANCELLED).
     *
     * @param paymentId the payment ID
     * @return true if an active refund exists, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Refund r WHERE r.payment.id = :paymentId AND r.status NOT IN ('REJECTED', 'CANCELLED')")
    boolean existsActiveRefundByPaymentId(@Param("paymentId") Long paymentId);

    /**
     * Check if a refund exists for a specific payment excluding certain statuses.
     *
     * @param paymentId the payment ID
     * @param statuses the statuses to exclude
     * @return true if a refund exists, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Refund r WHERE r.payment.id = :paymentId AND r.status NOT IN :statuses")
    boolean existsByPaymentIdAndStatusNotIn(@Param("paymentId") Long paymentId, @Param("statuses") List<RefundStatus> statuses);
}
