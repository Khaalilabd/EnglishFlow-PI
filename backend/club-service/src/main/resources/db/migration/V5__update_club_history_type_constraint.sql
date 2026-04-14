-- Supprimer l'ancienne contrainte
ALTER TABLE club_history DROP CONSTRAINT IF EXISTS club_history_type_check;

-- Ajouter la nouvelle contrainte avec toutes les valeurs de l'enum
ALTER TABLE club_history ADD CONSTRAINT club_history_type_check 
CHECK (type IN (
    'MEMBER_JOINED',
    'MEMBER_LEFT',
    'MEMBER_REMOVED',
    'RANK_CHANGED',
    'CLUB_CREATED',
    'CLUB_UPDATED',
    'CLUB_STATUS_CHANGED',
    'EVENT_CREATED',
    'EVENT_PARTICIPATED',
    'ACHIEVEMENT_EARNED',
    'CONTRIBUTION',
    'EXPENSE_ADDED',
    'EXPENSE_UPDATED',
    'EXPENSE_DELETED',
    'PAYMENT_CONFIRMED',
    'TASK_CREATED',
    'TASK_UPDATED',
    'TASK_DELETED',
    'OTHER'
));
