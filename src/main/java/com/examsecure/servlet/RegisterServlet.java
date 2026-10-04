package com.examsecure.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.examsecure.dao.UserDAO;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private UserDAO userDAO;


    @Override
    public void init() throws ServletException {

        userDAO = new UserDAO();

        System.out.println("RegisterServlet: init() called");
    }


    // =========================
    // GET
    // =========================
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/register.jsp")
               .forward(request, response);
    }


    // =========================
    // POST
    // =========================
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username =
                request.getParameter("username");

        String password =
                request.getParameter("password");

        String confirmPassword =
                request.getParameter("confirmPassword");


        // Remove unnecessary spaces
        if (username != null) {
            username = username.trim();
        }


        String error = null;


        // =========================
        // VALIDATION
        // =========================

        if (username == null || username.isEmpty()) {

            error = "Username is required.";

        } else if (!username.matches("[A-Za-z0-9_. ]{3,30}")) {

            error =
                    "Username must be 3-30 characters " +
                    "and can contain letters, numbers, spaces, dots or underscores.";

        } else if (password == null || password.length() < 6) {

            error =
                    "Password must be at least 6 characters.";

        } else if (confirmPassword == null ||
                   !password.equals(confirmPassword)) {

            error =
                    "Passwords do not match.";

        } else {

            // =========================
            // REGISTER USER
            // =========================

            error =
                    userDAO.register(username, password);
        }


        // =========================
        // REGISTRATION FAILED
        // =========================

        if (error != null) {

            request.setAttribute(
                    "error",
                    error
            );

            request.setAttribute(
                    "username",
                    username
            );

            request.getRequestDispatcher(
                    "/register.jsp"
            ).forward(request, response);

            return;
        }


        // =========================
        // REGISTRATION SUCCESS
        // =========================

        response.sendRedirect(
                request.getContextPath()
                + "/login?registered=1"
        );
    }


    @Override
    public void destroy() {

        System.out.println(
                "RegisterServlet: destroy() called"
        );
    }
}