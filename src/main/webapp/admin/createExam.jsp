<%@ page contentType="text/html; charset=UTF-8" %>

<%
    String error = (String) request.getAttribute("error");
    String base = request.getContextPath();
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>ExamSecure - Create Exam</title>

    <link rel="stylesheet"
          href="<%= base %>/css/style.css">
</head>

<body class="create-exam-page">

    <!-- NAVBAR -->
    <nav class="student-nav">

        <div class="student-brand">
            🔐 <span>ExamSecure</span>
        </div>

        <div class="student-user">
            <a href="<%= base %>/admin/dashboard">
                ← Dashboard
            </a>

            <a href="<%= base %>/logout">
                Logout
            </a>
        </div>

    </nav>


    <main class="create-exam-container">

        <!-- HEADER -->
        <div class="create-exam-header">

            <div>
                <p class="student-eyebrow">
                    TEACHER PORTAL
                </p>

                <h1>Create New Examination</h1>

                <p>
                    Add exam details and upload your MCQ question paper.
                </p>
            </div>

            <div class="create-exam-icon">
                📝
            </div>

        </div>


        <% if (error != null) { %>

            <div class="register-error">
                ⚠️ <%= error %>
            </div>

        <% } %>


        <!-- FORM -->
        <form action="<%= base %>/admin/createExam"
              method="post"
              enctype="multipart/form-data"
              class="create-exam-form">


            <!-- EXAM DETAILS -->
            <div class="form-section">

                <div class="form-section-title">

                    <span>01</span>

                    <div>
                        <h2>Exam Details</h2>
                        <p>Basic information about your examination.</p>
                    </div>

                </div>


                <div class="form-group">

                    <label for="title">
                        Exam Name
                    </label>

                    <input type="text"
                           id="title"
                           name="title"
                           placeholder="e.g. DAA Mid Term Test"
                           maxlength="150"
                           required>

                </div>


                <div class="form-group">

                    <label for="description">
                        Description
                    </label>

                    <textarea id="description"
                              name="description"
                              rows="3"
                              placeholder="Brief description of the examination"
                              maxlength="500"
                              required></textarea>

                </div>

            </div>


            <!-- EXAM SETTINGS -->
            <div class="form-section">

                <div class="form-section-title">

                    <span>02</span>

                    <div>
                        <h2>Exam Settings</h2>
                        <p>Set the examination requirements.</p>
                    </div>

                </div>


                <div class="form-row">

                    <div class="form-group">

                        <label for="examDate">
                            Date
                        </label>

                        <input type="date"
                               id="examDate"
                               name="examDate"
                               required>

                    </div>


                    <div class="form-group">

                        <label for="examTime">
                            Time
                        </label>

                        <input type="time"
                               id="examTime"
                               name="examTime"
                               required>

                    </div>

                </div>


                <div class="form-row">

                    <div class="form-group">

                        <label for="duration">
                            Duration
                        </label>

                        <div class="input-with-unit">

                            <input type="number"
                                   id="duration"
                                   name="duration"
                                   placeholder="30"
                                   min="1"
                                   max="600"
                                   required>

                            <span>minutes</span>

                        </div>

                    </div>


                    <div class="form-group">

                        <label for="totalMarks">
                            Total Marks
                        </label>

                        <div class="input-with-unit">

                            <input type="number"
                                   id="totalMarks"
                                   name="totalMarks"
                                   placeholder="20"
                                   min="1"
                                   max="1000"
                                   required>

                            <span>marks</span>

                        </div>

                    </div>

                </div>


                <div class="form-group">

                    <label for="questionCount">
                        Number of Questions
                    </label>

                    <input type="number"
                           id="questionCount"
                           name="questionCount"
                           placeholder="10"
                           min="1"
                           max="200"
                           required>

                </div>
                
                    <div class="form-group">

                    <label for="answerKey">
                        Answer Key (only if the PDF has no "Answer:" lines)
                    </label>

                    <input type="text"
                           id="answerKey"
                           name="answerKey"
                           placeholder="BBCBABBABA">

                </div>

            </div>


            <!-- PDF UPLOAD -->
            <div class="form-section">

                <div class="form-section-title">

                    <span>03</span>

                    <div>
                        <h2>Question Paper</h2>
                        <p>Upload the MCQ question paper as a PDF.</p>
                    </div>

                </div>


                <label for="pdfFile"
                       class="pdf-upload">

                    <div class="pdf-icon">
                        📄
                    </div>

                    <strong>
                        Drag & Drop your PDF here
                    </strong>

                    <span>
                        or click to browse
                    </span>

                    <small>
                        PDF only • Maximum 10 MB
                    </small>

                    <input type="file"
                           id="pdfFile"
                           name="pdfFile"
                           accept=".pdf,application/pdf"
                           required>

                </label>

                <p id="fileName"
                   class="file-name">
                </p>

            </div>


            <!-- BUTTONS -->
            <div class="create-exam-actions">

                <a href="<%= base %>/admin/dashboard"
                   class="cancel-button">
                    Cancel
                </a>

                <button type="submit"
                        class="create-button">
                    Create Examination →
                </button>

            </div>

        </form>

    </main>


    <script>

        const fileInput =
            document.getElementById("pdfFile");

        const fileName =
            document.getElementById("fileName");

        fileInput.addEventListener("change", function () {

            if (this.files.length > 0) {

                const file = this.files[0];

                if (file.type !== "application/pdf") {

                    alert("Please select a PDF file.");

                    this.value = "";
                    fileName.textContent = "";

                    return;
                }

                if (file.size > 10 * 1024 * 1024) {

                    alert("PDF must be smaller than 10 MB.");

                    this.value = "";
                    fileName.textContent = "";

                    return;
                }

                fileName.textContent =
                    "✓ " + file.name;

            }

        });

    </script>

</body>
</html>