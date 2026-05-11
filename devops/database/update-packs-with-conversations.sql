-- Update packs with their conversation IDs
-- This script updates the packs table with conversation IDs from messaging database
-- Run on database: englishflow_courses

DO $$
BEGIN
    -- Update Pack 3 (Beginner) with Conversation ID 5
    UPDATE packs SET conversation_id = 5 WHERE id = 3;
    RAISE NOTICE 'Updated Pack 3 (Beginner) with Conversation ID 5';
    
    -- Update Pack 4 (Intermediate) with Conversation ID 6
    UPDATE packs SET conversation_id = 6 WHERE id = 4;
    RAISE NOTICE 'Updated Pack 4 (Intermediate) with Conversation ID 6';
    
    -- Update Pack 5 (Advanced) with Conversation ID 7
    UPDATE packs SET conversation_id = 7 WHERE id = 5;
    RAISE NOTICE 'Updated Pack 5 (Advanced) with Conversation ID 7';
    
    -- Update Pack 6 (Business) with Conversation ID 8
    UPDATE packs SET conversation_id = 8 WHERE id = 6;
    RAISE NOTICE 'Updated Pack 6 (Business) with Conversation ID 8';
    
    -- Update Pack 7 (Speaking) with Conversation ID 9
    UPDATE packs SET conversation_id = 9 WHERE id = 7;
    RAISE NOTICE 'Updated Pack 7 (Speaking) with Conversation ID 9';
    
    -- Update Pack 8 (Complete) with Conversation ID 10
    UPDATE packs SET conversation_id = 10 WHERE id = 8;
    RAISE NOTICE 'Updated Pack 8 (Complete) with Conversation ID 10';
    
    RAISE NOTICE '========================================';
    RAISE NOTICE 'All packs updated successfully!';
    RAISE NOTICE '========================================';
END $$;

-- Verify the updates
SELECT id, name, conversation_id 
FROM packs 
WHERE id IN (3,4,5,6,7,8)
ORDER BY id;
