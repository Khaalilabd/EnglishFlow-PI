import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-subscription',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-6">
      <h1 class="text-3xl font-bold text-gray-900">My Subscription</h1>

      <!-- Current Plan -->
      <div class="bg-gradient-to-r from-[#2D5757] to-[#3D3D60] rounded-xl p-6 text-white">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm opacity-90 mb-1">Current Plan</p>
            <h2 class="text-3xl font-bold mb-2">Premium Access</h2>
            <p class="text-sm opacity-90">Access to all courses and features</p>
          </div>
          <div class="text-right">
            <p class="text-sm opacity-90 mb-1">Valid Until</p>
            <p class="text-2xl font-bold">Dec 31, 2026</p>
          </div>
        </div>
      </div>

      <!-- Payment History -->
      <div class="bg-white rounded-xl shadow-sm p-6">
        <h2 class="text-xl font-bold text-gray-900 mb-4">Payment History</h2>
        <div class="space-y-3">
          <div *ngFor="let payment of paymentHistory" 
               class="flex items-center justify-between p-4 border border-gray-200 rounded-lg">
            <div>
              <p class="font-semibold text-gray-900">{{ payment.description }}</p>
              <p class="text-sm text-gray-600">{{ payment.date }}</p>
            </div>
            <div class="text-right">
              <p class="font-bold text-gray-900">{{ payment.amount }}</p>
              <span class="text-xs px-2 py-1 rounded-full"
                    [ngClass]="{
                      'bg-green-100 text-green-700': payment.status === 'paid',
                      'bg-yellow-100 text-yellow-700': payment.status === 'pending'
                    }">
                {{ payment.status }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Available Plans -->
      <div class="bg-white rounded-xl shadow-sm p-6">
        <h2 class="text-xl font-bold text-gray-900 mb-4">Available Plans</h2>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div *ngFor="let plan of plans" 
               class="border-2 rounded-xl p-6"
               [ngClass]="plan.popular ? 'border-[#F59E0B]' : 'border-gray-200'">
            <div *ngIf="plan.popular" class="text-center mb-3">
              <span class="bg-[#F59E0B] text-white px-3 py-1 rounded-full text-xs font-semibold">
                Most Popular
              </span>
            </div>
            <h3 class="text-xl font-bold text-gray-900 mb-2">{{ plan.name }}</h3>
            <p class="text-3xl font-bold text-gray-900 mb-1">{{ plan.price }}</p>
            <p class="text-sm text-gray-600 mb-4">{{ plan.period }}</p>
            <ul class="space-y-2 mb-6">
              <li *ngFor="let feature of plan.features" class="flex items-center gap-2 text-sm">
                <svg class="w-5 h-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                </svg>
                {{ feature }}
              </li>
            </ul>
            <button class="w-full py-2 rounded-lg font-semibold transition-colors"
                    [ngClass]="plan.popular ? 'bg-[#F59E0B] text-white hover:bg-[#e5ac4f]' : 'bg-gray-200 text-gray-700 hover:bg-gray-300'">
              {{ plan.current ? 'Current Plan' : 'Upgrade' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class SubscriptionComponent {
  paymentHistory = [
    { description: 'Premium Subscription', date: 'Jan 1, 2026', amount: '$99.00', status: 'paid' },
    { description: 'Registration Fee', date: 'Dec 15, 2025', amount: '$50.00', status: 'paid' }
  ];

  plans = [
    {
      name: 'Basic',
      price: '$29',
      period: 'per month',
      features: ['3 Courses', 'Basic Support', 'Mobile Access'],
      popular: false,
      current: false
    },
    {
      name: 'Premium',
      price: '$99',
      period: 'per month',
      features: ['All Courses', 'Priority Support', 'Mobile & Desktop', 'Certificates', 'Live Sessions'],
      popular: true,
      current: true
    },
    {
      name: 'Enterprise',
      price: '$199',
      period: 'per month',
      features: ['All Premium Features', '1-on-1 Tutoring', 'Custom Learning Path', 'Team Access'],
      popular: false,
      current: false
    }
  ];
}
