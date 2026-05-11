-- Create conversation groups for the 6 packs created
-- This script creates GROUP conversations in the messaging database for each pack
-- Run on database: englishflow_messaging_db

DO $$
DECLARE
    conv_beginner_id BIGINT;
    conv_intermediate_id BIGINT;
    conv_advanced_id BIGINT;
    conv_business_id BIGINT;
    conv_speaking_id BIGINT;
    conv_complete_id BIGINT;
BEGIN
    -- ========================================
    -- Create Conversation for Pack 3: Beginner English Complete Package
    -- ========================================
    INSERT INTO conversations (
        title,
        description,
        type,
        created_by,
        created_at,
        updated_at,
        last_message_at
    ) VALUES (
        'Pack: Beginner English Complete Package - Teacher Khalil',
        'Discussion group for Beginner English Complete Package. Ask questions, share progress, and connect with fellow learners!',
        'GROUP',
        53,  -- Tutor ID
        NOW(),
        NOW(),
        NOW()
    ) RETURNING id INTO conv_beginner_id;
    
    -- Add tutor as participant
    INSERT INTO conversation_participants (
        conversation_id, 
        user_id, 
        user_email, 
        user_name, 
        user_role, 
        user_avatar, 
        participant_role, 
        is_active, 
        joined_at
    ) VALUES (
        conv_beginner_id, 
        53, 
        'khalilabdelmoumen11@gmail.com', 
        'Mr Amazing', 
        'TUTOR', 
        NULL, 
        'ADMIN', 
        true, 
        NOW()
    );
    
    -- ========================================
    -- Create Conversation for Pack 4: Intermediate Skills Development Bundle
    -- ========================================
    INSERT INTO conversations (
        title,
        description,
        type,
        created_by,
        created_at,
        updated_at,
        last_message_at
    ) VALUES (
        'Pack: Intermediate Skills Development Bundle - Teacher Khalil',
        'Discussion group for Intermediate Skills Development Bundle. Practice your skills and get feedback from your tutor!',
        'GROUP',
        53,
        NOW(),
        NOW(),
        NOW()
    ) RETURNING id INTO conv_intermediate_id;
    
    INSERT INTO conversation_participants (
        conversation_id, 
        user_id, 
        user_email, 
        user_name, 
        user_role, 
        user_avatar, 
        participant_role, 
        is_active, 
        joined_at
    ) VALUES (
        conv_intermediate_id, 
        53, 
        'khalilabdelmoumen11@gmail.com', 
        'Mr Amazing', 
        'TUTOR', 
        NULL, 
        'ADMIN', 
        true, 
        NOW()
    );
    
    -- ========================================
    -- Create Conversation for Pack 5: Advanced Fluency & Vocabulary Package
    -- ========================================
    INSERT INTO conversations (
        title,
        description,
        type,
        created_by,
        created_at,
        updated_at,
        last_message_at
    ) VALUES (
        'Pack: Advanced Fluency & Vocabulary Package - Teacher Khalil',
        'Discussion group for Advanced Fluency & Vocabulary Package. Share advanced vocabulary and practice together!',
        'GROUP',
        53,
        NOW(),
        NOW(),
        NOW()
    ) RETURNING id INTO conv_advanced_id;
    
    INSERT INTO conversation_participants (
        conversation_id, 
        user_id, 
        user_email, 
        user_name, 
        user_role, 
        user_avatar, 
        participant_role, 
        is_active, 
        joined_at
    ) VALUES (
        conv_advanced_id, 
        53, 
        'khalilabdelmoumen11@gmail.com', 
        'Mr Amazing', 
        'TUTOR', 
        NULL, 
        'ADMIN', 
        true, 
        NOW()
    );
    
    -- ========================================
    -- Create Conversation for Pack 6: Professional Business English Mastery
    -- ========================================
    INSERT INTO conversations (
        title,
        description,
        type,
        created_by,
        created_at,
        updated_at,
        last_message_at
    ) VALUES (
        'Pack: Professional Business English Mastery - Teacher Khalil',
        'Discussion group for Professional Business English Mastery. Network with professionals and improve your business English!',
        'GROUP',
        53,
        NOW(),
        NOW(),
        NOW()
    ) RETURNING id INTO conv_business_id;
    
    INSERT INTO conversation_participants (
        conversation_id, 
        user_id, 
        user_email, 
        user_name, 
        user_role, 
        user_avatar, 
        participant_role, 
        is_active, 
        joined_at
    ) VALUES (
        conv_business_id, 
        53, 
        'khalilabdelmoumen11@gmail.com', 
        'Mr Amazing', 
        'TUTOR', 
        NULL, 
        'ADMIN', 
        true, 
        NOW()
    );
    
    -- ========================================
    -- Create Conversation for Pack 7: Speaking & Pronunciation Excellence
    -- ========================================
    INSERT INTO conversations (
        title,
        description,
        type,
        created_by,
        created_at,
        updated_at,
        last_message_at
    ) VALUES (
        'Pack: Speaking & Pronunciation Excellence - Teacher Khalil',
        'Discussion group for Speaking & Pronunciation Excellence. Practice speaking and get pronunciation tips!',
        'GROUP',
        53,
        NOW(),
        NOW(),
        NOW()
    ) RETURNING id INTO conv_speaking_id;
    
    INSERT INTO conversation_participants (
        conversation_id, 
        user_id, 
        user_email, 
        user_name, 
        user_role, 
        user_avatar, 
        participant_role, 
        is_active, 
        joined_at
    ) VALUES (
        conv_speaking_id, 
        53, 
        'khalilabdelmoumen11@gmail.com', 
        'Mr Amazing', 
        'TUTOR', 
        NULL, 
        'ADMIN', 
        true, 
        NOW()
    );
    
    -- ========================================
    -- Create Conversation for Pack 8: Complete English Mastery
    -- ========================================
    INSERT INTO conversations (
        title,
        description,
        type,
        created_by,
        created_at,
        updated_at,
        last_message_at
    ) VALUES (
        'Pack: Complete English Mastery - All Courses Bundle - Teacher Khalil',
        'Discussion group for Complete English Mastery Bundle. Join our comprehensive learning community!',
        'GROUP',
        53,
        NOW(),
        NOW(),
        NOW()
    ) RETURNING id INTO conv_complete_id;
    
    INSERT INTO conversation_participants (
        conversation_id, 
        user_id, 
        user_email, 
        user_name, 
        user_role, 
        user_avatar, 
        participant_role, 
        is_active, 
        joined_at
    ) VALUES (
        conv_complete_id, 
        53, 
        'khalilabdelmoumen11@gmail.com', 
        'Mr Amazing', 
        'TUTOR', 
        NULL, 
        'ADMIN', 
        true, 
        NOW()
    );
    
    RAISE NOTICE '========================================';
    RAISE NOTICE 'All pack conversations created successfully!';
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Pack 3 (Beginner) - Conversation ID: %', conv_beginner_id;
    RAISE NOTICE 'Pack 4 (Intermediate) - Conversation ID: %', conv_intermediate_id;
    RAISE NOTICE 'Pack 5 (Advanced) - Conversation ID: %', conv_advanced_id;
    RAISE NOTICE 'Pack 6 (Business) - Conversation ID: %', conv_business_id;
    RAISE NOTICE 'Pack 7 (Speaking) - Conversation ID: %', conv_speaking_id;
    RAISE NOTICE 'Pack 8 (Complete) - Conversation ID: %', conv_complete_id;
    RAISE NOTICE '========================================';
    RAISE NOTICE 'NEXT STEP: Update packs table with these conversation IDs';
    RAISE NOTICE 'Run: psql -U postgres -d englishflow_courses -f update-packs-with-conversations.sql';
    RAISE NOTICE '========================================';
END $$;
