export interface Expense {
  id?: number;
  clubId: number;
  designation: string;
  amount: number;
  expenseDate: string;
  createdBy: number;
  createdByName?: string;
  notes?: string;
  source?: 'REGISTRATION_FEE' | 'SPONSORSHIP' | 'EVENT_FEE' | 'OTHER';
  createdAt?: string;
  updatedAt?: string;
}

export interface ExpenseStats {
  totalExpenses: number;
  expenseCount: number;
  averageExpense: number;
}
