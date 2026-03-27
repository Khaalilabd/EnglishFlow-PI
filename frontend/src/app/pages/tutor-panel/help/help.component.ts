import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-tutor-help',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="p-6 max-w-7xl mx-auto">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900">Help & Support</h1>
        <p class="text-gray-600 mt-2">Get help and find answers to your questions</p>
      </div>

      <!-- Quick Actions -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow cursor-pointer">
          <div class="w-12 h-12 bg-teal-100 rounded-lg flex items-center justify-center mb-4">
            <i class="fas fa-book text-teal-600 text-xl"></i>
          </div>
          <h3 class="text-lg font-semibold text-gray-900 mb-2">Documentation</h3>
          <p class="text-gray-600 text-sm">Browse our comprehensive guides and tutorials</p>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow cursor-pointer">
          <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mb-4">
            <i class="fas fa-comments text-blue-600 text-xl"></i>
          </div>
          <h3 class="text-lg font-semibold text-gray-900 mb-2">Live Chat</h3>
          <p class="text-gray-600 text-sm">Chat with our support team in real-time</p>
        </div>

        <div 
          routerLink="/tutor-panel/complaints"
          class="bg-white rounded-xl shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow cursor-pointer">
          <div class="w-12 h-12 bg-amber-100 rounded-lg flex items-center justify-center mb-4">
            <i class="fas fa-exclamation-triangle text-amber-600 text-xl"></i>
          </div>
          <h3 class="text-lg font-semibold text-gray-900 mb-2">Submit Complaint</h3>
          <p class="text-gray-600 text-sm">Report an issue or submit feedback</p>
        </div>
      </div>

      <!-- FAQ Section -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
        <h2 class="text-xl font-bold text-gray-900 mb-6">Frequently Asked Questions</h2>
        
        <div class="space-y-4">
          <!-- FAQ Item -->
          <div class="border-b border-gray-200 pb-4">
            <button class="w-full text-left flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-900">How do I create a new quiz?</h3>
              <i class="fas fa-chevron-down text-gray-400"></i>
            </button>
            <p class="text-gray-600 mt-2 text-sm">
              Navigate to Quiz Management and click on "Create New Quiz". Fill in the quiz details and add questions.
            </p>
          </div>

          <div class="border-b border-gray-200 pb-4">
            <button class="w-full text-left flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-900">How do I manage student complaints?</h3>
              <i class="fas fa-chevron-down text-gray-400"></i>
            </button>
            <p class="text-gray-600 mt-2 text-sm">
              Go to the Complaints section to view and respond to student complaints assigned to you.
            </p>
          </div>

          <div class="border-b border-gray-200 pb-4">
            <button class="w-full text-left flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-900">How do I update my profile?</h3>
              <i class="fas fa-chevron-down text-gray-400"></i>
            </button>
            <p class="text-gray-600 mt-2 text-sm">
              Click on your profile picture in the header and select "Settings" to update your information.
            </p>
          </div>

          <div class="pb-4">
            <button class="w-full text-left flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-900">How do I contact support?</h3>
              <i class="fas fa-chevron-down text-gray-400"></i>
            </button>
            <p class="text-gray-600 mt-2 text-sm">
              You can reach our support team at support&#64;jungleinenglish.com or use the live chat feature.
            </p>
          </div>
        </div>
      </div>

      <!-- Contact Support -->
      <div class="mt-8 bg-gradient-to-r from-teal-500 to-teal-600 rounded-xl shadow-sm p-8 text-white text-center">
        <h2 class="text-2xl font-bold mb-2">Still need help?</h2>
        <p class="mb-6">Our support team is here to assist you</p>
        <div class="flex gap-4 justify-center">
          <button class="px-6 py-3 bg-white text-teal-600 rounded-lg hover:bg-gray-100 transition-colors font-medium">
            <i class="fas fa-envelope mr-2"></i>
            Email Support
          </button>
          <button class="px-6 py-3 bg-teal-700 text-white rounded-lg hover:bg-teal-800 transition-colors font-medium">
            <i class="fas fa-comments mr-2"></i>
            Start Live Chat
          </button>
        </div>
      </div>
    </div>
  `
})
export class TutorHelpComponent {}
