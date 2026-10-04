<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.examsecure.model.Exam" %>
<%@ page import="com.examsecure.model.Attempt" %>
<%
    Exam exam = (Exam) request.getAttribute("exam");
    Attempt attempt = (Attempt) request.getAttribute("attempt");
    Integer total = (Integer) request.getAttribute("total");
    boolean late = Boolean.TRUE.equals(request.getAttribute("late"));
    String base = request.getContextPath();

    if (exam == null || attempt == null) {
        response.sendRedirect(base + "/student/dashboard");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ExamSecure - Result</title>
    <link rel="stylesheet" href="<%= base %>/css/style.css">
</head>
<body class="student-dashboard">

<nav class="student-nav">
    <div class="student-brand">🔐 <span>ExamSecure</span></div>
    <div class="student-user"><a href="<%= base %>/logout">Logout</a></div>
</nav>

<main class="student-container">
    <section class="student-exam-card" style="max-width:560px;margin:40px auto;text-align:center;">

        <p class="student-eyebrow">RESULT</p>
        <h2><%= exam.getTitle().replace("<", "&lt;") %></h2>

        <h1 style="font-size:56px;margin:20px 0;">
            <%= attempt.getScore() %> / <%= total %>
        </h1>

        <p>Status: <b><%= attempt.getStatus() %></b></p>

        <% if (late) { %>
            <p>⚠️ Time was over, so your answers were not counted.</p>
        <% } %>

        <a href="<%= base %>/student/dashboard" class="start-exam-button">
            Back to Dashboard <span>→</span>
        </a>
    </section>
</main>

</body>
</html>