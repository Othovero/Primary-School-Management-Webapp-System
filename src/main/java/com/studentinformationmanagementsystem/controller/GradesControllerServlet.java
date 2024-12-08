package com.studentinformationmanagementsystem.controller;

import com.studentinformationmanagementsystem.dao.*;
import com.studentinformationmanagementsystem.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.*;

@WebServlet("/grades")
public class GradesControllerServlet extends HttpServlet {
    private GradesDAO gradesDAO;
    private StudentDAO studentDAO;
    private TeacherDAO teacherDAO;
    private ClassDAO classDAO;
    private SubjectDAO subjectDAO;

    @Override
    public void init() throws ServletException {
        gradesDAO = new GradesDAO();
        studentDAO = new StudentDAO();
        teacherDAO = new TeacherDAO();
        classDAO = new ClassDAO();
        subjectDAO = new SubjectDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String command = request.getParameter("command");
            if (command == null) {
                command = "LIST";
            }

            // Get lists needed for most views
            List<SchoolClass> classes = classDAO.getAllClasses();
            List<Subject> subjects = subjectDAO.getAllSubjects();
            request.setAttribute("classes", classes);
            request.setAttribute("subjects", subjects);

            switch (command) {
                case "LIST":
                    listGrades(request, response);
                    break;
                case "ADD_FORM":
                    showAddForm(request, response);
                    break;
                case "SHOW_STUDENTS":
                    showStudentsForGrading(request, response);
                    break;
                case "VIEW_STUDENT":
                    viewStudentGrades(request, response);
                    break;
                case "TOP_PERFORMERS":
                    showTopPerformers(request, response);
                    break;
                default:
                    listGrades(request, response);
            }
        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String command = request.getParameter("command");
            if (command == null) {
                command = "LIST";
            }

            switch (command) {
                case "ADD":
                    addGrades(request, response);
                    break;
                default:
                    listGrades(request, response);
            }
        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    private void listGrades(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        try {
            // Get all classes for dropdown
            List<SchoolClass> classes = classDAO.getAllClasses();
            request.setAttribute("classes", classes);

            // If a class is selected, get students and their grades
            String classIdStr = request.getParameter("classId");
            if (classIdStr != null && !classIdStr.trim().isEmpty()) {
                int classId = Integer.parseInt(classIdStr);
                // Debug log
                System.out.println("Loading grades for class ID: " + classId);

                // Get the selected class
                SchoolClass selectedClass = classDAO.getClassById(classId);
                List<Student> students = studentDAO.getStudentsByClass(classId);
                // Debug log
                System.out.println("Found " + students.size() + " students in class");

                // Get average grades for each student
                for (Student student : students) {
                    double averageGrade = gradesDAO.getStudentAverage(student.getStudentId(), classId);
                    student.setCurrentGrade(averageGrade);
                    // Debug log
                    System.out.println("Student: " + student.getFullName() + ", Average Grade: " + averageGrade);
                }

                request.setAttribute("students", students);
                request.setAttribute("selectedClass", selectedClass);
            }

            request.getRequestDispatcher("/WEB-INF/list-grades.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Error in listGrades: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading grades: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/list-grades.jsp").forward(request, response);
        }
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/add-grades.jsp").forward(request, response);
    }

    private void showStudentsForGrading(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int classId = Integer.parseInt(request.getParameter("classId"));
        int subjectId = Integer.parseInt(request.getParameter("subjectId"));
        int term = Integer.parseInt(request.getParameter("term"));

        List<Student> students = studentDAO.getStudentsByClass(classId);
        SchoolClass selectedClass = classDAO.getClassById(classId);
        Subject selectedSubject = subjectDAO.getSubjectById(subjectId);

        // Get existing grades if any
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (Student student : students) {
            Grade grade = gradesDAO.getGrade(student.getStudentId(), subjectId, classId, term, currentYear);
            if (grade != null) {
                student.setCurrentGrade(grade.getGrade());
                student.setComments(grade.getComments());
            }
        }

        request.setAttribute("students", students);
        request.setAttribute("selectedClass", selectedClass);
        request.setAttribute("selectedSubject", selectedSubject);
        request.setAttribute("selectedTerm", term);

        request.getRequestDispatcher("/WEB-INF/add-grades.jsp").forward(request, response);
    }

    private void viewStudentGrades(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        try {
            int studentId = Integer.parseInt(request.getParameter("studentId"));
            Student student = studentDAO.getStudentById(studentId);

            // Get current class ID
            int classId;
            if (request.getParameter("classId") != null) {
                classId = Integer.parseInt(request.getParameter("classId"));
            } else if (student.getCurrentClassId() != null) {
                classId = student.getCurrentClassId();
            } else {
                request.setAttribute("error", "No class found for student");
                request.getRequestDispatcher("/WEB-INF/list-grades.jsp").forward(request, response);
                return;
            }

            // Get the class details
            SchoolClass selectedClass = classDAO.getClassById(classId);

            // Get grades map (subject -> term grades)
            Map<String, List<Double>> gradesMap = gradesDAO.getStudentGrades(studentId, classId);
            System.out.println("Grades found: " + gradesMap.size()); // Debug log

            // Get student's comments
            List<Map<String, Object>> comments = gradesDAO.getStudentComments(studentId, classId);

            // Calculate overall average
            double averageGrade = gradesDAO.getStudentAverage(studentId, classId);
            student.setCurrentGrade(averageGrade);

            // Set attributes
            request.setAttribute("student", student);
            request.setAttribute("selectedClass", selectedClass);
            request.setAttribute("gradesMap", gradesMap);
            request.setAttribute("comments", comments);

            // Forward to the view
            request.getRequestDispatcher("/WEB-INF/view-student-grades.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            System.err.println("Error in viewStudentGrades: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading student grades: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/list-grades.jsp")
                    .forward(request, response);
        }
    }
    private void showTopPerformers(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        try {
            // Get all classes for dropdown
            List<SchoolClass> classes = classDAO.getAllClasses();
            request.setAttribute("classes", classes);

            if (request.getParameter("classId") != null) {
                int classId = Integer.parseInt(request.getParameter("classId"));
                SchoolClass selectedClass = classDAO.getClassById(classId);

                // Get top 5 students
                List<Map.Entry<Student, Double>> topPerformers = gradesDAO.getTopPerformers(classId, 5);
                System.out.println("Found " + topPerformers.size() + " top performers"); // Debug log

                // Get top performers by subject
                Map<String, Map.Entry<Student, Double>> topPerformersBySubject =
                        gradesDAO.getTopPerformersBySubject(classId);
                System.out.println("Found top performers for " + topPerformersBySubject.size() + " subjects"); // Debug log

                request.setAttribute("selectedClass", selectedClass);
                request.setAttribute("topPerformers", topPerformers);
                request.setAttribute("topPerformersBySubject", topPerformersBySubject);
            }

            request.getRequestDispatcher("/WEB-INF/view-top-performers.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Error in showTopPerformers: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading top performers: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/list-grades.jsp").forward(request, response);
        }
    }

    private void addGrades(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        try {
            int classId = Integer.parseInt(request.getParameter("classId"));
            int subjectId = Integer.parseInt(request.getParameter("subjectId"));
            int term = Integer.parseInt(request.getParameter("term"));

            String[] studentIds = request.getParameterValues("studentIds");
            String[] grades = request.getParameterValues("grades");
            String[] comments = request.getParameterValues("comments");

            Date gradingDate = new Date(System.currentTimeMillis());
            int academicYear = Calendar.getInstance().get(Calendar.YEAR);
            int teacherId = teacherDAO.getTeacherIdForClassAndSubject(classId, subjectId);

            for (int i = 0; i < studentIds.length; i++) {
                if (grades[i] != null && !grades[i].trim().isEmpty()) {
                    Grade grade = new Grade();
                    grade.setStudentId(Integer.parseInt(studentIds[i]));
                    grade.setSubjectId(subjectId);
                    grade.setClassId(classId);
                    grade.setTeacherId(teacherId);
                    grade.setTerm(term);
                    grade.setAcademicYear(academicYear);
                    grade.setGrade(Double.parseDouble(grades[i]));
                    grade.setGradingDate(gradingDate);
                    grade.setComments(comments != null ? comments[i] : null);

                    gradesDAO.saveGrade(grade);
                }
            }

            request.setAttribute("success", "Grades saved successfully");
            showStudentsForGrading(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "Error saving grades: " + e.getMessage());
            showAddForm(request, response);
        }
    }

    private void handleException(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws ServletException, IOException {
        e.printStackTrace();
        request.setAttribute("error", "An error occurred: " + e.getMessage());
        request.getRequestDispatcher("/WEB-INF/list-grades.jsp").forward(request, response);
    }
}