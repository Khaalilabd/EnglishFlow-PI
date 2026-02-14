import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="space-y-6">
      <h1 class="text-3xl font-bold text-gray-900">Settings</h1>

      <!-- Profile Settings -->
      <div class="bg-white rounded-xl shadow-sm p-6">
        <h2 class="text-xl font-bold text-gray-900 mb-4">Profile Information</h2>
        <div class="flex items-center gap-6 mb-6">
          <img src="https://i.pravatar.cc/150?img=12" alt="Profile" class="w-24 h-24 rounded-full">
          <div>
            <button class="px-4 py-2 bg-[#F6BD60] text-white rounded-lg hover:bg-[#e5ac4f] transition-colors mr-2">
              Change Photo
            </button>
            <button class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50">
              Remove
            </button>
          </div>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">First Name</label>
            <input type="text" value="Shariar" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-[#F6BD60]">
          </div>
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">Last Name</label>
            <input type="text" value="Hossain" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-[#F6BD60]">
          </div>
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">Email</label>
            <input type="email" value="shariar@example.com" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-[#F6BD60]">
          </div>
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">Phone</label>
            <input type="tel" value="+1 (555) 123-4567" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-[#F6BD60]">
          </div>
        </div>
        
        <button class="mt-4 px-6 py-2 bg-[#2D5757] text-white rounded-lg hover:bg-[#3D3D60] transition-colors">
          Save Changes
        </button>
      </div>

      <!-- Notification Settings -->
      <div class="bg-white rounded-xl shadow-sm p-6">
        <h2 class="text-xl font-bold text-gray-900 mb-4">Notifications</h2>
        <div class="space-y-4">
          <div class="flex items-center justify-between">
            <div>
              <h3 class="font-semibold text-gray-900">Email Notifications</h3>
              <p class="text-sm text-gray-600">Receive email about your account activity</p>
            </div>
            <label class="relative inline-flex items-center cursor-pointer">
              <input type="checkbox" checked class="sr-only peer">
              <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-[#F6BD60]/20 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-[#F6BD60]"></div>
            </label>
          </div>
          
          <div class="flex items-center justify-between">
            <div>
              <h3 class="font-semibold text-gray-900">Push Notifications</h3>
              <p class="text-sm text-gray-600">Receive push notifications on your device</p>
            </div>
            <label class="relative inline-flex items-center cursor-pointer">
              <input type="checkbox" checked class="sr-only peer">
              <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-[#F6BD60]/20 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-[#F6BD60]"></div>
            </label>
          </div>
          
          <div class="flex items-center justify-between">
            <div>
              <h3 class="font-semibold text-gray-900">Course Updates</h3>
              <p class="text-sm text-gray-600">Get notified about new lessons and materials</p>
            </div>
            <label class="relative inline-flex items-center cursor-pointer">
              <input type="checkbox" checked class="sr-only peer">
              <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-[#F6BD60]/20 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-[#F6BD60]"></div>
            </label>
          </div>
        </div>
      </div>

      <!-- Security Settings -->
      <div class="bg-white rounded-xl shadow-sm p-6">
        <h2 class="text-xl font-bold text-gray-900 mb-4">Security</h2>
        <div class="space-y-4">
          <button class="w-full text-left px-4 py-3 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">
            <h3 class="font-semibold text-gray-900">Change Password</h3>
            <p class="text-sm text-gray-600">Update your password regularly</p>
          </button>
          
          <button class="w-full text-left px-4 py-3 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">
            <h3 class="font-semibold text-gray-900">Two-Factor Authentication</h3>
            <p class="text-sm text-gray-600">Add an extra layer of security</p>
          </button>
        </div>
      </div>
    </div>
  `
})
export class SettingsComponent {}
