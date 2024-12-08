package com.studentinformationmanagementsystem.model;

import java.util.Date;

public class SchoolClass {
    private int classId;
    private String className;
    private int academicYear;
    private int capacity;
    private boolean status;
    private Date createdAt;

    // Constructors
    public SchoolClass() {}

    public SchoolClass(String className, int academicYear, int capacity) {
        this.className = className;
        this.academicYear = academicYear;
        this.capacity = capacity;
        this.status = true;
        this.createdAt = new Date();
    }

    // Getters and Setters
    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(int academicYear) {
        this.academicYear = academicYear;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    private int currentEnrollment;

    public int getCurrentEnrollment() {
        return currentEnrollment;
    }

    public void setCurrentEnrollment(int currentEnrollment) {
        this.currentEnrollment = currentEnrollment;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // Utility methods
    public boolean isActive() {
        return status;
    }

    public boolean isCurrentYear() {
        return academicYear == java.time.Year.now().getValue();
    }

    // Override toString() for better object representation
    @Override
    public String toString() {
        return "Class{" +
                "classId=" + classId +
                ", className='" + className + '\'' +
                ", academicYear=" + academicYear +
                ", capacity=" + capacity +
                ", status=" + status +
                '}';
    }

    // Override equals() and hashCode() for proper object comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SchoolClass aSchoolClass = (SchoolClass) o;

        if (classId != aSchoolClass.classId) return false;
        if (academicYear != aSchoolClass.academicYear) return false;
        return className.equals(aSchoolClass.className);
    }

    @Override
    public int hashCode() {
        int result = classId;
        result = 31 * result + className.hashCode();
        result = 31 * result + academicYear;
        return result;
    }
}