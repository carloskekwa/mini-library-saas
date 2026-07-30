-- Flyway Migration: V8__Fix_borrow_records_demand_status_constraint.sql
-- Expands borrow_records.status CHECK to include demand lifecycle states used by the app.

ALTER TABLE borrow_records
    DROP CHECK borrow_records_chk_1;

ALTER TABLE borrow_records
    ADD CONSTRAINT borrow_records_chk_1
        CHECK (status IN ('PENDING', 'REJECTED', 'BORROWED', 'RETURNED', 'OVERDUE', 'LOST'));
