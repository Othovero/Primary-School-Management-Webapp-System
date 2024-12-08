package com.studentinformationmanagementsystem.controller;

import com.studentinformationmanagementsystem.dao.StudentDAO;
import com.studentinformationmanagementsystem.model.SchoolClass;
import com.studentinformationmanagementsystem.model.Student;
import com.studentinformationmanagementsystem.dao.ClassDAO;
import com.studentinformationmanagementsystem.dao.EnrollmentDAO;
import com.studentinformationmanagementsystem.model.Enrollment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.sql.Date;

@WebServlet("/student")
public class StudentControllerServlet extends HttpServlet {
    private StudentDAO studentDAO;
    private SimpleDateFormat dateFormat;
    private ClassDAO classDAO;
    private EnrollmentDAO enrollmentDAO;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAO();
        classDAO = new ClassDAO();
        enrollmentDAO = new EnrollmentDAO();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String command = request.getParameter("command");
            if (command == null) {
                command = "LIST";
            }

            switch (command) {
                case "LIST":
                    listStudents(request, response);
                    break;
                case "ADD_FORM":
                    showAddForm(request, response);
                    break;
                case "LOAD":
                    loadStudent(request, response);
                    break;
                default:
                    listStudents(request, response);
            }
        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String command = request.getParameter("command");
        if (command == null) {
            command = "LIST";
        }

