<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.examsecure.model.User" %>
<%@ page import="com.examsecure.model.Exam" %>

<%
    User user = (User) session.getAttribute("user");
    String name = user != null ? user.getUsername() : "Teacher";

    List<Exam> exams = (List<Exam>) request.getAttribute("exams");
    int totalExams = exams != null ? exams.size() : 0;
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ExamSecure - Teacher Dashboard</title>

    <link rel="stylesheet"
          href="<%= request.getContextPath() %>/css/style.css">
</head>

<body class="teacher-dashboard">

    <nav class="dashboard-nav">
        <div class="nav-brand">
            🔐 <span>ExamSecure</span>
        </div>

        <div class="nav-user">
            <span>👨‍🏫 <%= name %></span>
            <a href="<%= request.getContextPath() %>/logout">
                Logout
            </a>
        </div>
    </nav>

    <main class="teacher-container">

        <!-- Welcome -->
        <section class="dashboard-welcome">
            <div>
                <p class="eyebrow">TEACHER PORTAL</p>

                <h1>Welcome back, <%= name %> 👋</h1>

                <p>
                    Create and manage your examinations.
                </p>
            </div>
        </section>


        <!-- Exam Count -->
        <section class="dashboard-stats">

            <div class="stat-card">
                <div class="stat-icon purple">📝</div>

                <div>
                    <strong><%= totalExams %></strong>
                    <span>Total Exams</span>
                </div>
            </div>

        </section>


        <!-- Quick Actions -->
        <section>

            <div class="section-heading">
                <div>
                    <h2>Quick Actions</h2>
                    <p>Manage your examination system</p>
                </div>
            </div>

            <div class="action-grid">

                <a href="<%= request.getContextPath() %>/admin/createExam"
                   class="action-card primary-action">

                    <div class="action-icon">➕</div>

                    <div>
                        <h3>Create New Exam</h3>
                        <p>Create an exam and add questions.</p>
                    </div>

                    <span class="arrow">→</span>
                </a>


                <a href="<%= request.getContextPath() %>/admin/results"
                   class="action-card">

                    <div class="action-icon">📊</div>

                    <div>
                        <h3>View Results</h3>
                        <p>Check student examination results.</p>
                    </div>

                    <span class="arrow">→</span>
                </a>


                <a href="<%= request.getContextPath() %>/admin/manage"
                   class="action-card">

                    <div class="action-icon">📚</div>

                    <div>
                        <h3>Manage Exams</h3>
                        <p>Change exam name, time and duration.</p>
                    </div>

                    <span class="arrow">→</span>
                </a>

            </div>

        </section>

    </main>

</body>
</html>