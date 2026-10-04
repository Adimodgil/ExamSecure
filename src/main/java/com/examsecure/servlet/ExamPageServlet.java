package com.examsecure.servlet;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

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

@WebServlet("/student/exam")
public class ExamPageServlet extends HttpServlet {

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

        if (session == null) {

            response.sendRedirect(
                    base + "/login"
            );

            return;
        }

        // Get logged-in user
        User user =
                (User) session.getAttribute("user");

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

        if (examId <= 0) {

            response.sendRedirect(
                    base + "/student/dashboard"
            );

            return;
        }

        // Find exam
        Exam exam =
                examDAO.findById(examId);

        if (exam == null) {

            response.sendRedirect(
                    base + "/student/dashboard"
            );

            return;
        }

        // Find student's attempt
        Attempt attempt =
                attemptDAO.find(
                        user.getId(),
                        examId
                );

        if (attempt == null) {

            response.sendRedirect(
                    base + "/student/dashboard"
            );

            return;
        }

        // If already submitted, show result
        if (!attempt.isInProgress()) {

            response.sendRedirect(
                    base
                    + "/student/result?examId="
                    + examId
            );

            return;
        }

        /*
         * Calculate deadline.
         *
         * start time + exam duration
         */

        LocalDateTime startedAt =
                attempt.getStartTime();

        LocalDateTime deadline =
                startedAt.plusMinutes(
                        exam.getDurationMinutes()
                );

        // Current server time
        LocalDateTime now =
                LocalDateTime.now();

        // Remaining seconds
        long remaining =
                Duration.between(
                        now,
                        deadline
                ).getSeconds();

        /*
         * If time has expired,
         * automatically submit the exam.
         */

        if (remaining <= 0) {

            response.setContentType(
                    "text/html;charset=UTF-8"
            );

            response.getWriter().println(
                    "<!DOCTYPE html>"
                    + "<html>"
                    + "<head>"
                    + "<title>Exam Expired</title>"
                    + "</head>"
                    + "<body>"
                    + "<p>Exam time has expired. Submitting...</p>"

                    + "<form id='expireForm' "
                    + "method='post' "
                    + "action='"
                    + base
                    + "/student/submit'>"

                    + "<input type='hidden' "
                    + "name='examId' "
                    + "value='"
                    + examId
                    + "'>"

                    + "<input type='hidden' "
                    + "name='auto' "
                    + "value='1'>"

                    + "</form>"

                    + "<script>"
                    + "document.getElementById('expireForm').submit();"
                    + "</script>"

                    + "</body>"
                    + "</html>"
            );

            return;
        }

        // Load questions
        request.setAttribute(
                "questions",
                questionDAO.getByExam(examId)
        );

        // Send exam
        request.setAttribute(
                "exam",
                exam
        );

        // Send attempt
        request.setAttribute(
                "attempt",
                attempt
        );

        // Send deadline
        request.setAttribute(
                "deadline",
                deadline
        );

        // Send remaining seconds
        request.setAttribute(
                "remaining",
                remaining
        );

        // Open exam page
        request.getRequestDispatcher(
                "/examPage.jsp"
        ).forward(
                request,
                response
        );
    }
}