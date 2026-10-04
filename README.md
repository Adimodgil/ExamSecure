# 🔐 ExamSecure

### Secure Online MCQ Examination System

**ExamSecure** is a secure online examination platform built with **Java Servlets, JSP, MySQL, and Apache PDFBox**.

Teachers can upload MCQ question papers as PDFs, create and schedule exams, while students can take timed examinations and receive their results instantly.

---

## ✨ Features

### 👨‍🏫 Teacher / Admin

* Create and manage examinations
* Upload MCQ question papers in PDF format
* Automatically extract questions and options from PDFs
* Schedule exams with start time and duration
* View student attempts and results
* Manage exam details

### 👨‍🎓 Student

* Student registration and login
* View available examinations
* Take timed MCQ examinations
* Live countdown timer
* Submit answers securely
* Instant result and score
* One controlled attempt per examination

### 🔒 Security

* Passwords stored using salted SHA-256 hashing
* Role-based authentication
* `AuthFilter` protects student and teacher areas
* Prepared statements for database operations
* Session-based authentication
* Protected pages are not cached
* Path-traversal protection for uploaded files
* Server-side exam timing validation

---

## 🛠️ Tech Stack

| Layer           | Technology                    |
| --------------- | ----------------------------- |
| Backend         | Java 21                       |
| Web             | JSP, HTML, CSS, JavaScript    |
| Server          | Apache Tomcat 10.1+           |
| Database        | MySQL 8                       |
| Database Access | JDBC                          |
| PDF Processing  | Apache PDFBox 3               |
| Build Tool      | Maven                         |
| Architecture    | MVC-style Servlet Application |

---

## 📂 Project Structure

```text
ExamSecure/
│
├── src/
│   └── main/
│       │
│       ├── java/
│       │   └── com/
│       │       └── examsecure/
│       │           │
│       │           ├── dao/
│       │           │   ├── ExamDAO.java
│       │           │   ├── QuestionDAO.java
│       │           │   ├── AttemptDAO.java
│       │           │   └── UserDAO.java
│       │           │
│       │           ├── filter/
│       │           │   └── AuthFilter.java
│       │           │
│       │           ├── model/
│       │           │   ├── Exam.java
│       │           │   ├── Question.java
│       │           │   ├── Attempt.java
│       │           │   └── User.java
│       │           │
│       │           ├── servlet/
│       │           │   ├── LoginServlet.java
│       │           │   ├── CreateExamServlet.java
│       │           │   ├── StartExamServlet.java
│       │           │   ├── SubmitExamServlet.java
│       │           │   └── ResultServlet.java
│       │           │
│       │           └── util/
│       │               ├── DBConnection.java
│       │               ├── PasswordUtil.java
│       │               └── FileStore.java
│       │
│       ├── resources/
│       │   ├── db.properties.example
│       │   └── db.properties       ← ignored by Git
│       │
│       └── webapp/
│           ├── admin/
│           ├── student/
│           ├── css/
│           ├── login.jsp
│           └── index.jsp
│
├── schema.sql
├── pom.xml
├── README.md
└── .gitignore
```

---

## 🔄 How ExamSecure Works

```text
Teacher
   │
   ├── Login
   │
   ├── Upload MCQ PDF
   │
   ├── PDFBox extracts questions
   │
   ├── Create / Schedule Exam
   │
   └── Monitor Results
          │
          ▼
       MySQL Database
          ▲
          │
   ┌──────┴──────┐
   │             │
Student       Exam System
   │             │
   ├── Login     │
   ├── View Exam │
   ├── Start     │
   ├── Timer     │
   ├── Submit ───┘
   │
   └── Instant Result
```

---

## 📄 PDF Question Format

ExamSecure can extract MCQs from a structured PDF.

Example:

```text
1. What is the capital of India?

A. Mumbai
B. New Delhi
C. Kolkata
D. Chennai

Answer: B
```

The system extracts:

```text
Question
   ↓
Option A
Option B
Option C
Option D
   ↓
Correct Answer
```

and stores the question in the database.

---

## 🔐 Security Architecture

ExamSecure is designed with security as an important part of the application.

