-- Keep overdue seed data only for member2.
-- This is additive (new migration) to avoid changing checksums of already-applied migrations.

-- Remove return records tied to overdue borrows for any user except member2.
DELETE rr
FROM return_records rr
JOIN borrow_records br ON br.id = rr.borrow_id
JOIN users u ON u.id = br.user_id
WHERE br.status = 'OVERDUE'
  AND u.username <> 'member2';

-- Remove overdue borrows for any user except member2.
DELETE br
FROM borrow_records br
JOIN users u ON u.id = br.user_id
WHERE br.status = 'OVERDUE'
  AND u.username <> 'member2';
