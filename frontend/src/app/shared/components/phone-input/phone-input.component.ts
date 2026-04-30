import { Component, Input, forwardRef, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, NG_VALUE_ACCESSOR, ReactiveFormsModule, FormsModule } from '@angular/forms';

export interface Country {
  name: string;
  code: string;
  dialCode: string;
  flag: string;
}

@Component({
  selector: 'app-phone-input',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './phone-input.component.html',
  styleUrls: ['./phone-input.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => PhoneInputComponent),
      multi: true
    }
  ]
})
export class PhoneInputComponent implements ControlValueAccessor, OnInit {
  @Input() label: string = 'Phone Number';
  @Input() required: boolean = false;
  @Input() placeholder: string = 'Enter phone number';
  
  countries: Country[] = [
    { name: 'Morocco', code: 'MA', dialCode: '+212', flag: '🇲🇦' },
    { name: 'France', code: 'FR', dialCode: '+33', flag: '🇫🇷' },
    { name: 'United States', code: 'US', dialCode: '+1', flag: '🇺🇸' },
    { name: 'United Kingdom', code: 'GB', dialCode: '+44', flag: '🇬🇧' },
    { name: 'Spain', code: 'ES', dialCode: '+34', flag: '🇪🇸' },
    { name: 'Germany', code: 'DE', dialCode: '+49', flag: '🇩🇪' },
    { name: 'Italy', code: 'IT', dialCode: '+39', flag: '🇮🇹' },
    { name: 'Belgium', code: 'BE', dialCode: '+32', flag: '🇧🇪' },
    { name: 'Netherlands', code: 'NL', dialCode: '+31', flag: '🇳🇱' },
    { name: 'Switzerland', code: 'CH', dialCode: '+41', flag: '🇨🇭' },
    { name: 'Canada', code: 'CA', dialCode: '+1', flag: '🇨🇦' },
    { name: 'Algeria', code: 'DZ', dialCode: '+213', flag: '🇩🇿' },
    { name: 'Tunisia', code: 'TN', dialCode: '+216', flag: '🇹🇳' },
    { name: 'Egypt', code: 'EG', dialCode: '+20', flag: '🇪🇬' },
    { name: 'Saudi Arabia', code: 'SA', dialCode: '+966', flag: '🇸🇦' },
    { name: 'UAE', code: 'AE', dialCode: '+971', flag: '🇦🇪' },
    { name: 'Qatar', code: 'QA', dialCode: '+974', flag: '🇶🇦' },
    { name: 'Turkey', code: 'TR', dialCode: '+90', flag: '🇹🇷' },
    { name: 'India', code: 'IN', dialCode: '+91', flag: '🇮🇳' },
    { name: 'China', code: 'CN', dialCode: '+86', flag: '🇨🇳' },
    { name: 'Japan', code: 'JP', dialCode: '+81', flag: '🇯🇵' },
    { name: 'Australia', code: 'AU', dialCode: '+61', flag: '🇦🇺' },
    { name: 'Brazil', code: 'BR', dialCode: '+55', flag: '🇧🇷' },
    { name: 'Mexico', code: 'MX', dialCode: '+52', flag: '🇲🇽' },
    { name: 'Portugal', code: 'PT', dialCode: '+351', flag: '🇵🇹' }
  ];
  
  selectedCountry: Country = this.countries[0];
  phoneNumber: string = '';
  disabled: boolean = false;
  showDropdown: boolean = false;
  searchTerm: string = '';

  private onChange: (value: string) => void = () => {};
  private onTouched: () => void = () => {};

  ngOnInit(): void {
    // Morocco is already set as default in the initialization
  }

  get filteredCountries(): Country[] {
    if (!this.searchTerm) {
      return this.countries;
    }
    const term = this.searchTerm.toLowerCase();
    return this.countries.filter(country => 
      country.name.toLowerCase().includes(term) || 
      country.dialCode.includes(term) ||
      country.code.toLowerCase().includes(term)
    );
  }

  writeValue(value: string): void {
    if (value) {
      // Parse the full phone number to extract country code and number
      const country = this.countries.find(c => value.startsWith(c.dialCode));
      if (country) {
        this.selectedCountry = country;
        this.phoneNumber = value.substring(country.dialCode.length);
      } else {
        this.phoneNumber = value;
      }
    } else {
      this.phoneNumber = '';
    }
  }

  registerOnChange(fn: (value: string) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  selectCountry(country: Country): void {
    this.selectedCountry = country;
    this.showDropdown = false;
    this.searchTerm = '';
    this.updateValue();
  }

  onPhoneNumberChange(): void {
    // Remove non-numeric characters
    this.phoneNumber = this.phoneNumber.replaceAll(/[^0-9]/g, '');
    this.updateValue();
  }

  updateValue(): void {
    const fullNumber = this.phoneNumber 
      ? `${this.selectedCountry.dialCode}${this.phoneNumber}`
      : '';
    this.onChange(fullNumber);
  }

  toggleDropdown(): void {
    if (!this.disabled) {
      this.showDropdown = !this.showDropdown;
    }
  }

  closeDropdown(): void {
    setTimeout(() => {
      this.showDropdown = false;
      this.searchTerm = '';
    }, 200);
  }

  onBlur(): void {
    this.onTouched();
    this.closeDropdown();
  }
}
