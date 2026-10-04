package com.examsecure.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.examsecure.model.Question;
import com.examsecure.util.DBConnection;

public class QuestionDAO {

    public List<Question> getByExam(int examId) {

        List<Question> questions = new ArrayList<>();

        String sql =
                "SELECT question_id, exam_id, question_text, " +
                "option_a, option_b, option_c, option_d " +
                "FROM questions " +
                "WHERE exam_id = ? " +
                "ORDER BY question_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, examId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Question q = new Question();

                    q.setId(rs.getInt("question_id"));
                    q.setExamId(rs.getInt("exam_id"));
                    q.setQuestionText(rs.getString("question_text"));
                    q.setOptionA(rs.getString("option_a"));
                    q.setOptionB(rs.getString("option_b"));
                    q.setOptionC(rs.getString("option_c"));
                    q.setOptionD(rs.getString("option_d"));
                    q.setMarks(1);

                    questions.add(q);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return questions;
    }

    // Inserts every question in one transaction. Throws if anything fails.
    // row = {question, A, B, C, D, correctLetter}
    public void insertAll(int examId, List<String[]> rows) throws Exception {

        String sql =
                "INSERT INTO questions " +
                "(exam_id, question_text, option_a, option_b, " +
                "option_c, option_d, correct_answer) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                for (String[] r : rows) {
                    ps.setInt(1, examId);
                    for (int i = 0; i < 6; i++) {
                        ps.setString(i + 2, r[i]);
                    }
                    ps.addBatch();
                }

                ps.executeBatch();
                con.commit();

            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }
    }

    public int getTotalMarks(int examId) {

        String sql =
                "SELECT COUNT(*) FROM questions WHERE exam_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, examId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}