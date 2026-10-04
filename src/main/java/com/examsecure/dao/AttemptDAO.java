package com.examsecure.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.examsecure.model.Attempt;
import com.examsecure.util.DBConnection;

public class AttemptDAO {

    public List<Attempt> findAllByUser(int userId) {

        List<Attempt> attempts = new ArrayList<>();

        String sql =
                "SELECT attempt_id, exam_id, user_id, " +
                "start_time, end_time, status, score " +
                "FROM attempts " +
                "WHERE user_id = ? " +
                "ORDER BY attempt_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Attempt a = new Attempt();

                    a.setId(rs.getInt("attempt_id"));
                    a.setExamId(rs.getInt("exam_id"));
                    a.setStudentId(rs.getInt("user_id"));

                    Timestamp start = rs.getTimestamp("start_time");
                    if (start != null) {
                        a.setStartTime(start.toLocalDateTime());
                    }

                    Timestamp end = rs.getTimestamp("end_time");
                    if (end != null) {
                        a.setSubmittedAt(end.toLocalDateTime());
                    }

                    a.setStatus(rs.getString("status"));
                    a.setScore(rs.getInt("score"));

                    attempts.add(a);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return attempts;
    }


    public Attempt find(int userId, int examId) {

        String sql =
                "SELECT attempt_id, exam_id, user_id, " +
                "start_time, end_time, status, score " +
                "FROM attempts " +
                "WHERE user_id = ? AND exam_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, examId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Attempt a = new Attempt();

                    a.setId(rs.getInt("attempt_id"));
                    a.setExamId(rs.getInt("exam_id"));
                    a.setStudentId(rs.getInt("user_id"));

                    Timestamp start = rs.getTimestamp("start_time");
                    if (start != null) {
                        a.setStartTime(start.toLocalDateTime());
                    }

                    Timestamp end = rs.getTimestamp("end_time");
                    if (end != null) {
                        a.setSubmittedAt(end.toLocalDateTime());
                    }

                    a.setStatus(rs.getString("status"));
                    a.setScore(rs.getInt("score"));

                    return a;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public boolean hasAttempted(int userId, int examId) {

        String sql =
                "SELECT COUNT(*) FROM attempts " +
                "WHERE user_id = ? AND exam_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, examId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public boolean start(
            int userId,
            int examId,
            LocalDateTime startTime) {

        String sql =
                "INSERT INTO attempts " +
                "(user_id, exam_id, start_time, status, score) " +
                "VALUES (?, ?, ?, 'IN_PROGRESS', 0)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, examId);
            ps.setTimestamp(3, Timestamp.valueOf(startTime));

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean finish(
            int attemptId,
            String status,
            Map<Integer, String> answers) {

        Connection con = null;

        try {

            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            int score = 0;

            if (answers != null && !answers.isEmpty()) {

                String sql =
                        "SELECT correct_answer " +
                        "FROM questions WHERE question_id = ?";

                try (PreparedStatement ps =
                             con.prepareStatement(sql)) {

                    for (Map.Entry<Integer, String> entry
                            : answers.entrySet()) {

                        ps.setInt(1, entry.getKey());

                        try (ResultSet rs = ps.executeQuery()) {

                            if (rs.next()) {

                                String correct =
                                        rs.getString("correct_answer");

                                if (entry.getValue() != null &&
                                    entry.getValue().equalsIgnoreCase(correct)) {

                                    score += 1;
                                }
                            }
                        }
                    }
                }
            }

            String sql =
                    "UPDATE attempts " +
                    "SET end_time = CURRENT_TIMESTAMP, " +
                    "status = ?, score = ? " +
                    "WHERE attempt_id = ? " +
                    "AND status = 'IN_PROGRESS'";

            try (PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setString(1, status);
                ps.setInt(2, score);
                ps.setInt(3, attemptId);

                if (ps.executeUpdate() != 1) {
                    con.rollback();
                    return false;
                }
            }

            con.commit();
            return true;

        } catch (Exception e) {

            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception ignored) {
                }
            }

            e.printStackTrace();
            return false;

        } finally {

            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}