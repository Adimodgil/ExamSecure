package com.examsecure.servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.examsecure.dao.ExamDAO;
import com.examsecure.model.Exam;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ExamDAO examDAO;

    @Override
    public void init() {
        examDAO = new ExamDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        List<Exam> exams = examDAO.getAllExams();

        request.setAttribute("exams", exams);
        request.setAttribute("totalExams", exams.size());

        request.getRequestDispatcher(
                "/admin/adminHome.jsp"
        ).forward(request, response);
    }
}