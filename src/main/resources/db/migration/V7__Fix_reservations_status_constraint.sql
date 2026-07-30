-- Flyway Migration: V7__Fix_reservations_status_constraint.sql
-- Widens reservations.status CHECK to include all values used by the application:
-- PENDING (initial state), ACTIVE (legacy), NOTIFIED, FULFILLED, CANCELLED, EXPIRED.

ALTER TABLE reservations
    DROP CHECK reservations_chk_1;

ALTER TABLE reservations
    MODIFY COLUMN status VARCHAR(50) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'ACTIVE', 'NOTIFIED', 'FULFILLED', 'CANCELLED', 'EXPIRED'));

-- Migrate any existing ACTIVE rows (legacy default) to PENDING
UPDATE reservations SET status = 'PENDING' WHERE status = 'ACTIVE';
