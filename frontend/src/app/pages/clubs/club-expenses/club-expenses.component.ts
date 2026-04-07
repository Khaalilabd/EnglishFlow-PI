import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ExpenseService } from '../../../core/services/expense.service';
import { UserService } from '../../../core/services/user.service';
import { NotificationService } from '../../../core/services/notification.service';
import { MembershipRequestService } from '../../../core/services/membership-request.service';
import { Expense } from '../../../core/models/expense.model';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

@Component({
  selector: 'app-club-expenses',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './club-expenses.component.html',
  styleUrls: ['./club-expenses.component.scss']
})
export class ClubExpensesComponent implements OnInit {
  @Input() clubId!: number;
  @Input() currentUserId!: number;
  @Input() isTreasurer: boolean = false;

  expenses: Expense[] = [];
  loading = false;
  showModal = false;
  isEditMode = false;
  
  expenseForm: Expense = {
    clubId: 0,
    designation: '',
    amount: 0,
    expenseDate: '',
    createdBy: 0
  };

  totalExpenses = 0;
  totalRegistrationFees = 0;

  constructor(
    private expenseService: ExpenseService,
    private userService: UserService,
    private notificationService: NotificationService,
    private membershipRequestService: MembershipRequestService
  ) {}

  ngOnInit() {
    this.loadExpenses();
  }

  loadExpenses() {
    this.loading = true;
    this.expenseService.getExpensesByClub(this.clubId).subscribe({
      next: (expenses) => {
        this.enrichExpensesWithCreatorNames(expenses);
      },
      error: () => {
        console.error('Error loading expenses');
        this.loading = false;
      }
    });

    this.expenseService.getTotalExpenses(this.clubId).subscribe({
      next: (total) => {
        this.totalExpenses = total;
      },
      error: () => {
        console.error('Error loading total expenses');
      }
    });

    this.membershipRequestService.getTotalPayments(this.clubId).subscribe({
      next: (total) => {
        this.totalRegistrationFees = total;
      },
      error: () => {
        this.totalRegistrationFees = 0;
      }
    });
  }

  enrichExpensesWithCreatorNames(expenses: Expense[]) {
    if (expenses.length === 0) {
      this.expenses = [];
      this.loading = false;
      return;
    }

    let completed = 0;
    const enrichedExpenses: Expense[] = [];

    expenses.forEach(expense => {
      this.userService.getUserById(expense.createdBy).subscribe({
        next: (user) => {
          enrichedExpenses.push({
            ...expense,
            createdByName: `${user.firstName} ${user.lastName}`
          });
          completed++;
          if (completed === expenses.length) {
            this.expenses = enrichedExpenses.sort((a, b) => 
              new Date(b.expenseDate).getTime() - new Date(a.expenseDate).getTime()
            );
            this.loading = false;
          }
        },
        error: () => {
          enrichedExpenses.push({
            ...expense,
            createdByName: 'Unknown'
          });
          completed++;
          if (completed === expenses.length) {
            this.expenses = enrichedExpenses.sort((a, b) => 
              new Date(b.expenseDate).getTime() - new Date(a.expenseDate).getTime()
            );
            this.loading = false;
          }
        }
      });
    });
  }

  openCreateModal() {
    this.isEditMode = false;
    this.expenseForm = {
      clubId: this.clubId,
      designation: '',
      amount: 0,
      expenseDate: new Date().toISOString().slice(0, 16),
      createdBy: this.currentUserId
    };
    this.showModal = true;
  }

  openEditModal(expense: Expense) {
    this.isEditMode = true;
    this.expenseForm = {
      ...expense,
      expenseDate: new Date(expense.expenseDate).toISOString().slice(0, 16)
    };
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
    this.isEditMode = false;
  }

