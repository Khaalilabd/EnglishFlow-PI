import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class CustomValidators {
  /**
   * Validator for CIN - accepts letters followed by numbers (AB123456) or just numbers (12345678)
   */
  static cinValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null; // Don't validate empty values (use Validators.required for that)
      }
      
      // Accept: AB123456 (letters + numbers) or 12345678 (just numbers)
      const cinPattern = /^[A-Z]{0,2}[0-9]{5,8}$/;
      const valid = cinPattern.test(control.value);
      
      return valid ? null : { invalidCin: { value: control.value } };
    };
  }

  /**
   * Validator for phone number - accepts various formats
   */
  static phoneValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null; // Optional field
      }
      
      // Clean the phone number (remove spaces, dashes, etc.)
      const cleanPhone = control.value.replace(/[\s\-\(\)]/g, '');
      
      // Accept various formats:
      // +212XXXXXXXXX (international)
      // 0XXXXXXXXX (national with 0)
      // XXXXXXXXX (without 0)
      const phonePatterns = [
        /^\+212[0-9]{9}$/, // +212XXXXXXXXX
        /^0[0-9]{9}$/, // 0XXXXXXXXX
        /^[0-9]{9}$/, // XXXXXXXXX
        /^\+[1-9][0-9]{1,3}[0-9]{6,12}$/ // International format
      ];
      
      const valid = phonePatterns.some(pattern => pattern.test(cleanPhone));
      
      return valid ? null : { invalidPhone: { value: control.value } };
    };
  }

  /**
   * Validator to check if passwords match
   */
  static passwordMatchValidator(passwordField: string, confirmPasswordField: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const password = control.get(passwordField);
      const confirmPassword = control.get(confirmPasswordField);
      
      if (!password || !confirmPassword) {
        return null;
      }
      
      if (confirmPassword.value === '') {
        return null;
      }
      
      if (password.value !== confirmPassword.value) {
        confirmPassword.setErrors({ passwordMismatch: true });
        return { passwordMismatch: true };
      } else {
        // Clear the error if passwords match
        const errors = confirmPassword.errors;
        if (errors) {
          delete errors['passwordMismatch'];
          confirmPassword.setErrors(Object.keys(errors).length > 0 ? errors : null);
        }
        return null;
      }
    };
  }

  /**
   * Validator for strong password
   */
  static strongPasswordValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null;
      }
      
      const password = control.value;
      const errors: any = {};
      
      // At least 8 characters
      if (password.length < 8) {
        errors.minLength = true;
      }
      
      // At least one uppercase letter
      if (!/[A-Z]/.test(password)) {
        errors.uppercase = true;
      }
      
      // At least one lowercase letter
      if (!/[a-z]/.test(password)) {
        errors.lowercase = true;
      }
      
      // At least one number
      if (!/[0-9]/.test(password)) {
        errors.number = true;
      }
      
      return Object.keys(errors).length > 0 ? { weakPassword: errors } : null;
    };
  }

  /**
   * Validator for age (must be at least minAge years old)
   */
  static minAgeValidator(minAge: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null;
      }
      
      const birthDate = new Date(control.value);
      const today = new Date();
      const age = today.getFullYear() - birthDate.getFullYear();
      const monthDiff = today.getMonth() - birthDate.getMonth();
      
      if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
        return age - 1 >= minAge ? null : { minAge: { requiredAge: minAge, actualAge: age - 1 } };
      }
      
      return age >= minAge ? null : { minAge: { requiredAge: minAge, actualAge: age } };
    };
  }

  /**
   * Validator for postal code
   */
  static postalCodeValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null;
      }
      
      const postalCodePattern = /^[0-9]{4,10}$/;
      const valid = postalCodePattern.test(control.value);
      
      return valid ? null : { invalidPostalCode: { value: control.value } };
    };
  }
}
