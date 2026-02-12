
import { Component, Input } from '@angular/core';

@Component({
  standalone: true,
  selector: 'app-component-card',
  imports: [],
  templateUrl: './component-card.component.html',
  styles: ``
})
export class ComponentCardComponent {

  @Input() title!: string;
  @Input() desc: string = '';
  @Input() className: string = '';
}
