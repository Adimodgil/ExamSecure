package com.examsecure.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.examsecure.dao.UserDAO;

@WebServlet("/teacher-register")
public class TeacherRegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/teacherRegister.jsp"
        ).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword =
                request.getParameter("confirmPassword");

        String error = null;

        if (username != null) {
            username = username.trim();
        }

        if (username == null || username.isEmpty()) {

            error = "Username is required.";

        } else if (!username.matches("[A-Za-z0-9_. ]{3,30}")) {

            error = "Username must be 3-30 characters.";

        } else if (password == null || password.length() < 6) {

            error = "Password must be at least 6 characters.";

        } else if (!password.equals(confirmPassword)) {

            error = "Passwords do not match.";

        } else {

            error = userDAO.registerTeacher(
                    username,
                    password
            );
        }

        if (error != null) {

            request.setAttribute("error", error);
            request.setAttribute("username", username);

            request.getRequestDispatcher(
                    "/teacherRegister.jsp"
            ).forward(request, response);

            return;
        }

        response.sendRedirect(
                request.getContextPath()
                + "/login?teacherRegistered=1"
        );
    }
}