package com.examsecure.servlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.examsecure.dao.AttemptDAO;
import com.examsecure.dao.ExamDAO;
import com.examsecure.model.Attempt;
import com.examsecure.model.Exam;
import com.examsecure.model.User;
import com.examsecure.util.FileStore;
import com.examsecure.util.Params;

@WebServlet("/student/paper")
public class PaperServlet extends HttpServlet {

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
            throws IOException {

        HttpSession session =
                request.getSession(false);

        if (session == null) {

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            return;
        }

        User user =
                (User) session.getAttribute("user");

        if (user == null) {

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            return;
        }

        int examId =
                Params.toInt(
                        request.getParameter("examId")
                );

        if (examId <= 0) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST
            );

            return;
        }

        Exam exam =
                examDAO.findById(examId);

        if (exam == null) {

            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND
            );

            return;
        }

        Attempt attempt =
                attemptDAO.find(
                        user.getId(),
                        examId
                );

        boolean allowed = false;

        if (attempt != null
                && attempt.isInProgress()
                && exam.getPdfFile() != null
                && !exam.getPdfFile().isBlank()
                && attempt.getStartTime() != null) {

            LocalDateTime deadline =
                    attempt.getStartTime()
                            .plusMinutes(
                                    exam.getDurationMinutes()
                            );

            allowed =
                    LocalDateTime.now()
                            .isBefore(deadline);
        }

        if (!allowed) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "No active exam attempt."
            );

            return;
        }

        File file =
                FileStore.get(
                        exam.getPdfFile()
                );

        if (!file.exists()
                || !file.isFile()) {

            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Exam paper not found."
            );

            return;
        }

        response.setContentType(
                "application/pdf"
        );

        response.setHeader(
                "Content-Disposition",
                "inline; filename=\"paper.pdf\""
        );

        response.setHeader(
                "X-Content-Type-Options",
                "nosniff"
        );

        response.setContentLengthLong(
                file.length()
        );

        Files.copy(
                file.toPath(),
                response.getOutputStream()
        );
    }
}