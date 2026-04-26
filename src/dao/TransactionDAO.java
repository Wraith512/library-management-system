package dao;

import db.DatabaseConnection;
import model.Transaction;
import model.Transaction.Status;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for {@link Transaction}.
 */
public class TransactionDAO implements GenericDAO<Transaction, Integer> {

    // ── CREATE ────────────────────────────────────────────────────────────────
    @Override
    public boolean create(Transaction t) {
        String sql = """
            INSERT INTO transactions
                (member_id, book_id, librarian_id, borrow_date,
                 due_date, return_date, status, fine_amount)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps =
                 DatabaseConnection.getInstance().prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, t.getMemberId());
            ps.setInt   (2, t.getBookId());
            ps.setInt   (3, t.getLibrarianId());
            ps.setString(4, t.getBorrowDate().toString());
            ps.setString(5, t.getDueDate().toString());
            ps.setString(6, t.getReturnDate() != null ? t.getReturnDate().toString() : null);
            ps.setString(7, t.getStatus().name());
            ps.setDouble(8, t.getFineAmount());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) t.setId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[TransactionDAO] create error: " + e.getMessage());
        }
        return false;
    }

    // ── READ ──────────────────────────────────────────────────────────────────
    @Override
    public Optional<Transaction> findById(Integer id) {
        String sql = joinedQuery() + " WHERE t.id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[TransactionDAO] findById error: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> list = new ArrayList<>();
        String sql = joinedQuery() + " ORDER BY t.borrow_date DESC";
        try (Statement stmt = DatabaseConnection.getInstance().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[TransactionDAO] findAll error: " + e.getMessage());
        }
        return list;
    }

    /** Returns all active (not-returned) transactions. */
    public List<Transaction> findActive() {
        List<Transaction> list = new ArrayList<>();
        String sql = joinedQuery() + " WHERE t.status != 'RETURNED' ORDER BY t.due_date";
        try (Statement stmt = DatabaseConnection.getInstance().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[TransactionDAO] findActive error: " + e.getMessage());
        }
        return list;
    }

    /** Returns transactions for a specific member. */
    public List<Transaction> findByMember(int memberId) {
        List<Transaction> list = new ArrayList<>();
        String sql = joinedQuery() + " WHERE t.member_id = ? ORDER BY t.borrow_date DESC";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[TransactionDAO] findByMember error: " + e.getMessage());
        }
        return list;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    @Override
    public boolean update(Transaction t) {
        String sql = """
            UPDATE transactions SET return_date = ?, status = ?, fine_amount = ?
            WHERE id = ?
            """;
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, t.getReturnDate() != null ? t.getReturnDate().toString() : null);
            ps.setString(2, t.getStatus().name());
            ps.setDouble(3, t.getFineAmount());
            ps.setInt   (4, t.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[TransactionDAO] update error: " + e.getMessage());
        }
        return false;
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[TransactionDAO] delete error: " + e.getMessage());
        }
        return false;
    }

    // ── SEARCH ────────────────────────────────────────────────────────────────
    @Override
    public List<Transaction> search(String keyword) {
        List<Transaction> list = new ArrayList<>();
        String like = "%" + keyword.toLowerCase() + "%";
        String sql  = joinedQuery()
            + " WHERE LOWER(m.name) LIKE ? OR LOWER(b.title) LIKE ? OR t.status LIKE ?"
            + " ORDER BY t.borrow_date DESC";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            for (int i = 1; i <= 3; i++) ps.setString(i, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[TransactionDAO] search error: " + e.getMessage());
        }
        return list;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private String joinedQuery() {
        return """
            SELECT t.*, m.name AS member_name, b.title AS book_title,
                   l.name AS librarian_name
            FROM   transactions t
            JOIN   members    m ON t.member_id    = m.id
            JOIN   books      b ON t.book_id      = b.id
            JOIN   librarians l ON t.librarian_id = l.id
            """;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        String retStr = rs.getString("return_date");
        Transaction t = new Transaction(
            rs.getInt   ("id"),
            rs.getInt   ("member_id"),
            rs.getInt   ("book_id"),
            rs.getInt   ("librarian_id"),
            LocalDate.parse(rs.getString("borrow_date")),
            LocalDate.parse(rs.getString("due_date")),
            retStr != null ? LocalDate.parse(retStr) : null,
            Status.valueOf(rs.getString("status")),
            rs.getDouble("fine_amount")
        );
        t.setMemberName  (rs.getString("member_name"));
        t.setBookTitle   (rs.getString("book_title"));
        t.setLibrarianName(rs.getString("librarian_name"));
        return t;
    }
}
