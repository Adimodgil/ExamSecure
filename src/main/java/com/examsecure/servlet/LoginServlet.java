package com.examsecure.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.examsecure.dao.UserDAO;
import com.examsecure.model.User;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/login.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        User user = null;

        if (username != null && password != null) {
            user = userDAO.login(username.trim(), password);
        }

        boolean correctRole =
            user != null &&
            (("STUDENT".equals(role) &&
              "STUDENT".equalsIgnoreCase(user.getRole()))
            ||
             ("TEACHER".equals(role) &&
              "ADMIN".equalsIgnoreCase(user.getRole())));
        
        if (!correctRole) {

            request.setAttribute(
                "error",
                "Invalid username, password or account type."
            );

            request.getRequestDispatcher("/login.jsp")
                   .forward(request, response);

            return;
        }

        HttpSession oldSession =
            request.getSession(false);

        if (oldSession != null) {
            oldSession.invalidate();
        }

        HttpSession session =
            request.getSession(true);

        session.setAttribute("user", user);

        session.setMaxInactiveInterval(30 * 60);

        String base = request.getContextPath();

        if ("TEACHER".equals(role)) {

            response.sendRedirect(
                base + "/admin/dashboard"
            );

        } else {

            response.sendRedirect(
                base + "/student/dashboard"
            );
        }
    }
}