package com.studentinformationmanagementsystem.model;

public class Subject {
    private int subjectId;
    private String subjectName;
    private String description;
    private boolean status;
    private int teacherCount;
    // Constructors
    public Subject() {}

    public Subject(String subjectName, String description) {
        this.subjectName = subjectName;
        this.description = description;
        this.status = true;
    }

    // Getters and Setters
    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
    public int getTeacherCount() {
        return teacherCount;
    }
    public void setTeacherCount(int teacherCount) {
        this.teacherCount = teacherCount;
    }

    // Utility methods
    public boolean isActive() {
        return status;
    }

    // Override toString() for better object representation
    @Override
    public String toString() {
        return "Subject{" +
                "subjectId=" + subjectId +
                ", subjectName='" + subjectName + '\'' +
                ", status=" + status +
                '}';
    }

    // Override equals() and hashCode() for proper object comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subject subject = (Subject) o;

        if (subjectId != subject.subjectId) return false;
        return subjectName.equals(subject.subjectName);
    }

    @Override
    public int hashCode() {
        int result = subjectId;
        result = 31 * result + subjectName.hashCode();
        return result;
    }
}
