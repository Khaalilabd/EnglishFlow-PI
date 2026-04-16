import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PackCreateComponent } from './pack-create.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

describe('PackCreateComponent', () => {
  let component: PackCreateComponent;
  let fixture: ComponentFixture<PackCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PackCreateComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([])
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PackCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
