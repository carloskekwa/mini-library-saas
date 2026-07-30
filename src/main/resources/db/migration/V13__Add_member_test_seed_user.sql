-- Flyway Migration: V13__Add_member_test_seed_user.sql
-- Adds member_test seed user with MEMBER role (password: Admin1234!)

INSERT INTO users (username, email, password_hash, first_name, last_name, status) VALUES
('member_test', 'member_test@library.local', '$2a$10$60WDXUw/Eqk4HAV/0X7lc.QhqNkYEO3vwswG3A9vPfmWQYsIDanFG', 'Test', 'Member', 'ACTIVE');

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'member_test' AND r.name = 'MEMBER';
