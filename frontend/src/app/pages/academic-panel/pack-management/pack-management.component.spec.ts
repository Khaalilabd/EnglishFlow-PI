import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PackManagementComponent } from './pack-management.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('PackManagementComponent', () => {
  let component: PackManagementComponent;
  let fixture: ComponentFixture<PackManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PackManagementComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PackManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
