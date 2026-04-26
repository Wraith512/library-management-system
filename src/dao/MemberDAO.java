package dao;

import db.DatabaseConnection;
import model.Member;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for {@link Member}.
 */
public class MemberDAO implements GenericDAO<Member, Integer> {

    // ── CREATE ────────────────────────────────────────────────────────────────
    @Override
    public boolean create(Member m) {
        String sql = """
            INSERT INTO members
                (name, email, phone, membership_type, join_date,
                 expiry_date, books_checked_out, active)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps =
                 DatabaseConnection.getInstance().prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getEmail());
            ps.setString(3, m.getPhone());
            ps.setString(4, m.getMembershipType());
            ps.setString(5, m.getJoinDate().toString());
            ps.setString(6, m.getExpiryDate().toString());
            ps.setInt   (7, m.getBooksCheckedOut());
            ps.setInt   (8, m.isActive() ? 1 : 0);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) m.setId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[MemberDAO] create error: " + e.getMessage());
        }
        return false;
    }

    // ── READ ──────────────────────────────────────────────────────────────────
    @Override
    public Optional<Member> findById(Integer id) {
        String sql = "SELECT * FROM members WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[MemberDAO] findById error: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Member> findAll() {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT * FROM members ORDER BY name";
        try (Statement stmt = DatabaseConnection.getInstance().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[MemberDAO] findAll error: " + e.getMessage());
        }
        return list;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    @Override
    public boolean update(Member m) {
        String sql = """
            UPDATE members SET name = ?, email = ?, phone = ?,
                membership_type = ?, join_date = ?, expiry_date = ?,
                books_checked_out = ?, active = ?
            WHERE id = ?
            """;
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getEmail());
            ps.setString(3, m.getPhone());
            ps.setString(4, m.getMembershipType());
            ps.setString(5, m.getJoinDate().toString());
            ps.setString(6, m.getExpiryDate().toString());
            ps.setInt   (7, m.getBooksCheckedOut());
            ps.setInt   (8, m.isActive() ? 1 : 0);
            ps.setInt   (9, m.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MemberDAO] update error: " + e.getMessage());
        }
        return false;
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM members WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MemberDAO] delete error: " + e.getMessage());
        }
        return false;
    }

    // ── SEARCH ────────────────────────────────────────────────────────────────
    @Override
    public List<Member> search(String keyword) {
        List<Member> list = new ArrayList<>();
        String like = "%" + keyword.toLowerCase() + "%";
        String sql  = """
            SELECT * FROM members
            WHERE LOWER(name)  LIKE ? OR LOWER(email) LIKE ?
               OR LOWER(phone) LIKE ?
            ORDER BY name
            """;
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            for (int i = 1; i <= 3; i++) ps.setString(i, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[MemberDAO] search error: " + e.getMessage());
        }
        return list;
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private Member mapRow(ResultSet rs) throws SQLException {
        return new Member(
            rs.getInt    ("id"),
            rs.getString ("name"),
            rs.getString ("email"),
            rs.getString ("phone"),
            rs.getString ("membership_type"),
            LocalDate.parse(rs.getString("join_date")),
            LocalDate.parse(rs.getString("expiry_date")),
            rs.getInt    ("books_checked_out"),
            rs.getInt    ("active") == 1
        );
    }
}
