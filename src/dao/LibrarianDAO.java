package dao;

import db.DatabaseConnection;
import model.Librarian;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for {@link Librarian}.
 */
public class LibrarianDAO implements GenericDAO<Librarian, Integer> {

    // ── CREATE ────────────────────────────────────────────────────────────────
    @Override
    public boolean create(Librarian lib) {
        String sql = """
            INSERT INTO librarians
                (name, email, phone, employee_code, department, hire_date, salary)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps =
                 DatabaseConnection.getInstance().prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, lib.getName());
            ps.setString(2, lib.getEmail());
            ps.setString(3, lib.getPhone());
            ps.setString(4, lib.getEmployeeCode());
            ps.setString(5, lib.getDepartment());
            ps.setString(6, lib.getHireDate().toString());
            ps.setDouble(7, lib.getSalary());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) lib.setId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[LibrarianDAO] create error: " + e.getMessage());
        }
        return false;
    }

    // ── READ ──────────────────────────────────────────────────────────────────
    @Override
    public Optional<Librarian> findById(Integer id) {
        String sql = "SELECT * FROM librarians WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[LibrarianDAO] findById error: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Librarian> findAll() {
        List<Librarian> list = new ArrayList<>();
        String sql = "SELECT * FROM librarians ORDER BY name";
        try (Statement stmt = DatabaseConnection.getInstance().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[LibrarianDAO] findAll error: " + e.getMessage());
        }
        return list;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    @Override
    public boolean update(Librarian lib) {
        String sql = """
            UPDATE librarians SET name = ?, email = ?, phone = ?,
                employee_code = ?, department = ?, hire_date = ?, salary = ?
            WHERE id = ?
            """;
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, lib.getName());
            ps.setString(2, lib.getEmail());
            ps.setString(3, lib.getPhone());
            ps.setString(4, lib.getEmployeeCode());
            ps.setString(5, lib.getDepartment());
            ps.setString(6, lib.getHireDate().toString());
            ps.setDouble(7, lib.getSalary());
            ps.setInt   (8, lib.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[LibrarianDAO] update error: " + e.getMessage());
        }
        return false;
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM librarians WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[LibrarianDAO] delete error: " + e.getMessage());
        }
        return false;
    }

    // ── SEARCH ────────────────────────────────────────────────────────────────
    @Override
    public List<Librarian> search(String keyword) {
        List<Librarian> list = new ArrayList<>();
        String like = "%" + keyword.toLowerCase() + "%";
        String sql  = """
            SELECT * FROM librarians
            WHERE LOWER(name) LIKE ? OR LOWER(department) LIKE ?
               OR LOWER(employee_code) LIKE ?
            ORDER BY name
            """;
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            for (int i = 1; i <= 3; i++) ps.setString(i, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[LibrarianDAO] search error: " + e.getMessage());
        }
        return list;
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private Librarian mapRow(ResultSet rs) throws SQLException {
        return new Librarian(
            rs.getInt   ("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("employee_code"),
            rs.getString("department"),
            LocalDate.parse(rs.getString("hire_date")),
            rs.getDouble("salary")
        );
    }
}
