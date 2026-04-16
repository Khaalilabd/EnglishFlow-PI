import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TutorAvailabilityComponent } from './tutor-availability.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('TutorAvailabilityComponent', () => {
  let component: TutorAvailabilityComponent;
  let fixture: ComponentFixture<TutorAvailabilityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TutorAvailabilityComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TutorAvailabilityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
