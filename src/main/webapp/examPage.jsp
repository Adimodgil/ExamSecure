<%@ page contentType="text/html; charset=UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="com.examsecure.model.Exam" %>
<%@ page import="com.examsecure.model.Question" %>

<%!
    private String esc(String s) {
        if (s == null) return "";

        return s
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
%>

<%
    Exam exam =
            (Exam) request.getAttribute("exam");

    List<Question> questions =
            (List<Question>) request.getAttribute("questions");

    Long remainingObj =
            (Long) request.getAttribute("remaining");

    long remaining =
            remainingObj != null ? remainingObj : 0;

    String base =
            request.getContextPath();

    if (exam == null) {
        response.sendRedirect(
                base + "/student/dashboard"
        );
        return;
    }
%>

<!DOCTYPE html>

<html>

<head>

    <meta charset="UTF-8">

    <title>
        ExamSecure - <%= esc(exam.getTitle()) %>
    </title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 0;
            background: #f4f3ff;
        }

        .bar {
            position: sticky;
            top: 0;
            z-index: 1000;

            background: linear-gradient(
                135deg,
                #6d28d9,
                #4f46e5
            );

            color: white;

            padding: 18px 30px;

            display: flex;
            justify-content: space-between;

            font-size: 18px;

            box-shadow: 0 4px 15px #0002;
        }

        .container {
            max-width: 900px;
            margin: 30px auto;
            padding: 0 20px;
        }

        .exam-info {
            background: white;
            padding: 25px;
            border-radius: 15px;
            margin-bottom: 20px;
            box-shadow: 0 5px 20px #0001;
        }

        .q {
            background: white;
            margin: 18px 0;
            padding: 25px;
            border-radius: 15px;
            box-shadow: 0 5px 20px #0001;
        }

        .question-text {
            font-size: 18px;
            font-weight: bold;
            margin-bottom: 18px;
        }

        .option {
            display: block;
            padding: 12px;
            margin: 8px 0;
            border: 1px solid #ddd;
            border-radius: 10px;
            cursor: pointer;
        }

        .option:hover {
            background: #f3f0ff;
        }

        .option input {
            margin-right: 10px;
        }

        button {
            width: 100%;
            padding: 15px;

            background: linear-gradient(
                135deg,
                #7c3aed,
                #4f46e5
            );

            color: white;
            border: 0;
            border-radius: 10px;

            font-size: 17px;
            font-weight: bold;

            cursor: pointer;
        }

        .submit-area {
            margin: 30px 0 50px;
        }

    </style>

</head>

<body>

<div class="bar">

    <strong>
        <%= esc(exam.getTitle()) %>
    </strong>

    <span>
        ⏱️ Time Left:
        <b id="timer">--:--</b>
    </span>

</div>


<div class="container">

    <div class="exam-info">

        <h2>
            <%= esc(exam.getTitle()) %>
        </h2>

        <p>
            📝 <%= questions.size() %> Questions
            &nbsp;&nbsp;
            ⭐ <%= exam.getTotalMarks() %> Marks
            &nbsp;&nbsp;
            ⏱️ <%= exam.getDurationMinutes() %> Minutes
        </p>

    </div>


    <form
            id="examForm"
            action="<%= base %>/student/submit"
            method="post">

        <input
                type="hidden"
                name="examId"
                value="<%= exam.getId() %>">

        <input
                type="hidden"
                name="auto"
                id="auto"
                value="0">


        <%
            int number = 1;

            if (questions != null) {

                for (Question q : questions) {
        %>

        <div class="q">

            <div class="question-text">

                Q<%= number %>.
                <%= esc(q.getQuestionText()) %>

            </div>


            <label class="option">

                <input
                        type="radio"
                        name="q<%= q.getId() %>"
                        value="A">

                A. <%= esc(q.getOptionA()) %>

            </label>


            <label class="option">

                <input
                        type="radio"
                        name="q<%= q.getId() %>"
                        value="B">

                B. <%= esc(q.getOptionB()) %>

            </label>


            <label class="option">

                <input
                        type="radio"
                        name="q<%= q.getId() %>"
                        value="C">

                C. <%= esc(q.getOptionC()) %>

            </label>


            <label class="option">

                <input
                        type="radio"
                        name="q<%= q.getId() %>"
                        value="D">

                D. <%= esc(q.getOptionD()) %>

            </label>

        </div>

        <%
                    number++;
                }
            }
        %>


        <div class="submit-area">

            <button type="submit">
                Submit Examination
            </button>

        </div>

    </form>

</div>


<script>

    let remaining = <%= remaining %>;

    const timer =
        document.getElementById("timer");

    const form =
        document.getElementById("examForm");

    const auto =
        document.getElementById("auto");

    let submitted = false;


    function tick() {

        if (remaining <= 0) {

            timer.textContent = "00:00";

            if (!submitted) {

                submitted = true;

                auto.value = "1";

                form.submit();
            }

            return;
        }


        const minutes =
            Math.floor(remaining / 60);

        const seconds =
            remaining % 60;


        timer.textContent =
            minutes +
            ":" +
            (seconds < 10 ? "0" : "") +
            seconds;


        remaining--;

        setTimeout(tick, 1000);
    }


    tick();

</script>

</body>

</html>