-- Remove overdue seeded borrow for member1 and seed return/fine records for member2.

CREATE TABLE IF NOT EXISTS return_records (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        borrow_id BIGINT NOT NULL UNIQUE,
        user_id BIGINT NOT NULL,
        return_date DATETIME NOT NULL,
        book_condition ENUM('EXCELLENT','GOOD','FAIR','POOR','DAMAGED') NOT NULL DEFAULT 'GOOD',
        damage_notes VARCHAR(500),
        days_late INT DEFAULT 0,
        fine_amount DECIMAL(10,2) DEFAULT 0.00,
        fine_paid BIT(1) DEFAULT b'0',
        fine_paid_date DATETIME,
        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_return_borrow FOREIGN KEY (borrow_id) REFERENCES borrow_records(id),
        CONSTRAINT fk_return_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 1) Remove member1 overdue seeded borrow data.
DELETE rr
FROM return_records rr
JOIN borrow_records br ON br.id = rr.borrow_id
JOIN users u ON u.id = br.user_id
WHERE u.username = 'member1'
    AND br.status = 'OVERDUE';

DELETE br
FROM borrow_records br
JOIN users u ON u.id = br.user_id
WHERE u.username = 'member1'
    AND br.status = 'OVERDUE';

-- 2) Ensure member2 has return/fine records, including unpaid lost-book fine.
INSERT INTO return_records (
        book_condition,
        created_at,
        damage_notes,
        days_late,
        fine_amount,
        fine_paid,
        fine_paid_date,
        return_date,
        borrow_id,
        user_id
)
SELECT
        seed.book_condition,
        seed.created_at,
        seed.damage_notes,
        seed.days_late,
        seed.fine_amount,
        seed.fine_paid,
        seed.fine_paid_date,
        seed.return_date,
        seed.borrow_id,
        seed.user_id
FROM (
        SELECT
                'GOOD' AS book_condition,
                TIMESTAMP('2026-07-15 17:05:00') AS created_at,
                'Returned in good condition after short delay.' AS damage_notes,
                1 AS days_late,
                2.00 AS fine_amount,
                b'1' AS fine_paid,
                TIMESTAMP('2026-07-16 09:30:00') AS fine_paid_date,
                TIMESTAMP('2026-07-15 17:00:00') AS return_date,
                (
                        SELECT MAX(br_returned.id)
                        FROM borrow_records br_returned
                        WHERE br_returned.user_id = u.id
                            AND br_returned.status = 'RETURNED'
                ) AS borrow_id,
                u.id AS user_id
        FROM users u
        WHERE u.username = 'member2'
            AND EXISTS (
                    SELECT 1
                    FROM borrow_records br_returned
                    WHERE br_returned.user_id = u.id
                        AND br_returned.status = 'RETURNED'
            )

        UNION ALL

        SELECT
                'DAMAGED' AS book_condition,
                TIMESTAMP('2026-07-11 10:05:00') AS created_at,
                'Book reported lost. Replacement fine pending payment.' AS damage_notes,
                15 AS days_late,
                30.00 AS fine_amount,
                b'0' AS fine_paid,
                NULL AS fine_paid_date,
                TIMESTAMP('2026-07-11 10:00:00') AS return_date,
                (
                        SELECT MAX(br_lost.id)
                        FROM borrow_records br_lost
                        WHERE br_lost.user_id = u.id
                            AND br_lost.status = 'LOST'
                ) AS borrow_id,
                u.id AS user_id
        FROM users u
        WHERE u.username = 'member2'
            AND EXISTS (
                    SELECT 1
                    FROM borrow_records br_lost
                    WHERE br_lost.user_id = u.id
                        AND br_lost.status = 'LOST'
            )
) seed
WHERE seed.borrow_id IS NOT NULL
    AND NOT EXISTS (
            SELECT 1
            FROM return_records rr
            WHERE rr.borrow_id = seed.borrow_id
    );
