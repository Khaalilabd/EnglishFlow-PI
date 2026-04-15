export interface Payment {
  id: number;
  orderId: string;
  studentId: number;
  studentName: string;
  studentEmail: string;
  itemType: 'COURSE' | 'PACK';
  itemId: number;
  itemName: string;
  amount: number;
  status: 'PENDING' | 'SUCCESS' | 'FAILED' | 'CANCELLED';
  transactionId?: number;
  receivedAmount?: number;
  cost?: number;
  paymentUrl?: string;
  createdAt: string;
  updatedAt: string;
}

export interface PaymentStats {
  totalPayments: number;
  successfulPayments: number;
  pendingPayments: number;
  failedPayments: number;
  totalRevenue: number;
}

export interface InitiatePaymentRequest {
  studentId: number;
  studentName: string;
  studentEmail: string;
  studentPhone?: string;
  itemType: 'COURSE' | 'PACK';
  itemId: number;
  itemName: string;
  amount: number;
}
