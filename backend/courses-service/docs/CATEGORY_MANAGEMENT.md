# Category Management System

## Overview
The Category Management System provides a flexible and dynamic way to organize courses and packs by categories. Unlike traditional enum-based systems, this implementation uses a database-driven approach that allows administrators to create, modify, and manage categories without code changes.

## Features

### 1. Dynamic Category Creation
- Create new categories on-the-fly through the UI
- No code deployment required to add new categories
- Immediate availability across the system

### 2. Rich Category Metadata
Each category includes:
- **Name**: Unique identifier (e.g., "Business English", "Grammar")
- **Description**: Detailed explanation of the category
- **Icon**: Visual emoji or icon representation (📚, 💼, 🗣️, etc.)
- **Color**: Hex color code for UI theming (#3B82F6, #10B981, etc.)
- **Active Status**: Enable/disable categories without deletion
- **Display Order**: Control the order categories appear in lists

### 3. Category Management Interface
Located at `/dashboard/categories` for ACADEMIC_OFFICE_AFFAIR role:
- View all categories in a sortable list
- Create new categories with custom icons and colors
- Edit existing categories
- Toggle active/inactive status
- Reorder categories using up/down arrows
- Delete unused categories

### 4. Default Categories
The system comes pre-configured with 10 default categories:
1. Grammar (📚 Blue)
2. Vocabulary (📖 Green)
3. Pronunciation (🗣️ Orange)
4. Business English (💼 Red)
5. Conversation (💬 Purple)
6. Writing (✍️ Pink)
7. Reading (📕 Teal)
8. Listening (👂 Orange)
9. Exam Preparation (🎯 Indigo)
10. Culture & Idioms (🌟 Lime)

## API Endpoints

### Category Controller (`/api/categories`)

#### Create Category
```http
POST /api/categories
Content-Type: application/json

{
  "name": "Medical English",
  "description": "English for healthcare professionals",
  "icon": "🏥",
  "color": "#06B6D4",
  "active": true,
  "displayOrder": 11,
  "createdBy": 1
}
```

#### Update Category
```http
PUT /api/categories/{id}
Content-Type: application/json

{
  "name": "Medical English",
  "description": "Updated description",
  "icon": "🏥",
  "color": "#06B6D4",
  "active": true,
  "displayOrder": 11
}
```

#### Get All Categories
```http
GET /api/categories
```

#### Get Active Categories Only
```http
GET /api/categories/active
```

#### Get Category by ID
```http
GET /api/categories/{id}
```

#### Toggle Active Status
```http
PUT /api/categories/{id}/toggle-active
```

#### Update Display Order
```http
PUT /api/categories/{id}/order?order=5
```

#### Delete Category
```http
DELETE /api/categories/{id}
```

## Database Schema

```sql
CREATE TABLE course_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    icon VARCHAR(50),
    color VARCHAR(20),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL
);
```

## Integration with Courses and Packs

### Course Creation
When creating a course, tutors select from active categories. The category determines:
- Course classification
- Search and filtering options
- Pack assignment eligibility

### Pack Creation
When creating a pack, academic staff:
1. Select a category (e.g., "Business English")
2. Select a level (A1-C2)
3. Choose a tutor who teaches that category
4. Select courses from that tutor matching the category

### Student Experience
Students can:
- Browse courses by category
- Filter packs by category and level
- See category-specific recommendations
- View category icons and colors for visual identification

## Best Practices

### Naming Conventions
- Use clear, descriptive names
- Avoid abbreviations unless widely recognized
- Keep names concise (2-3 words maximum)

### Icon Selection
- Choose relevant emojis that represent the category
- Ensure icons are visually distinct
- Test icon rendering across devices

### Color Choices
- Use contrasting colors for different categories
- Maintain accessibility standards (WCAG AA)
- Consider color-blind friendly palettes

### Display Order
- Group related categories together
- Place most popular categories first
- Update order based on usage analytics

## Migration from Enum-based System

If migrating from the old enum-based Category system:

1. **Data Migration**: Run the V3 migration script to create the table and populate default categories
2. **Code Updates**: Update references from `Category.GRAMMAR` to database lookups
3. **Testing**: Verify all category-dependent features work correctly
4. **Cleanup**: Remove old Category enum after successful migration

## Future Enhancements

Potential improvements:
- Category analytics (most popular, enrollment rates)
- Subcategories for more granular organization
- Multi-language category names
- Category-specific settings and configurations
- Automated category suggestions based on course content
