package com.studentinformationmanagementsystem.model;

public class ClassAssignment {
    private int assignmentId;
    private int teacherId;
    private int classId;
    private int subjectId;
    private int academicYear;

    // Additional fields for display purposes
    private String className;
    private String subjectName;
    private String teacherName;

    // Constructors
    public ClassAssignment() {}

    public ClassAssignment(int teacherId, int classId, int subjectId, int academicYear) {
        this.teacherId = teacherId;
        this.classId = classId;
        this.subjectId = subjectId;
        this.academicYear = academicYear;
    }

    // Getters and Setters
    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(int academicYear) {
        this.academicYear = academicYear;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    // Utility methods
    public boolean isCurrentYear() {
        return academicYear == java.time.Year.now().getValue();
    }

    public String getDisplayText() {
        return String.format("%s - %s (%d)", className, subjectName, academicYear);
    }

    // Override toString() for better object representation
    @Override
    public String toString() {
        return "ClassAssignment{" +
                "assignmentId=" + assignmentId +
                ", teacherId=" + teacherId +
                ", classId=" + classId +
                ", subjectId=" + subjectId +
                ", academicYear=" + academicYear +
                ", className='" + className + '\'' +
                ", subjectName='" + subjectName + '\'' +
                '}';
    }

    // Override equals() and hashCode() for proper object comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassAssignment that = (ClassAssignment) o;

        if (teacherId != that.teacherId) return false;
        if (classId != that.classId) return false;
        if (subjectId != that.subjectId) return false;
        return academicYear == that.academicYear;
    }

    @Override
    public int hashCode() {
        int result = teacherId;
        result = 31 * result + classId;
        result = 31 * result + subjectId;
        result = 31 * result + academicYear;
        return result;
    }
}
