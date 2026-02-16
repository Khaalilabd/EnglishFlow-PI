import { Component } from '@angular/core';

@Component({
  standalone: true,
  selector: 'app-sidebar-widget',
  template: `
    <div
      class="mx-auto mb-6 w-full max-w-60 rounded-xl bg-gradient-to-br from-[#F6BD60] to-[#C84630] px-4 py-5 text-center shadow-lg"
    >
      <div class="w-12 h-12 bg-white/20 rounded-full flex items-center justify-center mx-auto mb-3">
        <i class="fas fa-graduation-cap text-white text-2xl"></i>
      </div>
      <h3 class="mb-2 font-bold text-white text-lg">
        Need Help?
      </h3>
      <p class="mb-4 text-white/90 text-sm">
        Contact our support team for assistance with your platform.
      </p>
      <a
        href="/dashboard/support"
        class="flex items-center justify-center gap-2 p-3 font-medium text-[#2D5757] bg-white rounded-lg text-sm hover:bg-[#F7EDE2] transition-colors shadow-md"
      >
        <i class="fas fa-headset"></i>
        Get Support
      </a>
    </div>
  `
})
export class SidebarWidgetComponent {}
