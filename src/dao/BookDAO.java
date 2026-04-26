package dao;

import db.DatabaseConnection;
import model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for {@link Book}.
 * Demonstrates: JDBC (PreparedStatement), Interface implementation.
 */
public class BookDAO implements GenericDAO<Book, Integer> {

    // ── CREATE ────────────────────────────────────────────────────────────────
    @Override
    public boolean create(Book book) {
        String sql = """
            INSERT INTO books (title, author, isbn, genre, publish_year,
                               total_copies, available_copies)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps =
                 DatabaseConnection.getInstance().prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setString(4, book.getGenre());
            ps.setInt   (5, book.getPublishYear());
            ps.setInt   (6, book.getTotalCopies());
            ps.setInt   (7, book.getAvailableCopies());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) book.setId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[BookDAO] create error: " + e.getMessage());
        }
        return false;
    }

    // ── READ ──────────────────────────────────────────────────────────────────
    @Override
    public Optional<Book> findById(Integer id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[BookDAO] findById error: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY title";
        try (Statement stmt = DatabaseConnection.getInstance().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) books.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[BookDAO] findAll error: " + e.getMessage());
        }
        return books;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    @Override
    public boolean update(Book book) {
        String sql = """
            UPDATE books SET title = ?, author = ?, isbn = ?, genre = ?,
                             publish_year = ?, total_copies = ?, available_copies = ?
            WHERE id = ?
            """;
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setString(4, book.getGenre());
            ps.setInt   (5, book.getPublishYear());
            ps.setInt   (6, book.getTotalCopies());
            ps.setInt   (7, book.getAvailableCopies());
            ps.setInt   (8, book.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BookDAO] update error: " + e.getMessage());
        }
        return false;
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BookDAO] delete error: " + e.getMessage());
        }
        return false;
    }

    // ── SEARCH ────────────────────────────────────────────────────────────────
    @Override
    public List<Book> search(String keyword) {
        List<Book> books = new ArrayList<>();
        String like = "%" + keyword.toLowerCase() + "%";
        String sql  = """
            SELECT * FROM books
            WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ?
               OR LOWER(isbn)  LIKE ? OR LOWER(genre)  LIKE ?
            ORDER BY title
            """;
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            for (int i = 1; i <= 4; i++) ps.setString(i, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) books.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[BookDAO] search error: " + e.getMessage());
        }
        return books;
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private Book mapRow(ResultSet rs) throws SQLException {
        return new Book(
            rs.getInt   ("id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("isbn"),
            rs.getString("genre"),
            rs.getInt   ("publish_year"),
            rs.getInt   ("total_copies"),
            rs.getInt   ("available_copies")
        );
    }
}