```text
User
 │
 ▼
Login
 │
 ▼
Authentication
 │
 ▼
Session
 │
 ▼
AuthFilter
 │
 ├── Teacher → /admin/*
 │
 └── Student → /student/*
          │
          ▼
      Authorized Request
```

Database operations use **PreparedStatement** to reduce SQL injection risks, while passwords are stored as hashes rather than plain text.

---

# 🚀 Future Roadmap

ExamSecure is currently focused on building a strong and secure online examination system.

Future versions may introduce **AI and Computer Vision based examination monitoring**.

### 👁️ Computer Vision

Possible future features:

* Face detection
* Face presence verification
* Multiple-person detection
* Candidate identity verification
* Head-position monitoring
* Camera-based examination monitoring

### 🙂 Emotion Detection

Future versions could explore:

* Basic facial-expression analysis
* Stress / attention indicators
* Emotion trend analysis during examinations

> These features are planned concepts and are **not currently implemented**.

### 👄 Lip / Mouth Movement Detection

Potential future functionality:

* Lip movement detection
* Speaking detection
* Suspicious communication indicators

### 🤖 AI-Assisted Proctoring

The long-term goal is to explore an intelligent examination-monitoring system that can combine:

```text
Camera
  │
  ├── Face Detection
  ├── Multiple Person Detection
  ├── Head Movement
  ├── Eye / Attention Analysis
  ├── Lip Movement
  └── Expression Analysis
          │
          ▼
     AI Analysis
          │
          ▼
   Examination Integrity
```

These features would be added gradually and evaluated carefully to avoid false positives and unnecessary monitoring.

---

## 🗺️ Development Roadmap

### ✅ Phase 1 — Core Examination System

* [x] Student authentication
* [x] Teacher authentication
* [x] Role-based access
* [x] MCQ examination
* [x] PDF question import
* [x] Exam timer
* [x] Automatic evaluation
* [x] Result generation
* [x] Database integration

### 🔄 Phase 2 — Security & Improvements

* [ ] Improved session management
* [ ] Stronger authentication options
* [ ] Better exam analytics
* [ ] Improved teacher dashboard
* [ ] Better result visualization

### 🔮 Phase 3 — Intelligent Proctoring

* [ ] Camera integration
* [ ] Face detection
* [ ] Multiple-person detection
* [ ] Head movement analysis
* [ ] Lip movement detection
* [ ] Emotion / expression analysis
* [ ] AI-assisted suspicious-activity detection

---

## 💻 Running Locally

### Requirements

* Java 21
* Maven
* MySQL 8
* Apache Tomcat 10.1+
* Eclipse / VS Code / IntelliJ IDEA

### Database

Create the ExamSecure database using:

```text
schema.sql
```

### Database Configuration

Copy:

```text
src/main/resources/db.properties.example
```

to:

```text
src/main/resources/db.properties
```

Then add your own MySQL credentials.

> `db.properties` is intentionally ignored by Git so database credentials are not uploaded to GitHub.

### Build

```bash
mvn clean package
```

Deploy the generated WAR file to Tomcat.

Then open:

```text
http://localhost:8080/ExamSecure/
```

---

## 📸 Screenshots

Screenshots will be added as the project UI develops.

Planned screenshots:

* Login page
* Student dashboard
* Teacher dashboard
* PDF upload
* Exam creation
* Examination page
* Result page

---

## 🌟 Why ExamSecure?

ExamSecure is more than a basic CRUD application.

It combines:

```text
Java Servlets
      +
JSP
      +
MySQL
      +
PDF Processing
      +
Authentication
      +
Security
      +
Timed Examinations
      +
Automatic Evaluation
      +
Future AI / Computer Vision
```

The project is designed to gradually evolve from a **secure online examination platform** into an **intelligent examination and proctoring system**.

---

## 👨‍💻 Author

### Aditya

**ExamSecure — Secure Online Examination System**

Built as an academic project with a focus on:

**Java • Web Development • Databases • Security • AI/Computer Vision**

---

⭐ If you find this project interesting, feel free to explore the code and follow its development.
