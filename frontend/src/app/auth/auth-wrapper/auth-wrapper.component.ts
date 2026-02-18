import { Component } from '@angular/core';
import { RouterOutlet, ChildrenOutletContexts } from '@angular/router';
import { slideAnimation } from '../auth-animations';

@Component({
  selector: 'app-auth-wrapper',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <div [@routeAnimations]="getRouteAnimationData()" class="relative overflow-hidden">
      <router-outlet></router-outlet>
    </div>
  `,
  animations: [slideAnimation],
  styles: [`
    :host {
      display: block;
      position: relative;
      width: 100%;
      min-height: 100vh;
      overflow: hidden;
    }
  `]
})
export class AuthWrapperComponent {
  constructor(private contexts: ChildrenOutletContexts) {}

  getRouteAnimationData() {
    return this.contexts.getContext('primary')?.route?.snapshot?.data?.['animation'];
  }
}
