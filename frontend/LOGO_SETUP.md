# Logo Setup Instructions

## How to Add Your Logo

1. Save your "Jungle in English" logo image file
2. Place it in: `frontend/public/images/logo/`
3. Rename it to: `jungle-in-english.png` (or update the path in the component)

### File Location:
```
frontend/
  └── public/
      └── images/
          └── logo/
              └── jungle-in-english.png  ← Place your logo here
```

### Supported Formats:
- PNG (recommended for transparency)
- JPG
- SVG
- WebP

### Recommended Size:
- Width: 800-1200px
- Height: 300-400px
- Transparent background (PNG)

## If You Use a Different Filename

Update the path in: `frontend/src/app/shared/components/logo.component.ts`

```typescript
logoSrc: string = '/images/logo/YOUR-FILENAME-HERE.png';
```

## Current Behavior:
- If the logo image loads successfully, it will be displayed
- If the image fails to load, a styled text fallback will appear with:
  - "Jungle in English" text
  - Arabic subtitle "جنقل بال ANGLAIS"
  - School colors (#2D5757, #F7EDE2)

## Testing:
After placing the logo, refresh your browser to see it appear on:
- Login page
- Register page
- Any other pages using the `<app-logo>` component
