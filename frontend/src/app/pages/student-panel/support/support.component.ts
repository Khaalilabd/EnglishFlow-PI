import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-support',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-6">
      <h1 class="text-3xl font-bold text-gray-900">Help & Support</h1>

      <!-- Quick Actions -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div class="bg-white rounded-xl shadow-sm p-6 hover:shadow-lg transition-shadow cursor-pointer">
          <div class="text-4xl mb-3">📚</div>
          <h3 class="text-lg font-bold text-gray-900 mb-2">Knowledge Base</h3>
          <p class="text-gray-600 text-sm">Browse articles and tutorials</p>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-6 hover:shadow-lg transition-shadow cursor-pointer">
          <div class="text-4xl mb-3">💬</div>
          <h3 class="text-lg font-bold text-gray-900 mb-2">Live Chat</h3>
          <p class="text-gray-600 text-sm">Chat with our support team</p>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-6 hover:shadow-lg transition-shadow cursor-pointer">
          <div class="text-4xl mb-3">📧</div>
          <h3 class="text-lg font-bold text-gray-900 mb-2">Email Support</h3>
          <p class="text-gray-600 text-sm">Send us an email</p>
        </div>
      </div>

      <!-- FAQ -->
      <div class="bg-white rounded-xl shadow-sm p-6">
        <h2 class="text-xl font-bold text-gray-900 mb-4">Frequently Asked Questions</h2>
        <div class="space-y-4">
          <div *ngFor="let faq of faqs" class="border-b border-gray-200 pb-4 last:border-0">
            <h3 class="font-semibold text-gray-900 mb-2">{{ faq.question }}</h3>
            <p class="text-gray-600 text-sm">{{ faq.answer }}</p>
          </div>
        </div>
      </div>

      <!-- Contact Info -->
      <div class="bg-gradient-to-br from-[#2D5757] to-[#3D3D60] rounded-xl p-6 text-white">
        <h2 class="text-xl font-bold mb-4">Contact Information</h2>
        <div class="space-y-3">
          <div class="flex items-center gap-3">
            <span class="text-2xl">📧</span>
            <span>support&#64;jungleinenglish.com</span>
          </div>
          <div class="flex items-center gap-3">
            <span class="text-2xl">📞</span>
            <span>+1 (555) 123-4567</span>
          </div>
          <div class="flex items-center gap-3">
            <span class="text-2xl">⏰</span>
            <span>Monday - Friday, 9:00 AM - 6:00 PM</span>
          </div>
        </div>
      </div>
    </div>
  `
})
export class SupportComponent {
  faqs = [
    {
      question: 'How do I reset my password?',
      answer: 'Go to Settings > Security > Change Password to reset your password.'
    },
    {
      question: 'How can I access course materials?',
      answer: 'Navigate to My Courses and click on any course to access all materials, videos, and assignments.'
    },
    {
      question: 'What if I miss a live class?',
      answer: 'All live classes are recorded and available in your course dashboard within 24 hours.'
    },
    {
      question: 'How do I contact my instructor?',
      answer: 'You can message your instructor directly through the Messages section or during office hours.'
    }
  ];
}
