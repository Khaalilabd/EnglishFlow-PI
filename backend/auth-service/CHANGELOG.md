# Changelog

All notable changes to the Auth Service will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [2.0.0] - 2026-02-19

### 🎉 Major Release - Security & Invitation System

### Added

#### Invitation System
- **New Entity:** `Invitation` - Secure invitation system for TUTOR and ACADEMIC_OFFICE_AFFAIR roles
- **New Repository:** `InvitationRepository` - Database operations for invitations
- **New Service:** `InvitationService` - Complete invitation lifecycle management
  - `sendInvitation()` - Send invitation email with unique token
  - `acceptInvitation()` - Create user account from invitation
  - `getPendingInvitations()` - List all pending invitations
  - `resendInvitation()` - Resend invitation with extended expiry
  - `cancelInvitation()` - Cancel pending invitation
  - `cleanupExpiredInvitations()` - Remove expired invitations
- **New Controller:** `InvitationController` - REST API endpoints for invitations
- **New DTOs:**
  - `InvitationRequest` - Send invitation (email + role)
  - `InvitationResponse` - Invitation details
  - `AcceptInvitationRequest` - Accept invitation with profile data
- **New Email Template:** `invitation-email.html` - Professional invitation email
- **New Migration:** `V2__add_invitations_table.sql` - Database schema for invitations

#### Security Enhancements
- **Rate Limiting:** Protection against brute force attacks on login
  - Maximum 5 failed attempts per 15 minutes
  - Automatic reset on successful login
  - Configurable limits
- **New Service:** `RateLimitService` - Rate limiting implementation using Guava Cache
- **Environment Variables:** JWT secret and sensitive data moved to environment variables
- **Dependency:** Added Guava 32.1.3 for rate limiting cache

#### Documentation
- **OPTIMIZATIONS.md** - Detailed documentation of Phase 1 optimizations
- **INVITATION_SYSTEM_GUIDE.md** - Complete guide for invitation system
- **README_UPDATED.md** - Updated README with new features
- **postman_collection.json** - Postman collection for API testing
- **CHANGELOG.md** - This file

### Changed

#### Security
- **JWT Secret:** Moved from hardcoded value to `${JWT_SECRET}` environment variable
- **Database Credentials:** Moved to environment variables (`${DB_URL}`, `${DB_USERNAME}`, `${DB_PASSWORD}`)
- **Email Service:** Added `sendInvitationEmail()` method
- **Auth Service:** Integrated rate limiting in `login()` method
- **.env.example:** Updated with new environment variables and security recommendations

#### Configuration
- **application.yml:**
  - JWT secret now uses environment variable with fallback
  - Database credentials use environment variables
  - Added comments for security best practices

### Deprecated

- **Manual User Creation:** The old manual creation workflow for TUTOR and ACADEMIC_OFFICE_AFFAIR is deprecated
  - Still functional for backward compatibility
  - Will be removed in v3.0.0
  - Replaced by invitation system

### Security

#### Fixed Vulnerabilities
- ❌ **CRITICAL:** Plain text passwords in emails - FIXED
  - Passwords no longer sent via email
  - Users choose their own passwords during invitation acceptance
- ❌ **HIGH:** JWT secret hardcoded in source code - FIXED
  - Moved to environment variable
  - Recommendation to use 256+ bit secrets
- ❌ **HIGH:** No rate limiting on login endpoint - FIXED
  - Implemented 5 attempts / 15 minutes limit
  - Protection against brute force attacks
- ❌ **MEDIUM:** Database credentials in source code - FIXED
  - Moved to environment variables

#### Security Improvements
- ✅ Invitation tokens are UUID v4 (128 bits entropy)
- ✅ Invitations expire after 7 days
- ✅ One-time use tokens (marked as used after acceptance)
- ✅ Email validation before sending invitations
- ✅ Role validation (only TUTOR and ACADEMIC_OFFICE_AFFAIR)
- ✅ Automatic cleanup of expired invitations

### Performance

- **Rate Limiting Cache:** In-memory cache with automatic expiration (15 minutes)
- **Database Indexes:** Added indexes on invitations table for better query performance
  - `idx_invitations_email`
  - `idx_invitations_token`
  - `idx_invitations_used`
  - `idx_invitations_expiry_date`

### Migration Guide

#### For Developers

1. **Update Environment Variables:**
```bash
cp .env.example .env
# Generate strong JWT secret
openssl rand -base64 64
# Update .env with your values
```

2. **Database Migration:**
```bash
# Automatic on startup via Flyway
# Creates 'invitations' table
```

3. **Test Invitation System:**
```bash
# Import postman_collection.json
# Follow INVITATION_SYSTEM_GUIDE.md
```

#### For Frontend Developers

1. **New Endpoints Available:**
   - `POST /invitations/send` - Send invitation
   - `POST /invitations/accept` - Accept invitation
   - `GET /invitations/pending` - List pending invitations

2. **Components to Create:**
   - Invitation acceptance page (`/accept-invitation`)
   - Admin invitation management interface
   - Replace manual creation forms with invitation buttons

3. **Components to Update:**
   - `create-tutor.component.ts` - Use invitation system
   - `academic-affairs.component.ts` - Use invitation system

4. **Components to Remove (v3.0.0):**
   - `password-modal.component.ts` - No longer needed

### Known Issues

- [ ] No rate limiting on `/invitations/send` endpoint (TODO: Phase 2)
- [ ] No automatic cleanup cron job for expired invitations (manual cleanup available)
- [ ] No notification to admin when invitation is accepted (TODO: Phase 2)
- [ ] No multi-language support for invitation emails (TODO: Phase 3)

### Breaking Changes

None. This release is backward compatible.

---

## [1.0.0] - 2025-12-01

### Initial Release

#### Features
- User registration (STUDENT role)
- Email activation
- Login with JWT
- Password reset
- OAuth2 (Google, GitHub)
- Role-based access control (ADMIN, TUTOR, STUDENT, ACADEMIC_OFFICE_AFFAIR)
- Profile management
- Admin user management
- Email notifications

#### Security
- BCrypt password hashing
- JWT authentication
- reCAPTCHA integration
- Email verification

#### Infrastructure
- Spring Boot 3.2.0
- PostgreSQL database
- Eureka service discovery
- Spring Cloud Config (optional)
- Actuator health checks

---

## Upcoming Releases

### [2.1.0] - Planned Q1 2026

#### Features
- Frontend invitation acceptance page
- Admin invitation management interface
- Audit trail logging
- Refresh token mechanism

### [3.0.0] - Planned Q2 2026

#### Breaking Changes
- Remove deprecated manual user creation endpoints
- Remove password-modal component
- Require environment variables (no fallbacks)

#### Features
- Two-factor authentication (TOTP)
- Session management
- Fine-grained permissions
- Multi-language email templates

---

## Version History

| Version | Date | Status | Notes |
|---------|------|--------|-------|
| 2.0.0 | 2026-02-19 | ✅ Current | Invitation system + Security |
| 1.0.0 | 2025-12-01 | 📦 Archived | Initial release |

---

## Support

For questions or issues related to specific versions:
- **v2.x:** See [INVITATION_SYSTEM_GUIDE.md](./INVITATION_SYSTEM_GUIDE.md)
- **v1.x:** See legacy documentation

---

**Maintained by:** EnglishFlow Team  
**Last Updated:** 2026-02-19
