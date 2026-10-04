package com.examsecure.servlet;

import java.io.IOException;
import java.time.LocalDateTime;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.examsecure.dao.ExamDAO;
import com.examsecure.model.Exam;
import com.examsecure.util.Params;

// One servlet for both teacher buttons: View Results and Manage Exams
@WebServlet({"/admin/results", "/admin/manage"})
public class ExamToolsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ExamDAO examDAO;

    @Override
    public void init() {
        examDAO = new ExamDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        show(request, response);
    }

    // Save changes (Manage Exams only)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        try {
            int examId = Params.toInt(request.getParameter("examId"));
            String title = request.getParameter("title").trim();
            String description = request.getParameter("description").trim();
            int duration = Integer.parseInt(request.getParameter("duration"));
            LocalDateTime start = LocalDateTime.parse(
                    request.getParameter("examDate") + "T" + request.getParameter("examTime"));

            if (title.isEmpty() || duration < 1 || duration > 600) {
                throw new Exception("Title is required and duration must be 1 to 600 minutes.");
            }
            if (!examDAO.updateExam(examId, title, description, start, duration)) {
                throw new Exception("Could not update the exam.");
            }

            response.sendRedirect(request.getContextPath() + "/admin/manage?saved=1");

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            show(request, response);
        }
    }

    private void show(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        boolean results = request.getServletPath().endsWith("results");
        int examId = Params.toInt(request.getParameter("examId"));
        Exam exam = examId > 0 ? examDAO.findById(examId) : null;

        request.setAttribute("mode", results ? "results" : "manage");
        request.setAttribute("exams", examDAO.getAllExams());
        request.setAttribute("exam", exam);

        if (exam != null && results) {
            request.setAttribute("results", examDAO.findResults(examId));
        }

        request.getRequestDispatcher("/admin/examTools.jsp").forward(request, response);
    }
}