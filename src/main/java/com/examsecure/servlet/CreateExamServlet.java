package com.examsecure.servlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.examsecure.dao.ExamDAO;
import com.examsecure.dao.QuestionDAO;
import com.examsecure.model.Exam;
import com.examsecure.model.User;
import com.examsecure.util.FileStore;

@WebServlet("/admin/createExam")
@MultipartConfig(maxFileSize = 10 * 1024 * 1024)
public class CreateExamServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // One question:  1. text  A. ..  B. ..  C. ..  D. ..  [Answer: B]
    // Works whether options are on separate lines or on one line.
    private static final Pattern MCQ = Pattern.compile(
            "(?ms)^\\s*(?:Q\\s*)?\\d{1,3}\\s*[.)]\\s*(.+?)"
          + "\\s+\\(?A[.)]\\s*(.+?)"
          + "(?:\\s+|(?<=[a-z)]))\\(?B[.)]\\s*(.+?)"
          + "(?:\\s+|(?<=[a-z)]))\\(?C[.)]\\s*(.+?)"
          + "(?:\\s+|(?<=[a-z)]))\\(?D[.)]\\s*(.+?)"
          + "(?:\\s+(?:Correct\\s+)?(?:Answer|Ans)\\s*[:\\-]?\\s*\\(?([A-Da-d])\\)?[.]?)?"
          + "\\s*(?=^\\s*(?:Q\\s*)?\\d{1,3}\\s*[.)]\\s|\\z)");

    private ExamDAO examDAO;
    private QuestionDAO questionDAO;

    @Override
    public void init() {
        examDAO = new ExamDAO();
        questionDAO = new QuestionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/admin/createExam.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            String title = request.getParameter("title").trim();
            String description = request.getParameter("description").trim();
            int duration = Integer.parseInt(request.getParameter("duration"));
            int totalMarks = Integer.parseInt(request.getParameter("totalMarks"));
            int questionCount = Integer.parseInt(request.getParameter("questionCount"));

            LocalDateTime startTime = LocalDateTime.parse(
                    request.getParameter("examDate") + "T" + request.getParameter("examTime"));

            Part pdf = request.getPart("pdfFile");
            if (pdf == null || pdf.getSize() == 0) {
                throw new Exception("PDF is required.");
            }

            String fileName = new File(pdf.getSubmittedFileName()).getName();
            if (!fileName.toLowerCase().endsWith(".pdf")) {
                throw new Exception("Only PDF files are allowed.");
            }

            // 1. Save the PDF
            FileStore.getBaseFolder();
            File file = FileStore.get(fileName);
            Files.copy(pdf.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 2. Read the MCQs FIRST - so a bad PDF never leaves an empty exam behind
            String key = request.getParameter("answerKey");
            key = key == null ? "" : key.replaceAll("[^A-Da-d]", "").toUpperCase();
            List<String[]> mcqs = readQuestions(file, questionCount, key);

            // 3. Create the exam
            Exam exam = new Exam();
            exam.setTitle(title);
            exam.setDescription(description);
            exam.setDurationMinutes(duration);
            exam.setTotalMarks(totalMarks);
            exam.setCreatedBy(user.getUserId());
            exam.setOpensAt(startTime);
            exam.setClosesAt(startTime.plusMinutes(duration));
            exam.setPdfFile(fileName);

            int examId = examDAO.createExam(exam);
            if (examId <= 0) {
                throw new Exception("Could not create the examination.");
            }

            // 4. Insert all questions (one transaction; throws if the database refuses)
            questionDAO.insertAll(examId, mcqs);

            response.sendRedirect(request.getContextPath() + "/admin/dashboard");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/admin/createExam.jsp").forward(request, response);
        }
    }

    // Returns a list of {question, A, B, C, D, correctLetter}
    private List<String[]> readQuestions(File file, int wanted, String key) throws Exception {

        String text;
        try (PDDocument document = Loader.loadPDF(file)) {
            text = new PDFTextStripper().getText(document);
        }

        text = text.replace('\u00A0', ' ').replace("\r", "");

        // DEBUG: shows exactly what PDFBox read (see Tomcat console). Remove when done.
        System.out.println("===== PDF TEXT (" + text.length() + " chars) =====");
        System.out.println(text);
        System.out.println("===== END PDF TEXT =====");

        if (text.isBlank()) {
            throw new Exception("No text found in the PDF (is it a scanned image?).");
        }

        List<String[]> list = new ArrayList<>();
        Matcher m = MCQ.matcher(text);

        while (m.find() && list.size() < wanted) {

            // Correct answer: from the PDF's "Answer: X" line, else from the answer key box
            int n = list.size();
            String correct = m.group(6);
            if (correct == null && n < key.length()) {
                correct = key.substring(n, n + 1);
            }
            if (correct == null) {
                throw new Exception("Correct answers missing. Type the answer key (e.g. BBCBABBABA) "
                        + "with one letter per question.");
            }

            list.add(new String[] {
                    clean(m.group(1)), clean(m.group(2)), clean(m.group(3)),
                    clean(m.group(4)), clean(m.group(5)),
                    correct.toUpperCase()
            });
        }

        if (list.size() < wanted) {
            throw new Exception("Expected " + wanted + " questions but found only "
                    + list.size() + " in the PDF. Format: '1. Question', 'A. ..', 'B. ..', "
                    + "'C. ..', 'D. ..', 'Answer: B'.");
        }

        return list;
    }

    // Join wrapped lines into one line
    private String clean(String s) {
        return s.replaceAll("\\s+", " ").trim();
    }
}