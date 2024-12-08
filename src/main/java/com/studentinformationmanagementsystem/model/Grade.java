package com.studentinformationmanagementsystem.model;


import java.sql.Date;

public class Grade {
    private int gradeId;
    private int studentId;
    private int subjectId;
    private int classId;
    private int academicYear;
    private int term;
    private double grade;
    private Date gradingDate;
    private int teacherId;
    private String comments;

    // Constructors
    public Grade() {}

    public Grade(int studentId, int subjectId, int classId, int academicYear, int term,
                 double grade, Date gradingDate, int teacherId, String comments) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.classId = classId;
        this.academicYear = academicYear;
        this.term = term;
        this.grade = grade;
        this.gradingDate = gradingDate;
        this.teacherId = teacherId;
        this.comments = comments;
    }

    // Getters and Setters
    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(int academicYear) {
        this.academicYear = academicYear;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public Date getGradingDate() {
        return gradingDate;
    }

    public void setGradingDate(Date gradingDate) {
        this.gradingDate = gradingDate;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    // Utility methods
    public boolean isPassingGrade() {
        return grade >= 50.0;
    }

    public String getGradeLevel() {
        if (grade >= 75.0) return "A";
        if (grade >= 65.0) return "B";
        if (grade >= 50.0) return "C";
        return "F";
    }

    // Override toString() for debugging and logging
    @Override
    public String toString() {
        return "Grade{" +
                "gradeId=" + gradeId +
                ", studentId=" + studentId +
                ", subjectId=" + subjectId +
                ", classId=" + classId +
                ", academicYear=" + academicYear +
                ", term=" + term +
                ", grade=" + grade +
                ", gradingDate=" + gradingDate +
                ", teacherId=" + teacherId +
                ", comments='" + comments + '\'' +
                '}';
    }

    // Override equals() and hashCode() for proper object comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Grade grade = (Grade) o;

        if (gradeId != grade.gradeId) return false;
        if (studentId != grade.studentId) return false;
        if (subjectId != grade.subjectId) return false;
        if (classId != grade.classId) return false;
        if (academicYear != grade.academicYear) return false;
        return term == grade.term;
    }

    @Override
    public int hashCode() {
        int result = gradeId;
        result = 31 * result + studentId;
        result = 31 * result + subjectId;
        result = 31 * result + classId;
        result = 31 * result + academicYear;
        result = 31 * result + term;
        return result;
    }
}