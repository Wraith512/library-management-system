package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton database connection manager.
 * Demonstrates: JDBC connectivity, Singleton pattern.
 *
 * ── Configuration ─────────────────────────────────────────────────────────
 * Default: SQLite (zero-install, file-based). Swap the constants below
 * and add the matching JDBC driver JAR to use MySQL / PostgreSQL instead.
 *
 * MySQL  : jdbc:mysql://localhost:3306/library_db
 * Postgres: jdbc:postgresql://localhost:5432/library_db
 * SQLite : jdbc:sqlite:library.db          ← default
 */
public class DatabaseConnection {

    // ── Connection settings (edit to match your environment) ──────────────
    private static final String URL      = "jdbc:sqlite:library.db";
    private static final String USER     = "";   // not used by SQLite
    private static final String PASSWORD = "";   // not used by SQLite

    private static Connection instance;

    /** Private constructor - prevents external instantiation. */
    private DatabaseConnection() {}

    /**
     * Returns the shared {@link Connection}.
     * Creates a new connection the first time (or after it was closed).
     */
    public static Connection getInstance() throws SQLException {
        if (instance == null || instance.isClosed()) {
            try {
                // Load driver – required for older JDKs; harmless on Java 9+
                Class.forName("org.sqlite.JDBC");
                instance = DriverManager.getConnection(URL, USER, PASSWORD);
                instance.setAutoCommit(true);
                System.out.println("[DB] Connected: " + URL);
            } catch (ClassNotFoundException e) {
                throw new SQLException("JDBC driver not found: " + e.getMessage(), e);
            }
        }
        return instance;
    }

    /** Closes the shared connection (call on application shutdown). */
    public static void close() {
        if (instance != null) {
            try {
                instance.close();
                System.out.println("[DB] Connection closed.");
            } catch (SQLException e) {
                System.err.println("[DB] Error closing connection: " + e.getMessage());
            } finally {
                instance = null;
            }
        }
    }
}
