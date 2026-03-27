-- Initialiser l'utilisateur ID 1 pour les tests
-- Ce script crée un niveau de base pour l'utilisateur

INSERT INTO user_levels (
    user_id,
    assessment_level,
    certified_level,
    has_completed_assessment,
    assessment_completed_at,
    currentxp,
    totalxp,
    jungle_coins,
    loyalty_tier,
    total_spent,
    consecutive_days,
    last_activity_date,
    created_at,
    updated_at
) VALUES (
    1,                          -- user_id
    'A1',                       -- assessment_level (niveau initial)
    NULL,                       -- certified_level (pas encore certifié)
    true,                       -- has_completed_assessment
    NOW(),                      -- assessment_completed_at
    0,                          -- currentxp
    0,                          -- totalxp
    0,                          -- jungle_coins
    'BRONZE',                   -- loyalty_tier
    0.0,                        -- total_spent
    0,                          -- consecutive_days
    NOW(),                      -- last_activity_date
    NOW(),                      -- created_at
    NOW()                       -- updated_at
)
ON CONFLICT (user_id) DO NOTHING;

-- Message de confirmation
SELECT 'User level initialized for user_id = 1' AS status;
