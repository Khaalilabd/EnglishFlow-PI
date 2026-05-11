-- Insert Course Packs for Tutor khalilabdelmoumen11@gmail.com (ID: 53)
-- This script creates relevant course packs combining multiple courses

DO $$
DECLARE
    pack_beginner_id BIGINT;
    pack_intermediate_id BIGINT;
    pack_advanced_id BIGINT;
    pack_business_id BIGINT;
    pack_speaking_id BIGINT;
    pack_complete_id BIGINT;
BEGIN
    -- ========================================
    -- PACK 1: Beginner Complete Package (A1-A2)
    -- ========================================
    INSERT INTO packs (
        name, 
        description, 
        category, 
        level, 
        price, 
        max_students, 
        current_enrolled_students,
        estimated_duration,
        status,
        tutor_id,
        tutor_name,
        tutor_rating,
        created_by,
        enrollment_start_date,
        enrollment_end_date,
        created_at,
        updated_at
    ) VALUES (
        'Beginner English Complete Package',
        'Perfect for absolute beginners! This comprehensive package includes Grammar Fundamentals and Conversational English Practice. Master the basics of English grammar while building confidence in everyday conversations. Ideal for A1-A2 level learners starting their English journey.',
        'Grammar',
        'A1',
        169.99,  -- Original: 99.99 + 89.99 = 189.98, Discount: 10%
        50,
        0,
        65,  -- 40 + 25 hours
        'ACTIVE',
        53,
        'Khalil Abdelmoumen',
        4.8,
        53,
        NOW(),
        NOW() + INTERVAL '90 days',
        NOW(),
        NOW()
    ) RETURNING id INTO pack_beginner_id;
    
    -- Link courses to Beginner Pack
    INSERT INTO pack_courses (pack_id, course_id) VALUES
    (pack_beginner_id, 54),  -- Grammar Fundamentals
    (pack_beginner_id, 58);  -- Conversational English
    
    -- ========================================
    -- PACK 2: Intermediate Skills Bundle (B1-B2)
    -- ========================================
    INSERT INTO packs (
        name, 
        description, 
        category, 
        level, 
        price, 
        max_students, 
        current_enrolled_students,
        estimated_duration,
        status,
        tutor_id,
        tutor_name,
        tutor_rating,
        created_by,
        enrollment_start_date,
        enrollment_end_date,
        created_at,
        updated_at
    ) VALUES (
        'Intermediate Skills Development Bundle',
        'Take your English to the next level! This bundle combines Pronunciation Mastery and Business English Communication. Perfect your accent and pronunciation while learning professional business communication skills. Designed for B1-B2 level learners.',
        'Business English',
        'B2',
        199.99,  -- Original: 79.99 + 149.99 = 229.98, Discount: 13%
        40,
        0,
        80,  -- 30 + 50 hours
        'ACTIVE',
        53,
        'Khalil Abdelmoumen',
        4.8,
        53,
        NOW(),
        NOW() + INTERVAL '90 days',
        NOW(),
        NOW()
    ) RETURNING id INTO pack_intermediate_id;
    
    -- Link courses to Intermediate Pack
    INSERT INTO pack_courses (pack_id, course_id) VALUES
    (pack_intermediate_id, 56),  -- Pronunciation Mastery
    (pack_intermediate_id, 55);  -- Business English
    
    -- ========================================
    -- PACK 3: Advanced Fluency Package (C1)
    -- ========================================
    INSERT INTO packs (
        name, 
        description, 
        category, 
        level, 
        price, 
        max_students, 
        current_enrolled_students,
        estimated_duration,
        status,
        tutor_id,
        tutor_name,
        tutor_rating,
        created_by,
        enrollment_start_date,
        enrollment_end_date,
        created_at,
        updated_at
    ) VALUES (
        'Advanced Fluency & Vocabulary Package',
        'Achieve near-native fluency! This advanced package focuses on expanding your vocabulary to C1 level with 1000+ words, idioms, phrasal verbs, and collocations. Includes pronunciation refinement to sound more natural. Perfect for advanced learners aiming for mastery.',
        'Vocabulary',
        'C1',
        179.99,  -- Original: 119.99 + 79.99 = 199.98, Discount: 10%
        30,
        0,
        65,  -- 35 + 30 hours
        'ACTIVE',
        53,
        'Khalil Abdelmoumen',
        4.8,
        53,
        NOW(),
        NOW() + INTERVAL '90 days',
        NOW(),
        NOW()
    ) RETURNING id INTO pack_advanced_id;
    
    -- Link courses to Advanced Pack
    INSERT INTO pack_courses (pack_id, course_id) VALUES
    (pack_advanced_id, 57),  -- Vocabulary Builder
    (pack_advanced_id, 56);  -- Pronunciation Mastery
    
    -- ========================================
    -- PACK 4: Professional Business English (B2)
    -- ========================================
    INSERT INTO packs (
        name, 
        description, 
        category, 
        level, 
        price, 
        max_students, 
        current_enrolled_students,
        estimated_duration,
        status,
        tutor_id,
        tutor_name,
        tutor_rating,
        created_by,
        enrollment_start_date,
        enrollment_end_date,
        created_at,
        updated_at
    ) VALUES (
        'Professional Business English Mastery',
        'Excel in your professional career! This specialized package combines Business English Communication with Advanced Vocabulary Builder. Master business communication, presentations, negotiations, and expand your professional vocabulary. Essential for career advancement.',
        'Business English',
        'B2',
        239.99,  -- Original: 149.99 + 119.99 = 269.98, Discount: 11%
        35,
        0,
        85,  -- 50 + 35 hours
        'ACTIVE',
        53,
        'Khalil Abdelmoumen',
        4.8,
        53,
        NOW(),
        NOW() + INTERVAL '90 days',
        NOW(),
        NOW()
    ) RETURNING id INTO pack_business_id;
    
    -- Link courses to Business Pack
    INSERT INTO pack_courses (pack_id, course_id) VALUES
    (pack_business_id, 55),  -- Business English
    (pack_business_id, 57);  -- Vocabulary Builder
    
    -- ========================================
    -- PACK 5: Speaking & Pronunciation Excellence (A2-B1)
    -- ========================================
    INSERT INTO packs (
        name, 
        description, 
        category, 
        level, 
        price, 
        max_students, 
        current_enrolled_students,
        estimated_duration,
        status,
        tutor_id,
        tutor_name,
        tutor_rating,
        created_by,
        enrollment_start_date,
        enrollment_end_date,
        created_at,
        updated_at
    ) VALUES (
        'Speaking & Pronunciation Excellence',
        'Speak English confidently and clearly! This package combines Conversational English Practice with Pronunciation Mastery. Perfect your pronunciation, master phonetics, and practice real-life conversations. Ideal for learners who want to improve their speaking skills.',
        'Pronunciation',
        'B1',
        149.99,  -- Original: 89.99 + 79.99 = 169.98, Discount: 12%
        45,
        0,
        55,  -- 25 + 30 hours
        'ACTIVE',
        53,
        'Khalil Abdelmoumen',
        4.8,
        53,
        NOW(),
        NOW() + INTERVAL '90 days',
        NOW(),
        NOW()
    ) RETURNING id INTO pack_speaking_id;
    
    -- Link courses to Speaking Pack
    INSERT INTO pack_courses (pack_id, course_id) VALUES
    (pack_speaking_id, 58),  -- Conversational English
    (pack_speaking_id, 56);  -- Pronunciation Mastery
    
    -- ========================================
    -- PACK 6: Complete English Mastery (A1-C1)
    -- ========================================
    INSERT INTO packs (
        name, 
        description, 
        category, 
        level, 
        price, 
        max_students, 
        current_enrolled_students,
        estimated_duration,
        status,
        tutor_id,
        tutor_name,
        tutor_rating,
        created_by,
        enrollment_start_date,
        enrollment_end_date,
        created_at,
        updated_at
    ) VALUES (
        'Complete English Mastery - All Courses Bundle',
        'The ultimate English learning package! Get ALL 5 courses at an incredible discount. From beginner grammar to advanced vocabulary, business communication to perfect pronunciation. This comprehensive bundle takes you from A1 to C1 level. Best value for serious learners!',
        'Grammar',
        'A1',
        449.99,  -- Original: 99.99 + 149.99 + 79.99 + 119.99 + 89.99 = 539.95, Discount: 17%
        25,
        0,
        180,  -- Total of all courses
        'ACTIVE',
        53,
        'Khalil Abdelmoumen',
        4.8,
        53,
        NOW(),
        NOW() + INTERVAL '180 days',
        NOW(),
        NOW()
    ) RETURNING id INTO pack_complete_id;
    
    -- Link ALL courses to Complete Pack
    INSERT INTO pack_courses (pack_id, course_id) VALUES
    (pack_complete_id, 54),  -- Grammar Fundamentals
    (pack_complete_id, 55),  -- Business English
    (pack_complete_id, 56),  -- Pronunciation Mastery
    (pack_complete_id, 57),  -- Vocabulary Builder
    (pack_complete_id, 58);  -- Conversational English
    
    RAISE NOTICE 'All course packs created successfully!';
    RAISE NOTICE 'Pack 1 (Beginner): ID %, 2 courses', pack_beginner_id;
    RAISE NOTICE 'Pack 2 (Intermediate): ID %, 2 courses', pack_intermediate_id;
    RAISE NOTICE 'Pack 3 (Advanced): ID %, 2 courses', pack_advanced_id;
    RAISE NOTICE 'Pack 4 (Business): ID %, 2 courses', pack_business_id;
    RAISE NOTICE 'Pack 5 (Speaking): ID %, 2 courses', pack_speaking_id;
    RAISE NOTICE 'Pack 6 (Complete): ID %, 5 courses', pack_complete_id;
    RAISE NOTICE 'Total: 6 packs created with various course combinations';
END $$;
