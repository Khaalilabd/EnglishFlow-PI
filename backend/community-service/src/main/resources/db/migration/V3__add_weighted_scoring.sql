-- Add weighted scoring columns to topics table
ALTER TABLE topics ADD COLUMN IF NOT EXISTS like_count INTEGER DEFAULT 0;
ALTER TABLE topics ADD COLUMN IF NOT EXISTS insightful_count INTEGER DEFAULT 0;
ALTER TABLE topics ADD COLUMN IF NOT EXISTS helpful_count INTEGER DEFAULT 0;
ALTER TABLE topics ADD COLUMN IF NOT EXISTS weighted_score INTEGER DEFAULT 0;
ALTER TABLE topics ADD COLUMN IF NOT EXISTS is_trending BOOLEAN DEFAULT FALSE;

-- Add weighted scoring columns to posts table
ALTER TABLE posts ADD COLUMN IF NOT EXISTS like_count INTEGER DEFAULT 0;
ALTER TABLE posts ADD COLUMN IF NOT EXISTS insightful_count INTEGER DEFAULT 0;
ALTER TABLE posts ADD COLUMN IF NOT EXISTS helpful_count INTEGER DEFAULT 0;
ALTER TABLE posts ADD COLUMN IF NOT EXISTS weighted_score INTEGER DEFAULT 0;
ALTER TABLE posts ADD COLUMN IF NOT EXISTS is_trending BOOLEAN DEFAULT FALSE;

-- Create index for better performance on sorting
CREATE INDEX IF NOT EXISTS idx_topics_weighted_score ON topics(weighted_score DESC);
CREATE INDEX IF NOT EXISTS idx_posts_weighted_score ON posts(weighted_score DESC);
CREATE INDEX IF NOT EXISTS idx_topics_trending ON topics(is_trending, weighted_score DESC);
CREATE INDEX IF NOT EXISTS idx_posts_trending ON posts(is_trending, weighted_score DESC);

-- Update existing records to calculate initial scores for topics
UPDATE topics SET 
    like_count = (SELECT COUNT(*) FROM reactions WHERE topic_id = topics.id AND type = 'LIKE'),
    insightful_count = (SELECT COUNT(*) FROM reactions WHERE topic_id = topics.id AND type = 'INSIGHTFUL'),
    helpful_count = (SELECT COUNT(*) FROM reactions WHERE topic_id = topics.id AND type = 'HELPFUL');

-- Update existing records to calculate initial scores for posts
UPDATE posts SET 
    like_count = (SELECT COUNT(*) FROM reactions WHERE post_id = posts.id AND type = 'LIKE'),
    insightful_count = (SELECT COUNT(*) FROM reactions WHERE post_id = posts.id AND type = 'INSIGHTFUL'),
    helpful_count = (SELECT COUNT(*) FROM reactions WHERE post_id = posts.id AND type = 'HELPFUL');

-- Calculate weighted scores (likes*1 + insightful*2 + helpful*3)
UPDATE topics SET weighted_score = (like_count * 1) + (insightful_count * 2) + (helpful_count * 3);
UPDATE posts SET weighted_score = (like_count * 1) + (insightful_count * 2) + (helpful_count * 3);

