package com.examsecure.servlet;

import java.io.IOException;
import java.time.LocalDateTime;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.examsecure.dao.AttemptDAO;
import com.examsecure.dao.ExamDAO;
import com.examsecure.model.User;

@WebServlet("/student/dashboard")
public class StudentDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ExamDAO examDAO;
    private AttemptDAO attemptDAO;

    @Override
    public void init() {
        examDAO = new ExamDAO();
        attemptDAO = new AttemptDAO();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        request.setAttribute(
                "exams",
                examDAO.getAllExams()
        );

        request.setAttribute(
                "attempts",
                attemptDAO.findAllByUser(user.getId())
        );

        request.setAttribute(
                "now",
                LocalDateTime.now()
        );

        request.getRequestDispatcher(
                "/student/studentHome.jsp"
        ).forward(request, response);
    }
}