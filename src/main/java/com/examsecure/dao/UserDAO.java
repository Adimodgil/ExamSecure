package com.examsecure.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.examsecure.model.User;
import com.examsecure.util.DBConnection;
import com.examsecure.util.PasswordUtil;

public class UserDAO {

    // =========================
    // LOGIN
    // =========================

    public User login(String username, String password) {

        String sql =
                "SELECT user_id, username, password_hash, salt, role " +
                "FROM users WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    int userId = rs.getInt("user_id");
                    String dbUsername = rs.getString("username");
                    String storedHash = rs.getString("password_hash");
                    String salt = rs.getString("salt");
                    String role = rs.getString("role");

                    String enteredHash =
                            PasswordUtil.hash(salt, password);

                    if (enteredHash.equals(storedHash)) {

                        return new User(
                                userId,
                                dbUsername,
                                role
                        );
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // =========================
    // STUDENT REGISTER
    // =========================

    public String register(String username, String password) {

        String checkSql =
                "SELECT user_id FROM users WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(checkSql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return "Username already exists.";
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
            return "Database error while checking username.";
        }

        String salt = PasswordUtil.generateSalt();

        String passwordHash =
                PasswordUtil.hash(salt, password);

        String sql =
                "INSERT INTO users " +
                "(username, password_hash, salt, role) " +
                "VALUES (?, ?, ?, 'STUDENT')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, salt);

            int rowsInserted =
                    ps.executeUpdate();

            if (rowsInserted > 0) {
                return null;
            }

            return "Registration failed.";

        } catch (SQLException e) {

            e.printStackTrace();

            if (e.getErrorCode() == 1062) {
                return "Username already exists.";
            }

            return "Database error while creating account.";
        }
    }


    // =========================
    // TEACHER REGISTER
    // =========================

    public String registerTeacher(String username, String password) {

        String checkSql =
                "SELECT user_id FROM users WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(checkSql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return "Username already exists.";
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
            return "Database error while checking username.";
        }

        String salt = PasswordUtil.generateSalt();

        String passwordHash =
                PasswordUtil.hash(salt, password);

        String sql =
                "INSERT INTO users " +
                "(username, password_hash, salt, role) " +
                "VALUES (?, ?, ?, 'ADMIN')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, salt);

            int rowsInserted =
                    ps.executeUpdate();

            if (rowsInserted > 0) {
                return null;
            }

            return "Teacher registration failed.";

        } catch (SQLException e) {

            e.printStackTrace();

            if (e.getErrorCode() == 1062) {
                return "Username already exists.";
            }

            return "Database error while creating teacher account.";
        }
    }
}