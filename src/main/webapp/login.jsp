<%@ page contentType="text/html; charset=UTF-8" %>

<%
    String error = (String) request.getAttribute("error");
    boolean registered = "1".equals(request.getParameter("registered"));
    String base = request.getContextPath();
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>ExamSecure - Login</title>

    <link rel="stylesheet"
          href="<%= base %>/css/style.css">

</head>

<body class="login-page">

    <div class="login-background">
        <div class="glow glow-one"></div>
        <div class="glow glow-two"></div>
        <div class="glow glow-three"></div>
    </div>

    <main class="login-container">

        <!-- BRAND -->
        <div class="brand">

            <div class="brand-icon">
                🔐
            </div>

            <h1>Exam<span>Secure</span></h1>

            <p>
                Secure Online Examination Platform
            </p>

        </div>


        <!-- LOGIN CARD -->
        <div class="login-card">

            <div class="login-heading">

                <h2>Welcome Back</h2>

                <p>
                    Choose your account type to continue
                </p>

            </div>


            <!-- ROLE SELECTION -->
            <div class="role-selection">

                <button type="button"
                        class="role-card active"
                        id="studentRole"
                        onclick="selectRole('STUDENT')">

                    <div class="role-icon">
                        👨‍🎓
                    </div>

                    <div>
                        <strong>Student</strong>
                        <small>Attend examinations</small>
                    </div>

                </button>


                <button type="button"
                        class="role-card"
                        id="teacherRole"
                        onclick="selectRole('TEACHER')">

                    <div class="role-icon">
                        👨‍🏫
                    </div>

                    <div>
                        <strong>Teacher</strong>
                        <small>Create & manage exams</small>
                    </div>

                </button>

            </div>


            <!-- SELECTED ROLE -->
            <div class="selected-role" id="selectedRole">
                👨‍🎓 Student Login
            </div>


            <% if (registered) { %>

                <div class="message success-message">
                    Account created successfully.
                    Please login.
                </div>

            <% } %>


            <% if (error != null) { %>

                <div class="message error-message">
                    <%= error %>
                </div>

            <% } %>


            <!-- LOGIN FORM -->
            <form action="<%= base %>/login"
                  method="post"
                  class="login-form">

                <input type="hidden"
                       name="role"
                       id="role"
                       value="STUDENT">


                <div class="input-group">

                    <label for="username">
                        Username
                    </label>

                    <input type="text"
                           id="username"
                           name="username"
                           placeholder="Enter your username"
                           required
                           autocomplete="username">

                </div>


                <div class="input-group">

                    <label for="password">
                        Password
                    </label>

                    <input type="password"
                           id="password"
                           name="password"
                           placeholder="Enter your password"
                           required
                           autocomplete="current-password">

                </div>


                <button type="submit"
                        class="login-button"
                        id="loginButton">

                    Login as Student

                </button>

            </form>


            <!-- STUDENT REGISTRATION -->
          <div class="register-link">

            <span>New student?</span>
                <a href="<%= base %>/register">
                           Student Registration
                 </a>
    
                <br>

                <span>Are you a teacher?</span>
			    <a href="<%= base %>/teacher-register">
			        Teacher Registration
			    </a>

</div>


            <div class="security-note">
                🛡️ Secure authentication · Protected sessions
            </div>

        </div>

        <p class="copyright">
            © 2026 ExamSecure · Secure Examination System
        </p>

    </main>


    <script>

        function selectRole(role) {

            const student =
                document.getElementById("studentRole");

            const teacher =
                document.getElementById("teacherRole");

            const roleInput =
                document.getElementById("role");

            const selected =
                document.getElementById("selectedRole");

            const loginButton =
                document.getElementById("loginButton");


            student.classList.remove("active");
            teacher.classList.remove("active");


            if (role === "STUDENT") {

                student.classList.add("active");

                roleInput.value = "STUDENT";

                selected.innerHTML =
                    "👨‍🎓 Student Login";

                loginButton.innerHTML =
                    "Login as Student";

            } else {

                teacher.classList.add("active");

                roleInput.value = "TEACHER";

                selected.innerHTML =
                    "👨‍🏫 Teacher Login";

                loginButton.innerHTML =
                    "Login as Teacher";
            }
        }

    </script>

</body>

</html>