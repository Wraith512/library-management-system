package db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Creates all tables and inserts sample seed data on first run.
 */
public class DatabaseInitializer {

    public static void initialize() {
        try (Connection conn = DatabaseConnection.getInstance();
             Statement  stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON;");

            // ── Members table ─────────────────────────────────────────────
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS members (
                    id               INTEGER PRIMARY KEY AUTOINCREMENT,
                    name             TEXT    NOT NULL,
                    email            TEXT    NOT NULL UNIQUE,
                    phone            TEXT,
                    membership_type  TEXT    NOT NULL DEFAULT 'STANDARD',
                    join_date        TEXT    NOT NULL,
                    expiry_date      TEXT    NOT NULL,
                    books_checked_out INTEGER NOT NULL DEFAULT 0,
                    active           INTEGER NOT NULL DEFAULT 1
                );
            """);

            // ── Librarians table ──────────────────────────────────────────
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS librarians (
                    id            INTEGER PRIMARY KEY AUTOINCREMENT,
                    name          TEXT    NOT NULL,
                    email         TEXT    NOT NULL UNIQUE,
                    phone         TEXT,
                    employee_code TEXT    NOT NULL UNIQUE,
                    department    TEXT    NOT NULL,
                    hire_date     TEXT    NOT NULL,
                    salary        REAL    NOT NULL DEFAULT 0.0
                );
            """);

            // ── Books table ───────────────────────────────────────────────
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS books (
                    id               INTEGER PRIMARY KEY AUTOINCREMENT,
                    title            TEXT    NOT NULL,
                    author           TEXT    NOT NULL,
                    isbn             TEXT    NOT NULL UNIQUE,
                    genre            TEXT,
                    publish_year     INTEGER,
                    total_copies     INTEGER NOT NULL DEFAULT 1,
                    available_copies INTEGER NOT NULL DEFAULT 1
                );
            """);

            // ── Transactions table ────────────────────────────────────────
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS transactions (
                    id            INTEGER PRIMARY KEY AUTOINCREMENT,
                    member_id     INTEGER NOT NULL,
                    book_id       INTEGER NOT NULL,
                    librarian_id  INTEGER NOT NULL,
                    borrow_date   TEXT    NOT NULL,
                    due_date      TEXT    NOT NULL,
                    return_date   TEXT,
                    status        TEXT    NOT NULL DEFAULT 'BORROWED',
                    fine_amount   REAL    NOT NULL DEFAULT 0.0,
                    FOREIGN KEY (member_id)    REFERENCES members(id),
                    FOREIGN KEY (book_id)      REFERENCES books(id),
                    FOREIGN KEY (librarian_id) REFERENCES librarians(id)
                );
            """);

            // ── Seed data (only if tables empty) ─────────────────────────
            seedIfEmpty(stmt);

            System.out.println("[DB] Schema initialised successfully.");

        } catch (SQLException e) {
            System.err.println("[DB] Initialisation error: " + e.getMessage());
        }
    }

    private static void seedIfEmpty(Statement stmt) throws SQLException {
        var rs = stmt.executeQuery("SELECT COUNT(*) FROM books");
        if (rs.getInt(1) > 0) return;   // already seeded

        // Librarians
        stmt.execute("""
            INSERT INTO librarians (name,email,phone,employee_code,department,hire_date,salary) VALUES
            ('Alice Johnson','alice@library.com','555-0101','EMP001','General',date('now'),55000),
            ('Bob Martinez','bob@library.com','555-0102','EMP002','Reference',date('now'),52000);
        """);

        // Members
        stmt.execute("""
            INSERT INTO members (name,email,phone,membership_type,join_date,expiry_date,books_checked_out,active) VALUES
            ('Carol White','carol@mail.com','555-1001','STANDARD',date('now'),date('now','+1 year'),0,1),
            ('David Brown','david@mail.com','555-1002','PREMIUM',date('now'),date('now','+1 year'),0,1),
            ('Emma Davis','emma@mail.com','555-1003','STUDENT',date('now'),date('now','+1 year'),0,1);
        """);

        // Books
        stmt.execute("""
            INSERT INTO books (title,author,isbn,genre,publish_year,total_copies,available_copies) VALUES
            ('Clean Code','Robert C. Martin','978-0132350884','Technology',2008,3,3),
            ('The Great Gatsby','F. Scott Fitzgerald','978-0743273565','Fiction',1925,2,2),
            ('Sapiens','Yuval Noah Harari','978-0062316097','History',2011,4,4),
            ('Design Patterns','Gang of Four','978-0201633610','Technology',1994,2,2),
            ('To Kill a Mockingbird','Harper Lee','978-0061935466','Fiction',1960,3,3),
            ('Thinking, Fast and Slow','Daniel Kahneman','978-0374533557','Psychology',2011,2,2),
            ('The Pragmatic Programmer','Andrew Hunt','978-0201616224','Technology',1999,3,3),
            ('1984','George Orwell','978-0451524935','Fiction',1949,5,5),
            ('Educated','Tara Westover','978-0399590504','Biography',2018,2,2),
            ('Atomic Habits','James Clear','978-0735211292','Self-Help',2018,4,4);
        """);

        System.out.println("[DB] Sample data seeded.");
    }
}
