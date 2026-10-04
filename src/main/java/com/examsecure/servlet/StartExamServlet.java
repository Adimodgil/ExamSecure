package com.examsecure.servlet;

import java.io.IOException;
import java.time.LocalDateTime;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.examsecure.dao.AttemptDAO;
import com.examsecure.dao.ExamDAO;
import com.examsecure.model.Exam;
import com.examsecure.model.User;
import com.examsecure.util.Params;

@WebServlet("/student/start")
public class StartExamServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ExamDAO examDAO;
    private AttemptDAO attemptDAO;

    @Override
    public void init() {
        examDAO = new ExamDAO();
        attemptDAO = new AttemptDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String base = request.getContextPath();

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(base + "/login");
            return;
        }

        int examId = Params.toInt(request.getParameter("examId"));
        Exam exam = examDAO.findById(examId);

        if (exam == null) {
            response.sendRedirect(base + "/student/dashboard");
            return;
        }

        if (exam.getQuestionCount() == 0) {
            response.sendError(HttpServletResponse.SC_CONFLICT,
                    "This exam has no questions.");
            return;
        }

        // One attempt per exam: create it only the first time.
        // Timer start = server time now, stored in attempts.start_time.
        if (!attemptDAO.hasAttempted(user.getId(), examId)) {
            if (!attemptDAO.start(user.getId(), examId, LocalDateTime.now())) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Could not start the exam.");
                return;
            }
        }

        // ExamPageServlet shows the questions, or the result if already submitted
        response.sendRedirect(base + "/student/exam?examId=" + examId);
    }
}