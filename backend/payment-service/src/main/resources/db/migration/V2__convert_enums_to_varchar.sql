-- Step 1: Remove default values that depend on the enum types
ALTER TABLE payments ALTER COLUMN status DROP DEFAULT;
ALTER TABLE payments ALTER COLUMN item_type DROP DEFAULT;

-- Step 2: Convert enum columns to VARCHAR
ALTER TABLE payments
    ALTER COLUMN item_type TYPE VARCHAR(20) USING item_type::VARCHAR,
    ALTER COLUMN status    TYPE VARCHAR(20) USING status::VARCHAR;

-- Step 3: Restore the default for status as a plain string
ALTER TABLE payments ALTER COLUMN status SET DEFAULT 'PENDING';

-- Step 4: Drop the now-unused enum types (CASCADE handles any remaining dependencies)
DROP TYPE IF EXISTS payment_item_type CASCADE;
DROP TYPE IF EXISTS payment_status CASCADE;
