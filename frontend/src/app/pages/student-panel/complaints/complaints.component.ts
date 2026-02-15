import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-complaints',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="space-y-6">
      <h1 class="text-3xl font-bold text-gray-900">Submit a Complaint</h1>

      <!-- New Complaint Form -->
      <div class="bg-white rounded-xl shadow-sm p-6">
        <h2 class="text-xl font-bold text-gray-900 mb-4">New Complaint</h2>
        <form class="space-y-4">
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">Subject</label>
            <input 
              type="text" 
              [(ngModel)]="newComplaint.subject"
              name="subject"
              placeholder="Brief description of your issue"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-[#F59E0B]"
            />
          </div>

          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">Category</label>
            <select 
              [(ngModel)]="newComplaint.category"
              name="category"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-[#F59E0B]"
            >
              <option value="">Select a category</option>
              <option value="technical">Technical Issue</option>
              <option value="course">Course Content</option>
              <option value="instructor">Instructor</option>
              <option value="payment">Payment</option>
              <option value="other">Other</option>
            </select>
          </div>

          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">Description</label>
            <textarea 
              [(ngModel)]="newComplaint.description"
              name="description"
              rows="5"
              placeholder="Please provide detailed information about your complaint"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-[#F59E0B]"
            ></textarea>
          </div>

          <button 
            type="submit"
            class="px-6 py-3 bg-[#F59E0B] text-white rounded-lg hover:bg-[#e5ac4f] transition-colors font-semibold"
          >
            Submit Complaint
          </button>
        </form>
      </div>

      <!-- Previous Complaints -->
      <div class="bg-white rounded-xl shadow-sm p-6">
        <h2 class="text-xl font-bold text-gray-900 mb-4">My Complaints</h2>
        <div class="space-y-4">
          <div *ngFor="let complaint of complaints" 
               class="border border-gray-200 rounded-lg p-4 hover:border-[#F59E0B] transition-colors">
            <div class="flex items-start justify-between mb-2">
              <div>
                <h3 class="font-semibold text-gray-900">{{ complaint.subject }}</h3>
                <p class="text-sm text-gray-600">{{ complaint.category }}</p>
              </div>
              <span class="px-3 py-1 rounded-full text-xs font-semibold"
                    [ngClass]="{
                      'bg-yellow-100 text-yellow-700': complaint.status === 'pending',
                      'bg-blue-100 text-blue-700': complaint.status === 'in-progress',
                      'bg-green-100 text-green-700': complaint.status === 'resolved'
                    }">
                {{ complaint.status }}
              </span>
            </div>
            <p class="text-gray-700 mb-2">{{ complaint.description }}</p>
            <p class="text-sm text-gray-500">Submitted: {{ complaint.date }}</p>
            <div *ngIf="complaint.response" class="mt-3 p-3 bg-blue-50 rounded-lg">
              <p class="text-sm font-semibold text-blue-900 mb-1">Response:</p>
              <p class="text-sm text-blue-800">{{ complaint.response }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class ComplaintsComponent {
  newComplaint = {
    subject: '',
    category: '',
    description: ''
  };

  complaints = [
    {
      subject: 'Video not loading',
      category: 'Technical Issue',
      description: 'The video in lesson 3 is not loading properly',
      status: 'resolved',
      date: 'Feb 10, 2026',
      response: 'We have fixed the video loading issue. Please try again.'
    },
    {
      subject: 'Assignment deadline',
      category: 'Course Content',
      description: 'Request for extension on assignment due to personal reasons',
      status: 'in-progress',
      date: 'Feb 12, 2026',
      response: null
    }
  ];
}
