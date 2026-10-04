package com.examsecure.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.examsecure.util.DBConnection;

@WebServlet("/dbtest")
public class DBTestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");
        out.println("<h1>ExamSecure Database Test</h1>");

        String sql = "SELECT username, role FROM users";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {

            out.println("<h2 style='color:green;'>DB connection OK!</h2>");

            out.println("<h3>Users:</h3>");
            out.println("<ul>");

            while (resultSet.next()) {

                String username = resultSet.getString("username");
                String role = resultSet.getString("role");

                out.println("<li>" + username + " | " + role + "</li>");
            }

            out.println("</ul>");

        } catch (Exception e) {

            out.println("<h2 style='color:red;'>Database connection failed!</h2>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }

        out.println("</body></html>");
    }
}