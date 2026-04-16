import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ExamBuilderComponent } from './exam-builder.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

describe('ExamBuilderComponent', () => {
  let component: ExamBuilderComponent;
  let fixture: ComponentFixture<ExamBuilderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExamBuilderComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([])
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExamBuilderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
