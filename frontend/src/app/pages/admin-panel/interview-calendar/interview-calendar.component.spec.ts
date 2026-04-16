import { ComponentFixture, TestBed } from '@angular/core/testing';
import { InterviewCalendarComponent } from './interview-calendar.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

describe('InterviewCalendarComponent', () => {
  let component: InterviewCalendarComponent;
  let fixture: ComponentFixture<InterviewCalendarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InterviewCalendarComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([])
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InterviewCalendarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
