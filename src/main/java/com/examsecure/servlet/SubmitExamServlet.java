package com.examsecure.servlet;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
import com.examsecure.model.Question;
import com.examsecure.model.User;
import com.examsecure.util.Params;

@WebServlet("/student/submit")
public class SubmitExamServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final int GRACE_SECONDS = 10;

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
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        String base = request.getContextPath();

        // 1. Check login session
        HttpSession session =
                request.getSession(false);

        if (session == null) {

            response.sendRedirect(
                    base + "/login"
            );

            return;
        }

        User user =
                (User) session.getAttribute("user");

        if (user == null) {

            response.sendRedirect(
                    base + "/login"
            );

            return;
        }

        // 2. Get exam ID
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

        // 3. Find exam
        Exam exam =
                examDAO.findById(examId);

        if (exam == null) {

            response.sendRedirect(
                    base + "/student/dashboard"
            );

            return;
        }

        // 4. Find student's attempt
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

        // 5. Prevent double submission
        if (!attempt.isInProgress()) {

            response.sendRedirect(
                    base
                    + "/student/result?examId="
                    + examId
            );

            return;
        }

        // 6. SERVER-SIDE TIMER CHECK
        //
        // Attempt.startTime is already LocalDateTime.
        // Therefore DO NOT use .toLocalDateTime().

        LocalDateTime deadline =
                attempt.getStartTime()
                        .plusMinutes(
                                exam.getDurationMinutes()
                        );

        // 7. Check whether submission is late

        boolean late =
                LocalDateTime.now()
                        .isAfter(
                                deadline.plusSeconds(
                                        GRACE_SECONDS
                                )
                        );

        // 8. Check whether browser
        // automatically submitted the exam

        boolean auto =
                "1".equals(
                        request.getParameter("auto")
                );

        // 9. Collect answers

        Map<Integer, String> answers = null;

        if (!late) {

            answers = new HashMap<>();

            for (Question q :
                    questionDAO.getByExam(examId)) {

                String choice =
                        request.getParameter(
                                "q" + q.getId()
                        );

                // Only accept A, B, C or D

                if (choice != null
                        && choice.matches("[A-D]")) {

                    answers.put(
                            q.getId(),
                            choice
                    );
                }
            }
        }

        // 10. Determine status

        String status;

        if (late || auto) {

            status = "AUTO_SUBMITTED";

        } else {

            status = "SUBMITTED";
        }

        // 11. Finish attempt

        boolean finished =
                attemptDAO.finish(
                        attempt.getId(),
                        status,
                        answers
                );

        // 12. Redirect to result

        if (finished) {

            response.sendRedirect(
                    base
                    + "/student/result?examId="
                    + examId
                    + (late ? "&late=1" : "")
            );

        } else {

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to submit the exam."
            );
        }
    }
}