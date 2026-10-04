package com.examsecure.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.examsecure.dao.AttemptDAO;
import com.examsecure.dao.ExamDAO;
import com.examsecure.dao.QuestionDAO;
import com.examsecure.model.Attempt;
import com.examsecure.model.Exam;
import com.examsecure.model.User;
import com.examsecure.util.Params;

@WebServlet("/student/result")
public class ResultServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ExamDAO examDAO;
    private AttemptDAO attemptDAO;
    private QuestionDAO questionDAO;

    @Override
    public void init() {
        examDAO = new ExamDAO();
        attemptDAO = new AttemptDAO();
        questionDAO = new QuestionDAO();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String base = request.getContextPath();

        // Get existing session
        HttpSession session =
                request.getSession(false);

        // No session = not logged in
        if (session == null) {
            response.sendRedirect(
                    base + "/login"
            );
            return;
        }

        // Get logged-in user
        User user =
                (User) session.getAttribute("user");

        // No user in session
        if (user == null) {
            response.sendRedirect(
                    base + "/login"
            );
            return;
        }

        // Get exam ID
        int examId =
                Params.toInt(
                        request.getParameter("examId")
                );

        // Invalid exam ID
        if (examId <= 0) {
            response.sendRedirect(
                    base + "/student/dashboard"
            );
            return;
        }

        // Find exam
        Exam exam =
                examDAO.findById(examId);

        // Find the attempt of the logged-in student
        Attempt attempt =
                (exam == null)
                ? null
                : attemptDAO.find(
                        user.getId(),
                        examId
                );

        // Exam or attempt doesn't exist
        if (exam == null || attempt == null) {
            response.sendRedirect(
                    base + "/student/dashboard"
            );
            return;
        }

        // Student cannot view result
        // while the exam is still running
        if (attempt.isInProgress()) {
            response.sendRedirect(
                    base + "/student/exam?examId="
                    + examId
            );
            return;
        }

        // Calculate total marks from questions
        int total =
                questionDAO.getTotalMarks(examId);

        // Send data to result.jsp
        request.setAttribute(
                "exam",
                exam
        );

        request.setAttribute(
                "attempt",
                attempt
        );

        request.setAttribute(
                "total",
                total
        );

        request.setAttribute(
                "late",
                "1".equals(
                        request.getParameter("late")
                )
        );

        // Open result page
        request.getRequestDispatcher(
                "/student/result.jsp"
        ).forward(request, response);
    }
}