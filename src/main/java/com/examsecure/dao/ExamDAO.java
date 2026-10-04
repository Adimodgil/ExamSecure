package com.examsecure.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.examsecure.model.Exam;
import com.examsecure.util.DBConnection;

public class ExamDAO {

    public List<Exam> getAllExams() {

        List<Exam> exams = new ArrayList<>();

        String sql =
                "SELECT e.exam_id, e.exam_name, e.description, " +
                "e.duration_minutes, e.total_marks, e.created_by, " +
                "e.start_time, e.end_time, " +
                "(SELECT COUNT(*) FROM questions q " +
                "WHERE q.exam_id = e.exam_id) AS question_count " +
                "FROM exams e " +
                "ORDER BY e.exam_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Exam exam = new Exam();

                exam.setId(rs.getInt("exam_id"));
                exam.setTitle(rs.getString("exam_name"));
                exam.setDescription(rs.getString("description"));
                exam.setDurationMinutes(rs.getInt("duration_minutes"));
                exam.setTotalMarks(rs.getInt("total_marks"));
                exam.setCreatedBy(rs.getInt("created_by"));

                Timestamp start = rs.getTimestamp("start_time");

                if (start != null) {
                    exam.setOpensAt(start.toLocalDateTime());
                }

                Timestamp end = rs.getTimestamp("end_time");

                if (end != null) {
                    exam.setClosesAt(end.toLocalDateTime());
                }

                exam.setQuestionCount(
                        rs.getInt("question_count"));

                exams.add(exam);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return exams;
    }


    public Exam findById(int id) {

        String sql =
                "SELECT e.exam_id, e.exam_name, e.description, " +
                "e.duration_minutes, e.total_marks, e.created_by, " +
                "e.start_time, e.end_time, e.pdf_file, " +
                "(SELECT COUNT(*) FROM questions q " +
                "WHERE q.exam_id = e.exam_id) AS question_count " +
                "FROM exams e " +
                "WHERE e.exam_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Exam exam = new Exam();

                    exam.setId(rs.getInt("exam_id"));
                    exam.setTitle(rs.getString("exam_name"));
                    exam.setDescription(rs.getString("description"));

                    exam.setDurationMinutes(
                            rs.getInt("duration_minutes"));

                    exam.setTotalMarks(
                            rs.getInt("total_marks"));

                    exam.setCreatedBy(
                            rs.getInt("created_by"));

                    Timestamp start =
                            rs.getTimestamp("start_time");

                    if (start != null) {
                        exam.setOpensAt(
                                start.toLocalDateTime());
                    }

                    Timestamp end =
                            rs.getTimestamp("end_time");

                    if (end != null) {
                        exam.setClosesAt(
                                end.toLocalDateTime());
                    }

                    exam.setPdfFile(
                            rs.getString("pdf_file"));

                    exam.setQuestionCount(
                            rs.getInt("question_count"));

                    return exam;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    // Throws the real database error so the Create Exam page can show it
    public int createExam(Exam exam) throws Exception {

        String sql =
                "INSERT INTO exams " +
                "(exam_name, description, duration_minutes, " +
                "total_marks, created_by, start_time, end_time, pdf_file) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql,
                     java.sql.Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, exam.getTitle());
            ps.setString(2, exam.getDescription());
            ps.setInt(3, exam.getDurationMinutes());
            ps.setInt(4, exam.getTotalMarks());
            ps.setInt(5, exam.getCreatedBy());

            ps.setTimestamp(
                    6,
                    Timestamp.valueOf(exam.getOpensAt()));

            ps.setTimestamp(
                    7,
                    Timestamp.valueOf(exam.getClosesAt()));

            ps.setString(
                    8,
                    exam.getPdfFile());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return -1;
    }


    // Each row = {username, score, status, startTime, endTime}
    public List<String[]> findResults(int examId) {

        List<String[]> rows = new ArrayList<>();

        String sql =
                "SELECT u.username, a.score, a.status, a.start_time, a.end_time " +
                "FROM attempts a JOIN users u ON u.user_id = a.user_id " +
                "WHERE a.exam_id = ? ORDER BY a.score DESC, u.username";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, examId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp s = rs.getTimestamp("start_time");
                    Timestamp e = rs.getTimestamp("end_time");
                    rows.add(new String[] {
                            rs.getString("username"),
                            String.valueOf(rs.getInt("score")),
                            rs.getString("status"),
                            s == null ? "-" : s.toLocalDateTime().toString().replace('T', ' '),
                            e == null ? "-" : e.toLocalDateTime().toString().replace('T', ' ')
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }


    // Changes title, description, date/time and duration of one exam
    public boolean updateExam(int examId, String title, String description,
                              java.time.LocalDateTime start, int duration) {

        String sql =
                "UPDATE exams SET exam_name = ?, description = ?, " +
                "duration_minutes = ?, start_time = ?, end_time = ? " +
                "WHERE exam_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, description);
            ps.setInt(3, duration);
            ps.setTimestamp(4, Timestamp.valueOf(start));
            ps.setTimestamp(5, Timestamp.valueOf(start.plusMinutes(duration)));
            ps.setInt(6, examId);

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}