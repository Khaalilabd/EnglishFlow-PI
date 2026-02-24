-- Update pack_enrollments table with new fields
ALTER TABLE pack_enrollments 
ADD COLUMN IF NOT EXISTS pack_category VARCHAR(50) NOT NULL DEFAULT 'GRAMMAR',
ADD COLUMN IF NOT EXISTS pack_level VARCHAR(10) NOT NULL DEFAULT 'A1',
ADD COLUMN IF NOT EXISTS tutor_name VARCHAR(255) NOT NULL DEFAULT 'Unknown',
ADD COLUMN IF NOT EXISTS total_courses INTEGER NOT NULL DEFAULT 0,
ADD COLUMN IF NOT EXISTS completed_courses INTEGER DEFAULT 0,
ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

-- Create index for faster queries
CREATE INDEX IF NOT EXISTS idx_pack_enrollments_status ON pack_enrollments(status);
CREATE INDEX IF NOT EXISTS idx_pack_enrollments_student_status ON pack_enrollments(student_id, status);
