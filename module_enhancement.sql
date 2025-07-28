-- Module Progress Enhancement SQL Script
-- This script adds module support to videos and quizzes, and creates module progress tracking

-- 1. Add module_id and order_number to videos table
ALTER TABLE videos 
ADD COLUMN module_id INT NULL,
ADD COLUMN order_number INT NULL DEFAULT 1,
ADD CONSTRAINT fk_video_module FOREIGN KEY (module_id) REFERENCES modules(module_id) ON DELETE SET NULL;

-- 2. Add module_id and order_number to quizzes table  
ALTER TABLE quizzes
ADD COLUMN module_id INT NULL,
ADD COLUMN order_number INT NULL DEFAULT 1,
ADD CONSTRAINT fk_quiz_module FOREIGN KEY (module_id) REFERENCES modules(module_id) ON DELETE SET NULL;

-- 3. Create module_progress table
CREATE TABLE module_progress (
    progress_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    module_id INT NOT NULL,
    content_completed BOOLEAN DEFAULT FALSE,
    video_completed BOOLEAN DEFAULT FALSE,
    test_completed BOOLEAN DEFAULT FALSE,
    test_unlocked BOOLEAN DEFAULT FALSE,
    module_completed BOOLEAN DEFAULT FALSE,
    completed_at DATETIME NULL,
    last_updated DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (module_id) REFERENCES modules(module_id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_module (user_id, module_id)
);

-- 4. Add indexes for better performance
CREATE INDEX idx_video_module ON videos(module_id);
CREATE INDEX idx_quiz_module ON quizzes(module_id);
CREATE INDEX idx_video_order ON videos(module_id, order_number);
CREATE INDEX idx_quiz_order ON quizzes(module_id, order_number);
CREATE INDEX idx_module_progress_user ON module_progress(user_id);
CREATE INDEX idx_module_progress_module ON module_progress(module_id);

-- 5. Sample data migration (optional - move existing videos/quizzes to first module of each course)
-- This is a safe migration that doesn't break existing data
-- You can run this to organize existing content into modules

-- Create a "General" module for courses that don't have modules yet
INSERT INTO modules (course_id, title, description, order_number, published)
SELECT DISTINCT 
    c.course_id,
    'General Content' as title,
    'Default module for existing course content' as description,
    1 as order_number,
    true as published
FROM courses c
WHERE NOT EXISTS (
    SELECT 1 FROM modules m WHERE m.course_id = c.course_id
);

-- Move existing videos to the first module of their course
UPDATE videos v
SET module_id = (
    SELECT m.module_id 
    FROM modules m 
    WHERE m.course_id = v.course_id 
    ORDER BY m.order_number 
    LIMIT 1
)
WHERE v.module_id IS NULL;

-- Move existing quizzes to the first module of their course  
UPDATE quizzes q
SET module_id = (
    SELECT m.module_id 
    FROM modules m 
    WHERE m.course_id = q.course_id 
    ORDER BY m.order_number 
    LIMIT 1
)
WHERE q.module_id IS NULL;

-- 6. Update order numbers for videos and quizzes within modules
-- Videos
SET @row_number = 0;
SET @prev_module = '';

UPDATE videos 
SET order_number = (
    SELECT @row_number := CASE 
        WHEN @prev_module = module_id THEN @row_number + 1 
        ELSE 1 
    END,
    @prev_module := module_id,
    @row_number
)[0]
WHERE module_id IS NOT NULL
ORDER BY module_id, video_id;

-- Reset variables for quizzes
SET @row_number = 0;
SET @prev_module = '';

-- Quizzes  
UPDATE quizzes 
SET order_number = (
    SELECT @row_number := CASE 
        WHEN @prev_module = module_id THEN @row_number + 1 
        ELSE 1 
    END,
    @prev_module := module_id,
    @row_number
)[0]
WHERE module_id IS NOT NULL
ORDER BY module_id, quiz_id;