        try {
            switch (command) {
                case "ADD":
                    addStudent(request, response);
                    break;
                case "UPDATE":
                    updateStudent(request, response);
                    break;
                case "DELETE":
                    deleteStudent(request, response);
                    break;
                default:
                    listStudents(request, response);
            }
        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    private void addStudent(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ParseException, ServletException {
        Student student = new Student();
        student.setBirthCertificateNo(request.getParameter("birthCertificateNo"));
        student.setFirstName(request.getParameter("firstName"));
        student.setLastName(request.getParameter("lastName"));
        student.setGender(request.getParameter("gender"));
        String dobString = request.getParameter("dateOfBirth");
        Date dob = Date.valueOf(dobString);  // Assumes format YYYY-MM-DD
        student.setDateOfBirth(dob);
        student.setAddress(request.getParameter("address"));
        student.setGuardianName(request.getParameter("guardianName"));
        student.setGuardianContact(request.getParameter("guardianContact"));
        student.setGuardianEmail(request.getParameter("guardianEmail"));
        student.setStatus("ACTIVE");

        String classIdStr = request.getParameter("currentClassId");
        if (classIdStr != null && !classIdStr.trim().isEmpty()) {
            int classId = Integer.parseInt(classIdStr);
            student.setCurrentClassId(classId);
        }

        try {
            validateStudent(student);

            if (studentDAO.exists(student.getBirthCertificateNo())) {
                request.getSession().setAttribute("error",
                        "A student with this birth certificate number already exists.");
                request.setAttribute("student", student);
                List<SchoolClass> schoolClasses = classDAO.getAllClasses();
                request.setAttribute("schoolClasses", schoolClasses);
                request.getRequestDispatcher("/WEB-INF/add-student.jsp").forward(request, response);
                return;
            }

            studentDAO.insertStudent(student);

            if (student.getCurrentClassId() != null) {
                Enrollment enrollment = new Enrollment();
                enrollment.setStudentId(student.getStudentId());
                enrollment.setClassId(student.getCurrentClassId());
                enrollment.setAcademicYear(Calendar.getInstance().get(Calendar.YEAR));
                enrollment.setEnrollmentDate(new Date(System.currentTimeMillis()));
                enrollment.setStatus("ACTIVE");
                enrollmentDAO.insertEnrollment(enrollment);
            }
            request.getSession().setAttribute("success",
                    "Student added successfully: " + student.getFullName());
            response.sendRedirect(request.getContextPath() + "/student?command=LIST");

        } catch (ValidationException e) {
            request.getSession().setAttribute("error", e.getMessage());
            List<SchoolClass> schoolClasses = classDAO.getAllClasses();
            request.setAttribute("schoolClasses", schoolClasses);
            request.getRequestDispatcher("/WEB-INF/add-student.jsp").forward(request, response);
        }

    }

    private void updateStudent(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ParseException, ServletException {
        int studentId = Integer.parseInt(request.getParameter("studentId"));
        Student student = studentDAO.getStudentById(studentId);

        if (student == null) {
            request.getSession().setAttribute("error", "Student not found.");
            response.sendRedirect(request.getContextPath() + "/student?command=LIST");
            return;
        }

        student.setBirthCertificateNo(request.getParameter("birthCertificateNo"));
        student.setFirstName(request.getParameter("firstName"));
        student.setLastName(request.getParameter("lastName"));
        student.setGender(request.getParameter("gender"));
        java.util.Date utilDate = dateFormat.parse(request.getParameter("dateOfBirth"));
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        student.setDateOfBirth(sqlDate);
        student.setAddress(request.getParameter("address"));
        student.setGuardianName(request.getParameter("guardianName"));
        student.setGuardianContact(request.getParameter("guardianContact"));
        student.setGuardianEmail(request.getParameter("guardianEmail"));
        student.setStatus(request.getParameter("status"));

        String newClassIdStr = request.getParameter("currentClassId");
        if (newClassIdStr != null && !newClassIdStr.trim().isEmpty()) {
            int newClassId = Integer.parseInt(newClassIdStr);
            if (student.getCurrentClassId() == null || student.getCurrentClassId() != newClassId) {
                // Withdraw from current class if exists
                if (student.getCurrentClassId() != null) {
                    enrollmentDAO.withdrawEnrollment(studentId, student.getCurrentClassId());
                }
                // Create new enrollment
                Enrollment enrollment = new Enrollment();
                enrollment.setStudentId(studentId);
                enrollment.setClassId(newClassId);
                enrollment.setAcademicYear(Calendar.getInstance().get(Calendar.YEAR));
                enrollment.setEnrollmentDate(new Date(System.currentTimeMillis()));
                enrollment.setStatus("ACTIVE");
                enrollmentDAO.insertEnrollment(enrollment);
            }
            student.setCurrentClassId(newClassId);
        }

        try {
            validateStudent(student);
            studentDAO.updateStudent(student);
            request.getSession().setAttribute("success",
                    "Student updated successfully: " + student.getFullName());
            response.sendRedirect(request.getContextPath() + "/student?command=LIST");
        } catch (ValidationException e) {
            request.getSession().setAttribute("error", e.getMessage());
            request.setAttribute("student", student);
            request.getRequestDispatcher("/WEB-INF/student-details.jsp").forward(request, response);
        }
    }
    private void deleteStudent(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        int studentId = Integer.parseInt(request.getParameter("studentId"));
        Student student = studentDAO.getStudentById(studentId);
        if (student != null && student.getCurrentClassId() != null) {
            enrollmentDAO.withdrawEnrollment(studentId, student.getCurrentClassId());
        }
        studentDAO.deleteStudent(studentId);
        request.getSession().setAttribute("success", "Student deleted successfully.");
        response.sendRedirect(request.getContextPath() + "/student?command=LIST");
    }

    private void listStudents(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<Student> students = studentDAO.getAllStudents();
        List<SchoolClass> schoolClasses = classDAO.getAllClasses();
        request.setAttribute("students", students);
        request.setAttribute("schoolClasses", schoolClasses);
        request.getRequestDispatcher("/WEB-INF/list-student.jsp")
                .forward(request, response);
    }

    private void searchStudents(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String searchTerm = request.getParameter("searchTerm");
        List<Student> students = studentDAO.searchStudents(searchTerm);
        request.setAttribute("students", students);
        request.setAttribute("searchTerm", searchTerm);
        request.getRequestDispatcher("/WEB-INF/list-student.jsp")
                .forward(request, response);
    }

    private void loadStudent(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        try {
            // Get student data
            int studentId = Integer.parseInt(request.getParameter("studentId"));
            Student student = studentDAO.getStudentById(studentId);

            if (student == null) {
                request.getSession().setAttribute("error", "Student not found.");
                response.sendRedirect(request.getContextPath() + "/student?command=LIST");
                return;
            }

            // Get available classes for assignment
            List<SchoolClass> schoolClasses = classDAO.getAllClasses();

            // Set attributes
            request.setAttribute("student", student);
            request.setAttribute("schoolClasses", schoolClasses);

            // Forward to edit page
            request.getRequestDispatcher("/WEB-INF/edit-student.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            System.err.println("Error in loadStudent: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("error", "Error loading student: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/student?command=LIST");
        }
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<SchoolClass> schoolClasses = classDAO.getAllClasses();
            request.setAttribute("schoolClasses", schoolClasses);
            request.getRequestDispatcher("/WEB-INF/add-student.jsp").forward(request, response);
        } catch (SQLException e) {
            handleException(request, response, e);
        }
    }

    private void validateStudent(Student student) throws ValidationException {
        if (student.getBirthCertificateNo() == null ||
                !student.getBirthCertificateNo().matches("\\d{9}")) {
            throw new ValidationException("Invalid birth certificate number. " +
                    "Must be 9 digits.");
        }
        if (student.getFirstName() == null || student.getFirstName().trim().isEmpty()) {
            throw new ValidationException("First name is required.");
        }
        if (student.getLastName() == null || student.getLastName().trim().isEmpty()) {
            throw new ValidationException("Last name is required.");
        }
        if (student.getDateOfBirth() == null) {
            throw new ValidationException("Date of birth is required.");
        }
        if (student.getGuardianContact() == null ||
                !student.getGuardianContact().matches("\\d{10}")) {
            throw new ValidationException("Invalid guardian contact number. " +
                    "Must be 10 digits.");
        }
        if (student.getGuardianEmail() != null && !student.getGuardianEmail().isEmpty() &&
                !student.getGuardianEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Invalid guardian email format.");
        }
    }

    private void handleException(HttpServletRequest request, HttpServletResponse response,
                                 Exception e) throws ServletException, IOException {
        e.printStackTrace();  // For logging
        request.getSession().setAttribute("error", "An error occurred: " + e.getMessage());
        request.getRequestDispatcher("/WEB-INF/list-student.jsp").forward(request, response);
    }
    private static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}
