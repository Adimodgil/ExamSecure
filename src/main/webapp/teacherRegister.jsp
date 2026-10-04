<%@ page contentType="text/html; charset=UTF-8" %>

<%
    String error = (String) request.getAttribute("error");
    String username = (String) request.getAttribute("username");
    String base = request.getContextPath();
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>ExamSecure - Teacher Registration</title>

    <link rel="stylesheet"
          href="<%= base %>/css/style.css">

</head>

<body class="register-page">

    <div class="register-wrapper">

        <!-- BRAND -->

        <div class="register-brand">

            <div class="brand-icon">
                🔐
            </div>

            <div class="brand-name">
                Exam<span>Secure</span>
            </div>

            <p>
                Secure Online Examination Portal
            </p>

        </div>


        <!-- REGISTER CARD -->

        <div class="register-card">

            <div class="register-header">

                <span class="register-tag">
                    TEACHER REGISTRATION
                </span>

                <h1>
                    Create your teacher account
                </h1>

                <p>
                    Create and manage secure online
                    examinations for your students.
                </p>

            </div>


            <% if (error != null) { %>

                <div class="register-error">
                    ⚠️ <%= error %>
                </div>

            <% } %>


            <form action="<%= base %>/teacher-register"
                  method="post"
                  class="register-form">

                <div class="form-group">

                    <label for="username">
                        Teacher Username
                    </label>

                    <input
                        type="text"
                        id="username"
                        name="username"
                        value="<%= username != null ? username : "" %>"
                        placeholder="Choose your teacher username"
                        required
                        autocomplete="username">

                </div>


                <div class="form-group">

                    <label for="password">
                        Password
                    </label>

                    <input
                        type="password"
                        id="password"
                        name="password"
                        placeholder="Minimum 6 characters"
                        required
                        autocomplete="new-password">

                </div>


                <div class="form-group">

                    <label for="confirmPassword">
                        Confirm Password
                    </label>

                    <input
                        type="password"
                        id="confirmPassword"
                        name="confirmPassword"
                        placeholder="Enter password again"
                        required
                        autocomplete="new-password">

                </div>


                <button type="submit"
                        class="register-button">

                    Create Teacher Account →

                </button>

            </form>


            <!-- FOOTER LINKS -->

            <div class="register-footer">

                <div>

                    Already have an account?

                    <a href="<%= base %>/login">
                        Login here
                    </a>

                </div>

                <br>

                <div>

                    Are you a student?

                    <a href="<%= base %>/register">
                        Student Registration
                    </a>

                </div>

            </div>


            <!-- SECURITY -->

            <div class="secure-note">

                🛡️

                <span>
                    Your password is securely hashed
                    before being stored.
                </span>

            </div>

        </div>


        <!-- BOTTOM -->

        <div class="register-bottom">

            ExamSecure · Teacher Portal

        </div>

    </div>


    <script>

        document.querySelector(".register-form")
            .addEventListener("submit", function(event) {

                const password =
                    document.getElementById("password").value;

                const confirmPassword =
                    document.getElementById("confirmPassword").value;

                if (password !== confirmPassword) {

                    event.preventDefault();

                    alert("Passwords do not match.");

                }

            });

    </script>

</body>

</html>