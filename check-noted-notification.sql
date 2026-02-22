-- Check the last NOTED notification created
SELECT 
    cn.id,
    cn.complaint_id,
    cn.recipient_id,
    cn.recipient_role,
    cn.notification_type,
    cn.message,
    cn.is_read,
    cn.created_at,
    c.user_id as complaint_owner_id,
    c.subject as complaint_subject,
    c.status as complaint_status
FROM complaint_notifications cn
JOIN complaints c ON cn.complaint_id = c.id
WHERE cn.notification_type = 'NOTED'
ORDER BY cn.created_at DESC
LIMIT 5;
