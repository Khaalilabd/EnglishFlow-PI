package com.englishflow.club.enums;

public enum MembershipRequestStatus {
    PENDING,
    PAYMENT_PENDING,  // Approuvé, en attente du paiement (accès provisoire 3 jours)
    APPROVED,
    REJECTED,
    EXPIRED           // Délai de paiement dépassé, membre retiré automatiquement
}
