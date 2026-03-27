import { trigger, transition, style, animate, query, group } from '@angular/animations';

export const slideAnimation = trigger('routeAnimations', [
  transition('* <=> *', [
    query(':enter, :leave', [
      style({
        position: 'absolute',
        width: '100%',
        opacity: 0,
        transform: 'translateX(0)'
      })
    ], { optional: true }),
    
    query(':enter', [
      style({ 
        opacity: 0,
        transform: 'translateX(100%)' 
      })
    ], { optional: true }),
    
    group([
      query(':leave', [
        animate('300ms ease-out', style({ 
          opacity: 0,
          transform: 'translateX(-100%)' 
        }))
      ], { optional: true }),
      
      query(':enter', [
        animate('300ms ease-out', style({ 
          opacity: 1,
          transform: 'translateX(0)' 
        }))
      ], { optional: true })
    ])
  ])
]);

export const fadeAnimation = trigger('fadeAnimation', [
  transition(':enter', [
    style({ opacity: 0 }),
    animate('300ms ease-in', style({ opacity: 1 }))
  ]),
  transition(':leave', [
    animate('300ms ease-out', style({ opacity: 0 }))
  ])
]);

export const scaleAnimation = trigger('scaleAnimation', [
  transition(':enter', [
    style({ transform: 'scale(0.8)', opacity: 0 }),
    animate('200ms ease-out', style({ transform: 'scale(1)', opacity: 1 }))
  ]),
  transition(':leave', [
    animate('200ms ease-in', style({ transform: 'scale(0.8)', opacity: 0 }))
  ])
]);
