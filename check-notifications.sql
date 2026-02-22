-- Check if notifications were created for NOTED status
SELECT 
    cn.id,
    cn.complaint_id,
    cn.recipient_id,
    cn.notification_type,
    cn.message,
    cn.is_read,
    cn.created_at,
    c.status as complaint_status,
    c.subject as complaint_subject
FROM complaint_notifications cn
JOIN complaints c ON cn.complaint_id = c.id
WHERE c.status = 'NOTED'
ORDER BY cn.created_at DESC
LIMIT 10;

-- Check recent workflow changes to NOTED
SELECT 
    cw.id,
    cw.complaint_id,
    cw.from_status,
    cw.to_status,
    cw.actor_role,
    cw.comment,
    cw.timestamp,
    c.user_id as student_id
FROM complaint_workflows cw
JOIN complaints c ON cw.complaint_id = c.id
WHERE cw.to_status = 'NOTED'
ORDER BY cw.timestamp DESC
LIMIT 10;
