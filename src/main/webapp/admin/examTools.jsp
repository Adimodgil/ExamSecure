<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.examsecure.model.Exam" %>
<%!
    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<%
    String base = request.getContextPath();
    boolean results = "results".equals(request.getAttribute("mode"));
    Exam exam = (Exam) request.getAttribute("exam");
    List<Exam> exams = (List<Exam>) request.getAttribute("exams");
    List<String[]> rows = (List<String[]>) request.getAttribute("results");
    String error = (String) request.getAttribute("error");
    String page_ = results ? "results" : "manage";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ExamSecure - <%= results ? "Results" : "Manage Exams" %></title>
    <link rel="stylesheet" href="<%= base %>/css/style.css">
    <style>
        .box { background:#fff; border-radius:16px; padding:24px; box-shadow:0 5px 20px #0001; margin-top:20px; overflow-x:auto; }
        table { width:100%; border-collapse:collapse; }
        th, td { padding:12px 14px; text-align:left; border-bottom:1px solid #eee; }
        th { background:#f3f0ff; color:#4f46e5; }
        a.link { color:#4f46e5; font-weight:600; }
        label { display:block; margin:14px 0 6px; font-weight:600; }
        input, textarea { width:100%; padding:12px; border:1px solid #ddd; border-radius:10px; font-size:15px; font-family:inherit; }
        .row { display:flex; gap:14px; flex-wrap:wrap; }
        .row > div { flex:1; min-width:150px; }
        button { margin-top:20px; padding:14px 28px; border:0; border-radius:10px; color:#fff; font-size:16px; font-weight:bold; cursor:pointer; background:linear-gradient(135deg,#7c3aed,#4f46e5); }
        .ok { background:#dcfce7; color:#166534; padding:12px 16px; border-radius:10px; margin-top:16px; }
        .err { background:#fee2e2; color:#991b1b; padding:12px 16px; border-radius:10px; margin-top:16px; }
    </style>
</head>
<body class="teacher-dashboard">

<nav class="dashboard-nav">
    <div class="nav-brand">🔐 <span>ExamSecure</span></div>
    <div class="nav-user"><a href="<%= base %>/logout">Logout</a></div>
</nav>

<main class="teacher-container">

    <a class="link" href="<%= base %>/admin/dashboard">← Back to Dashboard</a>

    <h1><%= results ? "📊 Student Results" : "📚 Manage Exams" %></h1>

    <% if ("1".equals(request.getParameter("saved"))) { %><div class="ok">✅ Exam updated.</div><% } %>
    <% if (error != null) { %><div class="err">⚠️ <%= esc(error) %></div><% } %>

    <% if (exam != null && results) { %>

        <!-- RESULTS OF ONE EXAM -->
        <div class="box">
            <h2><%= esc(exam.getTitle()) %></h2>
            <% if (rows == null || rows.isEmpty()) { %>
                <p>No student has taken this exam yet.</p>
            <% } else { %>
                <table>
                    <tr><th>#</th><th>Student</th><th>Score</th><th>Status</th><th>Started</th><th>Submitted</th></tr>
                    <% int i = 1; for (String[] r : rows) { %>
                    <tr>
                        <td><%= i++ %></td>
                        <td><%= esc(r[0]) %></td>
                        <td><b><%= r[1] %> / <%= exam.getQuestionCount() %></b></td>
                        <td><%= esc(r[2]) %></td>
                        <td><%= esc(r[3]) %></td>
                        <td><%= esc(r[4]) %></td>
                    </tr>
                    <% } %>
                </table>
            <% } %>
        </div>

    <% } else if (exam != null) { %>

        <!-- EDIT FORM -->
        <div class="box">
            <h2>Edit: <%= esc(exam.getTitle()) %></h2>
            <form action="<%= base %>/admin/manage" method="post">
                <input type="hidden" name="examId" value="<%= exam.getId() %>">

                <label>Exam Title</label>
                <input type="text" name="title" value="<%= esc(exam.getTitle()) %>" required>

                <label>Description</label>
                <textarea name="description" rows="3"><%= esc(exam.getDescription()) %></textarea>

                <div class="row">
                    <div><label>Date</label>
                        <input type="date" name="examDate" required
                               value="<%= exam.getOpensAt() != null ? exam.getOpensAt().toLocalDate() : "" %>"></div>
                    <div><label>Time</label>
                        <input type="time" name="examTime" required
                               value="<%= exam.getOpensAt() != null ? exam.getOpensAt().toLocalTime().withSecond(0).withNano(0) : "" %>"></div>
                    <div><label>Duration (minutes)</label>
                        <input type="number" name="duration" min="1" max="600" required
                               value="<%= exam.getDurationMinutes() %>"></div>
                </div>

                <button type="submit">Save Changes</button>
            </form>
        </div>

    <% } %>

    <!-- LIST OF EXAMS -->
    <div class="box">
        <% if (exams == null || exams.isEmpty()) { %>
            <p>No exams created yet.</p>
        <% } else { %>
            <table>
                <tr><th>#</th><th>Title</th><th>Date &amp; Time</th><th>Duration</th><th>Questions</th><th></th></tr>
                <% for (Exam e : exams) { %>
                <tr>
                    <td><%= e.getId() %></td>
                    <td><%= esc(e.getTitle()) %></td>
                    <td><%= e.getOpensAt() != null ? e.getOpensAt().toString().replace('T', ' ') : "-" %></td>
                    <td><%= e.getDurationMinutes() %> min</td>
                    <td><%= e.getQuestionCount() %></td>
                    <td><a class="link" href="<%= base %>/admin/<%= page_ %>?examId=<%= e.getId() %>">
                        <%= results ? "View Results →" : "Edit ✏️" %></a></td>
                </tr>
                <% } %>
            </table>
        <% } %>
    </div>

</main>
</body>
</html>