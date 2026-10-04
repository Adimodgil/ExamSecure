-- ExamSecure database schema
CREATE DATABASE IF NOT EXISTS examsecure;
USE examsecure;

CREATE TABLE IF NOT EXISTS users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(64)  NOT NULL,
    salt          VARCHAR(16)  NOT NULL,
    role          VARCHAR(10)  NOT NULL          -- 'STUDENT' or 'ADMIN' (teacher)
);

CREATE TABLE IF NOT EXISTS exams (
    exam_id          INT AUTO_INCREMENT PRIMARY KEY,
    exam_name        VARCHAR(100) NOT NULL,
    description      VARCHAR(255),
    duration_minutes INT NOT NULL,
    total_marks      INT NOT NULL DEFAULT 0,
    created_by       INT NOT NULL,
    start_time       DATETIME NULL,
    end_time         DATETIME NULL,
    pdf_file         VARCHAR(255) NULL,
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS questions (
    question_id    INT AUTO_INCREMENT PRIMARY KEY,
    exam_id        INT NOT NULL,
    question_text  TEXT NOT NULL,
    option_a       VARCHAR(500),
    option_b       VARCHAR(500),
    option_c       VARCHAR(500),
    option_d       VARCHAR(500),
    correct_answer CHAR(1),
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS attempts (
    attempt_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_id    INT NOT NULL,
    user_id    INT NOT NULL,
    start_time DATETIME NULL,
    end_time   DATETIME NULL,
    status     VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    score      INT NOT NULL DEFAULT 0,
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
