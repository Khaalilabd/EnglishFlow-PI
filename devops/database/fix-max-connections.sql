-- =====================================================
-- Script pour augmenter la limite de connexions PostgreSQL
-- =====================================================
-- Ce script doit être exécuté en tant que superutilisateur

-- Afficher la configuration actuelle
SHOW max_connections;

-- Augmenter temporairement la limite (nécessite un redémarrage)
-- ALTER SYSTEM SET max_connections = 200;

-- Voir toutes les connexions actives
SELECT 
    pid,
    usename,
    application_name,
    client_addr,
    state,
    query_start,
    state_change
FROM pg_stat_activity
WHERE state != 'idle'
ORDER BY query_start;

-- Compter les connexions par base de données
SELECT 
    datname,
    COUNT(*) as connections
FROM pg_stat_activity
GROUP BY datname
ORDER BY connections DESC;

-- Terminer les connexions inactives (ATTENTION: à utiliser avec précaution)
-- SELECT pg_terminate_backend(pid)
-- FROM pg_stat_activity
-- WHERE state = 'idle'
-- AND state_change < NOW() - INTERVAL '10 minutes'
-- AND pid <> pg_backend_pid();
