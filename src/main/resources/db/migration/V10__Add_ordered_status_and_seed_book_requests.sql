-- Extend book request lifecycle with ORDERED and seed librarian-facing examples.
CREATE TABLE IF NOT EXISTS book_requests (
       id BIGINT NOT NULL AUTO_INCREMENT,
       author VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
       book_title VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
       isbn VARCHAR(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
       justification TEXT COLLATE utf8mb4_unicode_ci,
       processed_at DATETIME(6) DEFAULT NULL,
       requested_at DATETIME(6) NOT NULL,
       status ENUM('PENDING', 'APPROVED', 'REJECTED', 'ORDERED', 'FULFILLED') COLLATE utf8mb4_unicode_ci NOT NULL,
       user_id BIGINT NOT NULL,
       PRIMARY KEY (id),
       KEY idx_request_user_id (user_id),
       KEY idx_request_status (status),
       CONSTRAINT fk_book_requests_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE book_requests
    MODIFY COLUMN status ENUM('PENDING', 'APPROVED', 'REJECTED', 'ORDERED', 'FULFILLED')
    COLLATE utf8mb4_unicode_ci NOT NULL;

INSERT INTO book_requests (user_id, book_title, author, isbn, justification, status, requested_at, processed_at)
SELECT
    u.id,
    seed.book_title,
    seed.author,
    seed.isbn,
    seed.justification,
    seed.status,
    seed.requested_at,
    seed.processed_at
FROM (
    SELECT 'member1' AS username, 'Domain-Driven Design' AS book_title, 'Eric Evans' AS author, '978-0321125217' AS isbn,
           'Needed for architecture guild study sessions.' AS justification, 'PENDING' AS status,
           TIMESTAMP('2026-07-24 10:00:00') AS requested_at, NULL AS processed_at
    UNION ALL
    SELECT 'member1', 'Refactoring' , 'Martin Fowler', '978-0201485677',
           'Supports quality improvements in ongoing projects.', 'ORDERED',
           TIMESTAMP('2026-07-20 11:30:00'), TIMESTAMP('2026-07-22 15:10:00')
    UNION ALL
    SELECT 'member1', 'Patterns of Enterprise Application Architecture', 'Martin Fowler', '978-0321127426',
           'Requested for backend design reference.', 'REJECTED',
           TIMESTAMP('2026-07-18 09:45:00'), TIMESTAMP('2026-07-19 13:00:00')
    UNION ALL
    SELECT 'member2', 'Working Effectively with Legacy Code', 'Michael C. Feathers', '978-0131177055',
           'Needed for modernization playbook workshops.', 'APPROVED',
           TIMESTAMP('2026-07-23 14:15:00'), TIMESTAMP('2026-07-24 10:20:00')
    UNION ALL
    SELECT 'member2', 'Site Reliability Engineering', 'Betsy Beyer', '978-1491929124',
           'Helps define incident readiness standards.', 'ORDERED',
           TIMESTAMP('2026-07-19 16:00:00'), TIMESTAMP('2026-07-21 09:00:00')
    UNION ALL
    SELECT 'member2', 'Clean Architecture', 'Robert C. Martin', '978-0134494166',
           'Requested for architecture chapter review group.', 'FULFILLED',
           TIMESTAMP('2026-07-10 08:40:00'), TIMESTAMP('2026-07-15 17:30:00')
) seed
JOIN users u ON u.username = seed.username
WHERE NOT EXISTS (
    SELECT 1
    FROM book_requests br
    WHERE br.user_id = u.id
      AND br.book_title = seed.book_title
      AND br.author = seed.author
);
