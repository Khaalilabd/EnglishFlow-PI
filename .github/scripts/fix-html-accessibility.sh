#!/bin/bash

# Script to fix HTML accessibility issues in email templates
# Adds lang="en" to <html> tags and role="presentation" to layout tables

set -e

echo "🔧 Fixing HTML accessibility issues in email templates..."

# Find all HTML files in templates directories
HTML_FILES=$(find backend -name "*.html" -path "*/src/main/resources/templates/*")

FIXED_COUNT=0

for file in $HTML_FILES; do
    echo "Processing: $file"
    
    # Add lang="en" to <html> tag if not present
    if grep -q '<html xmlns:th="http://www.thymeleaf.org">' "$file"; then
        sed -i.bak 's|<html xmlns:th="http://www.thymeleaf.org">|<html lang="en" xmlns:th="http://www.thymeleaf.org">|g' "$file"
        echo "  ✓ Added lang='en' attribute"
        FIXED_COUNT=$((FIXED_COUNT + 1))
    fi
    
    # Add role="presentation" to layout tables (tables without th headers)
    # This tells screen readers these are layout tables, not data tables
    sed -i.bak 's|<table width=|<table role="presentation" width=|g' "$file"
    sed -i.bak 's|<table cellpadding=|<table role="presentation" cellpadding=|g' "$file"
    
    # Remove backup files
    rm -f "${file}.bak"
done

echo ""
echo "✅ Fixed $FIXED_COUNT HTML files"
echo "   - Added lang='en' to <html> tags"
echo "   - Added role='presentation' to layout tables"
