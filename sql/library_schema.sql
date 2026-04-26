-- ============================================================
--  Library Management System — Database Schema & Sample Data
--  Compatible with: MySQL 8+, PostgreSQL 13+, SQLite 3+
-- ============================================================

-- ── (MySQL / PostgreSQL) Create & use the database ──────────
-- CREATE DATABASE library_db CHARACTER SET utf8mb4;
-- USE library_db;

-- ── Enable foreign keys (SQLite only) ───────────────────────
PRAGMA foreign_keys = ON;

-- ============================================================
--  TABLE: members
-- ============================================================
CREATE TABLE IF NOT EXISTS members (
    id                INTEGER      PRIMARY KEY AUTOINCREMENT,
    name              TEXT         NOT NULL,
    email             TEXT         NOT NULL UNIQUE,
    phone             TEXT,
    membership_type   TEXT         NOT NULL DEFAULT 'STANDARD',
    join_date         TEXT         NOT NULL,
    expiry_date       TEXT         NOT NULL,
    books_checked_out INTEGER      NOT NULL DEFAULT 0,
    active            INTEGER      NOT NULL DEFAULT 1
);

-- ============================================================
--  TABLE: librarians
-- ============================================================
CREATE TABLE IF NOT EXISTS librarians (
    id            INTEGER  PRIMARY KEY AUTOINCREMENT,
    name          TEXT     NOT NULL,
    email         TEXT     NOT NULL UNIQUE,
    phone         TEXT,
    employee_code TEXT     NOT NULL UNIQUE,
    department    TEXT     NOT NULL,
    hire_date     TEXT     NOT NULL,
    salary        REAL     NOT NULL DEFAULT 0.0
);

-- ============================================================
--  TABLE: books
-- ============================================================
CREATE TABLE IF NOT EXISTS books (
    id               INTEGER  PRIMARY KEY AUTOINCREMENT,
    title            TEXT     NOT NULL,
    author           TEXT     NOT NULL,
    isbn             TEXT     NOT NULL UNIQUE,
    genre            TEXT,
    publish_year     INTEGER,
    total_copies     INTEGER  NOT NULL DEFAULT 1,
    available_copies INTEGER  NOT NULL DEFAULT 1
);

-- ============================================================
--  TABLE: transactions
-- ============================================================
CREATE TABLE IF NOT EXISTS transactions (
    id            INTEGER  PRIMARY KEY AUTOINCREMENT,
    member_id     INTEGER  NOT NULL,
    book_id       INTEGER  NOT NULL,
    librarian_id  INTEGER  NOT NULL,
    borrow_date   TEXT     NOT NULL,
    due_date      TEXT     NOT NULL,
    return_date   TEXT,                        -- NULL until returned
    status        TEXT     NOT NULL DEFAULT 'BORROWED',
    fine_amount   REAL     NOT NULL DEFAULT 0.0,
    FOREIGN KEY (member_id)    REFERENCES members(id),
    FOREIGN KEY (book_id)      REFERENCES books(id),
    FOREIGN KEY (librarian_id) REFERENCES librarians(id)
);

-- ============================================================
--  SAMPLE DATA
-- ============================================================

-- Librarians
INSERT INTO librarians (name, email, phone, employee_code, department, hire_date, salary) VALUES
  ('Alice Johnson',  'alice@library.com', '555-0101', 'EMP001', 'General',   '2022-01-15', 55000.00),
  ('Bob Martinez',   'bob@library.com',   '555-0102', 'EMP002', 'Reference', '2021-06-01', 52000.00),
  ('Clara Singh',    'clara@library.com', '555-0103', 'EMP003', 'Children',  '2023-03-10', 49000.00);

-- Members
INSERT INTO members (name, email, phone, membership_type, join_date, expiry_date, books_checked_out, active) VALUES
  ('Carol White',   'carol@mail.com', '555-1001', 'STANDARD', '2024-01-10', '2025-01-10', 0, 1),
  ('David Brown',   'david@mail.com', '555-1002', 'PREMIUM',  '2023-09-05', '2025-09-05', 0, 1),
  ('Emma Davis',    'emma@mail.com',  '555-1003', 'STUDENT',  '2024-03-20', '2025-03-20', 0, 1),
  ('Frank Wilson',  'frank@mail.com', '555-1004', 'STANDARD', '2024-07-01', '2025-07-01', 0, 1),
  ('Grace Lee',     'grace@mail.com', '555-1005', 'PREMIUM',  '2023-11-15', '2025-11-15', 0, 1);

-- Books
INSERT INTO books (title, author, isbn, genre, publish_year, total_copies, available_copies) VALUES
  ('Clean Code',                    'Robert C. Martin',       '978-0132350884', 'Technology',   2008, 3, 3),
  ('The Great Gatsby',              'F. Scott Fitzgerald',    '978-0743273565', 'Fiction',       1925, 2, 2),
  ('Sapiens',                       'Yuval Noah Harari',      '978-0062316097', 'History',       2011, 4, 4),
  ('Design Patterns',               'Gang of Four',           '978-0201633610', 'Technology',   1994, 2, 2),
  ('To Kill a Mockingbird',         'Harper Lee',             '978-0061935466', 'Fiction',       1960, 3, 3),
  ('Thinking, Fast and Slow',       'Daniel Kahneman',        '978-0374533557', 'Psychology',   2011, 2, 2),
  ('The Pragmatic Programmer',      'Andrew Hunt',            '978-0201616224', 'Technology',   1999, 3, 3),
  ('1984',                          'George Orwell',          '978-0451524935', 'Fiction',       1949, 5, 5),
  ('Educated',                      'Tara Westover',          '978-0399590504', 'Biography',    2018, 2, 2),
  ('Atomic Habits',                 'James Clear',            '978-0735211292', 'Self-Help',    2018, 4, 4),
  ('Introduction to Algorithms',   'Cormen et al.',          '978-0262033848', 'Technology',   2009, 2, 2),
  ('The Power of Now',              'Eckhart Tolle',          '978-1577314806', 'Self-Help',    1997, 3, 3);

-- Sample transactions
INSERT INTO transactions (member_id, book_id, librarian_id, borrow_date, due_date, return_date, status, fine_amount) VALUES
  (1, 1, 1, '2025-01-05', '2025-01-19', '2025-01-18', 'RETURNED', 0.00),
  (2, 3, 1, '2025-02-10', '2025-02-24', NULL,          'BORROWED', 0.00),
  (3, 8, 2, '2025-01-20', '2025-02-03', '2025-02-10', 'RETURNED', 3.50),
  (4, 5, 1, '2025-03-01', '2025-03-15', NULL,          'BORROWED', 0.00);

-- ============================================================
--  USEFUL QUERIES
-- ============================================================

-- Active borrows with member and book details:
-- SELECT t.id, m.name, b.title, t.borrow_date, t.due_date
-- FROM transactions t
-- JOIN members m ON t.member_id = m.id
-- JOIN books   b ON t.book_id   = b.id
-- WHERE t.status != 'RETURNED';

-- Books with low availability:
-- SELECT title, author, available_copies, total_copies
-- FROM books WHERE available_copies = 0;

-- Members with most books checked out:
-- SELECT name, books_checked_out, membership_type
-- FROM members ORDER BY books_checked_out DESC;
