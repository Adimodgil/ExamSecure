<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.examsecure.model.User" %>
<%@ page import="com.examsecure.model.Exam" %>

<%
    User user = (User) session.getAttribute("user");
    String name = user != null ? user.getUsername() : "Student";

    List<Exam> exams =
            (List<Exam>) request.getAttribute("exams");

    if (exams == null) {
        exams = new java.util.ArrayList<>();
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>ExamSecure - Student Dashboard</title>

    <link rel="stylesheet"
          href="<%= request.getContextPath() %>/css/style.css">
</head>

<body class="student-dashboard">

<!-- NAVBAR -->
<nav class="student-nav">

    <div class="student-brand">
        🔐 <span>ExamSecure</span>
    </div>

    <div class="student-user">
        <span>👨‍🎓 <%= name %></span>

        <a href="<%= request.getContextPath() %>/logout">
            Logout
        </a>
    </div>

</nav>


<main class="student-container">

    <!-- WELCOME -->
    <section class="student-welcome">

        <div>

            <p class="student-eyebrow">
                STUDENT PORTAL
            </p>

            <h1>
                Welcome, <%= name %> 👋
            </h1>

            <p>
                Choose an examination and test your knowledge.
            </p>

        </div>

    </section>


    <!-- AVAILABLE EXAMS -->
    <section class="student-exams">

        <div class="student-section-heading">

            <h2>
                Available Examinations
            </h2>

            <p>
                Select an exam to begin.
            </p>

        </div>


        <% if (exams.isEmpty()) { %>

            <div class="student-empty">

                <div>📝</div>

                <h3>
                    No exams available
                </h3>

                <p>
                    Your teacher has not published
                    an examination yet.
                </p>

            </div>

        <% } else { %>

            <div class="student-exam-grid">

                <% for (Exam exam : exams) { %>

                    <div class="student-exam-card">

                        <!-- EXAM NUMBER -->
                        <div class="student-exam-top">

                            <span class="available-badge">
                                AVAILABLE
                            </span>

                            <span class="exam-id">
                                #<%= exam.getId() %>
                            </span>

                        </div>


                        <!-- TITLE -->
                        <h3>
                            <%= exam.getTitle() %>
                        </h3>


                        <!-- DESCRIPTION -->
                        <p class="student-exam-description">

                            <%= exam.getDescription() != null
                                ? exam.getDescription()
                                : "Online examination" %>

                        </p>


                        <!-- DETAILS -->
                        <div class="student-exam-details">

                            <span>
                                📝
                                <%= exam.getQuestionCount() %>
                                Questions
                            </span>

                            <span>
                                ⏱️
                                <%= exam.getDurationMinutes() %>
                                Minutes
                            </span>

                            <span>
                                ⭐
                                <%= exam.getTotalMarks() %>
                                Marks
                            </span>

                        </div>


                        <!-- START -->
                        <a href="<%= request.getContextPath() %>/student/start?examId=<%= exam.getId() %>"
                           class="start-exam-button">

                            Start Examination

                            <span>→</span>

                        </a>

                    </div>

                <% } %>

            </div>

        <% } %>

    </section>

</main>

</body>
</html>