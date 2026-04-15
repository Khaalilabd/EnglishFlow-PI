// Refund Status enum matching backend
export enum RefundStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  PROCESSING = 'PROCESSING',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED',
  CANCELLED = 'CANCELLED'
}

// Main Refund interface matching backend RefundDTO
export interface Refund {
  id: number;
  paymentId: number;
  orderId: string;
  studentId: number;
  studentName?: string;
  studentEmail?: string;
  itemType: string;
  itemId: number;
  itemName?: string;
  amount: number;
  status: RefundStatus;
  reason?: string;
  requestedAt: string;
  approvedAt?: string;
  rejectedAt?: string;
  processingAt?: string;
  completedAt?: string;
  cancelledAt?: string;
  adminId?: number;
  rejectionReason?: string;
  paymeeTransactionId?: string;
  errorMessage?: string;
  createdAt?: string;
  updatedAt?: string;
}

// DTO for creating refund requests
export interface CreateRefundRequest {
  paymentId: number;
  reason?: string;
}

// DTO for filtering refund queries
export interface RefundFilter {
  status?: RefundStatus;
  studentId?: number;
  startDate?: string; // ISO date string
  endDate?: string; // ISO date string
  itemType?: string;
}

// DTO for refund statistics
export interface RefundStats {
  totalRefunds: number;
  pendingRefunds: number;
  approvedRefunds: number;
  completedRefunds: number;
  rejectedRefunds: number;
  failedRefunds: number;
  totalRefundAmount: number;
  completedRefundAmount: number;
}
