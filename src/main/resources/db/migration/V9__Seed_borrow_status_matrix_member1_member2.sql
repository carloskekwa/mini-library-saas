-- Flyway Migration: V9__Seed_borrow_status_matrix_member1_member2.sql
-- Seeds borrow_records with all BorrowStatus values for member1 and member2.

INSERT INTO borrow_records (
    user_id,
    book_id,
    checkout_date,
    due_date,
    returned_date,
    status,
    fine_amount,
    created_at,
    borrow_date,
    is_overdue,
    renewal_count,
    return_date
)
SELECT
    u.id,
    s.book_id,
    s.borrow_date,
    s.due_date,
    s.returned_date,
    s.status,
    s.fine_amount,
    s.created_at,
    s.borrow_date,
    s.is_overdue,
    s.renewal_count,
    s.return_date
FROM (
    SELECT 'member1' AS username,  1 AS book_id, 'PENDING'  AS status, TIMESTAMP('2026-07-20 10:00:00') AS borrow_date, TIMESTAMP('2026-08-03 10:00:00') AS due_date, NULL AS returned_date, 0.00 AS fine_amount, TIMESTAMP('2026-07-20 10:00:00') AS created_at, b'0' AS is_overdue, 0 AS renewal_count, NULL AS return_date
    UNION ALL
    SELECT 'member1',             2,             'REJECTED',             TIMESTAMP('2026-07-18 11:00:00'),             TIMESTAMP('2026-08-01 11:00:00'),             NULL,                   0.00,               TIMESTAMP('2026-07-18 11:00:00'),             b'0',                 0,                    NULL
    UNION ALL
    SELECT 'member1',             3,             'BORROWED',             TIMESTAMP('2026-07-12 09:30:00'),             TIMESTAMP('2026-07-26 09:30:00'),             NULL,                   0.00,               TIMESTAMP('2026-07-12 09:30:00'),             b'0',                 1,                    NULL
    UNION ALL
    SELECT 'member1',             4,             'RETURNED',             TIMESTAMP('2026-07-01 08:00:00'),             TIMESTAMP('2026-07-15 08:00:00'),             TIMESTAMP('2026-07-14 16:00:00'), 0.00,               TIMESTAMP('2026-07-01 08:00:00'),             b'0',                 0,                    TIMESTAMP('2026-07-14 16:00:00')
    UNION ALL
    SELECT 'member1',             5,             'OVERDUE',              TIMESTAMP('2026-06-20 14:00:00'),             TIMESTAMP('2026-07-04 14:00:00'),             NULL,                   12.00,              TIMESTAMP('2026-06-20 14:00:00'),             b'1',                 0,                    NULL
    UNION ALL
    SELECT 'member1',             6,             'LOST',                 TIMESTAMP('2026-06-10 12:00:00'),             TIMESTAMP('2026-06-24 12:00:00'),             TIMESTAMP('2026-07-10 12:00:00'), 25.00,              TIMESTAMP('2026-06-10 12:00:00'),             b'0',                 0,                    TIMESTAMP('2026-07-10 12:00:00')

    UNION ALL
    SELECT 'member2',             7,             'PENDING',              TIMESTAMP('2026-07-21 10:15:00'),             TIMESTAMP('2026-08-04 10:15:00'),             NULL,                   0.00,               TIMESTAMP('2026-07-21 10:15:00'),             b'0',                 0,                    NULL
    UNION ALL
    SELECT 'member2',             8,             'REJECTED',             TIMESTAMP('2026-07-19 11:30:00'),             TIMESTAMP('2026-08-02 11:30:00'),             NULL,                   0.00,               TIMESTAMP('2026-07-19 11:30:00'),             b'0',                 0,                    NULL
    UNION ALL
    SELECT 'member2',             9,             'BORROWED',             TIMESTAMP('2026-07-13 09:45:00'),             TIMESTAMP('2026-07-27 09:45:00'),             NULL,                   0.00,               TIMESTAMP('2026-07-13 09:45:00'),             b'0',                 2,                    NULL
    UNION ALL
    SELECT 'member2',            10,             'RETURNED',             TIMESTAMP('2026-07-02 08:20:00'),             TIMESTAMP('2026-07-16 08:20:00'),             TIMESTAMP('2026-07-15 17:00:00'), 0.00,               TIMESTAMP('2026-07-02 08:20:00'),             b'0',                 0,                    TIMESTAMP('2026-07-15 17:00:00')
    UNION ALL
    SELECT 'member2',            11,             'OVERDUE',              TIMESTAMP('2026-06-22 14:30:00'),             TIMESTAMP('2026-07-06 14:30:00'),             NULL,                   8.00,               TIMESTAMP('2026-06-22 14:30:00'),             b'1',                 0,                    NULL
    UNION ALL
    SELECT 'member2',            12,             'LOST',                 TIMESTAMP('2026-06-12 12:30:00'),             TIMESTAMP('2026-06-26 12:30:00'),             TIMESTAMP('2026-07-11 10:00:00'), 30.00,              TIMESTAMP('2026-06-12 12:30:00'),             b'0',                 0,                    TIMESTAMP('2026-07-11 10:00:00')
) s
JOIN users u ON u.username = s.username
JOIN books b ON b.id = s.book_id
WHERE NOT EXISTS (
    SELECT 1
    FROM borrow_records br
    WHERE br.user_id = u.id
      AND br.book_id = s.book_id
      AND br.status = s.status
      AND br.borrow_date = s.borrow_date
);