  saveExpense() {
    if (!this.expenseForm.designation.trim()) {
      this.notificationService.error('Missing Field', 'Please enter a designation');
      return;
    }

    if (this.expenseForm.amount <= 0) {
      this.notificationService.error('Invalid Amount', 'Amount must be greater than 0');
      return;
    }

    if (this.isEditMode && this.expenseForm.id) {
      this.expenseService.updateExpense(this.expenseForm.id, this.expenseForm).subscribe({
        next: () => {
          this.notificationService.success('Expense Updated', 'Expense has been updated successfully');
          this.closeModal();
          this.loadExpenses();
        },
        error: () => {
          this.notificationService.error('Update Failed', 'Failed to update expense');
        }
      });
    } else {
      this.expenseService.createExpense(this.expenseForm).subscribe({
        next: () => {
          this.notificationService.success('Expense Added', 'Expense has been added successfully');
          this.closeModal();
          this.loadExpenses();
        },
        error: (err) => {
          this.notificationService.error('Creation Failed', 'Failed to create expense');
        }
      });
    }
  }

  deleteExpense(expense: Expense) {
    if (confirm(`Are you sure you want to delete this expense: ${expense.designation}?`)) {
      this.expenseService.deleteExpense(expense.id!).subscribe({
        next: () => {
          this.notificationService.success('Expense Deleted', 'Expense has been deleted successfully');
          this.loadExpenses();
        },
        error: (err) => {
          this.notificationService.error('Delete Failed', 'Failed to delete expense');
        }
      });
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  exportExpenses() {
    if (this.expenses.length === 0) {
      this.notificationService.warning('No Data', 'No expenses to export');
      return;
    }

    const currentDate = new Date().toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });

    // Create PDF document in A4 format
    const doc = new jsPDF({
      orientation: 'portrait',
      unit: 'mm',
      format: 'a4'
    });

    const pageWidth = doc.internal.pageSize.getWidth();
    const pageHeight = doc.internal.pageSize.getHeight();
    const margin = 15;

    // Modern Header with gradient effect (simulated with rectangles)
    doc.setFillColor(45, 87, 87);
    doc.rect(0, 0, pageWidth, 45, 'F');
    
    // Accent line
    doc.setFillColor(246, 189, 96);
    doc.rect(0, 45, pageWidth, 3, 'F');

    // Title
    doc.setTextColor(255, 255, 255);
    doc.setFontSize(24);
    doc.setFont('helvetica', 'bold');
    doc.text('CLUB EXPENSES', pageWidth / 2, 20, { align: 'center' });
    
    doc.setFontSize(14);
    doc.setFont('helvetica', 'normal');
    doc.text('Financial Report', pageWidth / 2, 30, { align: 'center' });

    // Info cards section
    let yPos = 58;
    
    // Card 1: Export Date
    doc.setFillColor(248, 250, 252);
    doc.roundedRect(margin, yPos, (pageWidth - 3 * margin) / 2, 18, 2, 2, 'F');
    doc.setDrawColor(226, 232, 240);
    doc.roundedRect(margin, yPos, (pageWidth - 3 * margin) / 2, 18, 2, 2, 'S');
    
    doc.setTextColor(100, 116, 139);
    doc.setFontSize(9);
    doc.setFont('helvetica', 'normal');
    doc.text('EXPORT DATE', margin + 3, yPos + 6);
    
    doc.setTextColor(30, 41, 59);
    doc.setFontSize(10);
    doc.setFont('helvetica', 'bold');
    doc.text(currentDate, margin + 3, yPos + 13);

    // Card 2: Total Amount
    const card2X = margin + (pageWidth - 3 * margin) / 2 + margin;
    doc.setFillColor(254, 243, 199);
    doc.roundedRect(card2X, yPos, (pageWidth - 3 * margin) / 2, 18, 2, 2, 'F');
    doc.setDrawColor(251, 191, 36);
    doc.roundedRect(card2X, yPos, (pageWidth - 3 * margin) / 2, 18, 2, 2, 'S');
    
    doc.setTextColor(146, 64, 14);
    doc.setFontSize(9);
    doc.setFont('helvetica', 'normal');
    doc.text('TOTAL AMOUNT', card2X + 3, yPos + 6);
    
    doc.setTextColor(45, 87, 87);
    doc.setFontSize(14);
    doc.setFont('helvetica', 'bold');
    doc.text(`${this.totalExpenses.toFixed(2)} DT`, card2X + 3, yPos + 14);

    yPos += 25;

    // Section title
    doc.setTextColor(45, 87, 87);
    doc.setFontSize(12);
    doc.setFont('helvetica', 'bold');
    doc.text('Expense Details', margin, yPos);
    
    // Decorative line
    doc.setDrawColor(246, 189, 96);
    doc.setLineWidth(0.5);
    doc.line(margin, yPos + 2, margin + 40, yPos + 2);

    yPos += 8;

    // Prepare table data
    const tableData = this.expenses.map((expense, index) => [
      (index + 1).toString(),
      expense.designation,
      `${expense.amount.toFixed(2)}`,
      this.formatDate(expense.expenseDate),
      expense.createdByName || 'Unknown',
      expense.notes || '-'
    ]);

    // Generate modern table
    autoTable(doc, {
      startY: yPos,
      head: [['#', 'Designation', 'Amount (DT)', 'Date', 'Created By', 'Notes']],
      body: tableData,
      theme: 'striped',
      headStyles: {
        fillColor: [45, 87, 87],
        textColor: [255, 255, 255],
        fontStyle: 'bold',
        fontSize: 10,
        halign: 'center',
        cellPadding: 4
      },
      bodyStyles: {
        textColor: [51, 65, 85],
        fontSize: 9,
        cellPadding: 3
      },
      alternateRowStyles: {
        fillColor: [248, 250, 252]
      },
      columnStyles: {
        0: { halign: 'center', cellWidth: 12, fontStyle: 'bold', textColor: [100, 116, 139] },
        1: { cellWidth: 55 },
        2: { halign: 'right', cellWidth: 25, fontStyle: 'bold', textColor: [45, 87, 87] },
        3: { cellWidth: 28, halign: 'center' },
        4: { cellWidth: 35 },
        5: { cellWidth: 35, fontSize: 8, textColor: [100, 116, 139] }
      },
      margin: { left: margin, right: margin }
    });

    // Summary section with modern design
    const finalY = (doc as any).lastAutoTable.finalY + 10;
    
    // Check if we need a new page
    if (finalY + 30 > pageHeight - margin) {
      doc.addPage();
      yPos = margin;
    } else {
      yPos = finalY;
    }

    // Summary box
    const summaryBoxHeight = 20;
    doc.setFillColor(45, 87, 87);
    doc.roundedRect(margin, yPos, pageWidth - 2 * margin, summaryBoxHeight, 3, 3, 'F');
    
    // Summary content
    doc.setTextColor(255, 255, 255);
    doc.setFontSize(11);
    doc.setFont('helvetica', 'normal');
    doc.text('TOTAL EXPENSES', margin + 5, yPos + 8);
    
    doc.setFontSize(18);
    doc.setFont('helvetica', 'bold');
    doc.text(`${this.totalExpenses.toFixed(2)} DT`, pageWidth - margin - 5, yPos + 13, { align: 'right' });

    // Footer
    const footerY = pageHeight - 15;
    doc.setDrawColor(226, 232, 240);
    doc.setLineWidth(0.3);
    doc.line(margin, footerY, pageWidth - margin, footerY);
    
    doc.setTextColor(148, 163, 184);
    doc.setFontSize(8);
    doc.setFont('helvetica', 'normal');
    doc.text('Generated by Club Management System', pageWidth / 2, footerY + 5, { align: 'center' });
    doc.text(`Page 1 | ${this.expenses.length} records`, pageWidth / 2, footerY + 9, { align: 'center' });

    // Save PDF
    const fileDate = new Date().toISOString().split('T')[0];
    doc.save(`club-expenses-report-${fileDate}.pdf`);

    this.notificationService.success('Export Successful', 'Expenses report exported as PDF successfully');
  }
}
