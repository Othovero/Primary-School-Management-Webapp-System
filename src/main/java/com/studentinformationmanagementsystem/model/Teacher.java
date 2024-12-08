package com.studentinformationmanagementsystem.model;

import java.util.Date;
import java.util.List;

public class Teacher {
    private int teacherId;
    private String omangPassportNo;
    private String firstName;
    private String lastName;
    private String gender;
    private Date dateOfBirth;
    private String address;
    private String contactNo;
    private String email;
    private String qualifications;
    private Date joinDate;
    private boolean status;
    private int userId;
    private List<Subject> qualifiedSubjects;
    private List<ClassAssignment> classAssignments;

    // Constructors
    public Teacher() {}

    public Teacher(String omangPassportNo, String firstName, String lastName,
                   String gender, Date dateOfBirth, String address,
                   String contactNo, String email, String qualifications) {
        this.omangPassportNo = omangPassportNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.contactNo = contactNo;
        this.email = email;
        this.qualifications = qualifications;
        this.joinDate = new Date();
        this.status = true;
    }

    // Getters and Setters
    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getOmangPassportNo() {
        return omangPassportNo;
    }

    public void setOmangPassportNo(String omangPassportNo) {
        this.omangPassportNo = omangPassportNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQualifications() {
        return qualifications;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Subject> getQualifiedSubjects() {
        return qualifiedSubjects;
    }

    public void setQualifiedSubjects(List<Subject> qualifiedSubjects) {
        this.qualifiedSubjects = qualifiedSubjects;
    }

    public List<ClassAssignment> getClassAssignments() {
        return classAssignments;
    }

    public void setClassAssignments(List<ClassAssignment> classAssignments) {
        this.classAssignments = classAssignments;
    }

    // Utility methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isActive() {
        return status;
    }

    public void activate() {
        this.status = true;
    }

    public void deactivate() {
        this.status = false;
    }

    public boolean canTeachSubject(Subject subject) {
        if (qualifiedSubjects == null) return false;
        return qualifiedSubjects.stream()
                .anyMatch(s -> s.getSubjectId() == subject.getSubjectId());
    }

    public boolean hasClassAssignment(int classId) {
        if (classAssignments == null) return false;
        return classAssignments.stream()
                .anyMatch(ca -> ca.getClassId() == classId);
    }

    public int getYearsOfService() {
        if (joinDate == null) return 0;
        long diffInMillies = Math.abs(new Date().getTime() - joinDate.getTime());
        return (int) (diffInMillies / (1000L * 60 * 60 * 24 * 365));
    }

    public boolean isValidEmail() {
        return email != null &&
                email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public boolean isValidContactNo() {
        return contactNo != null &&
                contactNo.matches("\\d{10}");
    }

    public int getNumberOfSubjects() {
        return qualifiedSubjects != null ? qualifiedSubjects.size() : 0;
    }

    public int getNumberOfClasses() {
        return classAssignments != null ? classAssignments.size() : 0;
    }

    // Override toString() for better object representation
    @Override
    public String toString() {
        return "Teacher{" +
                "teacherId=" + teacherId +
                ", omangPassportNo='" + omangPassportNo + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", qualifiedSubjects=" + qualifiedSubjects.size() +
                ", classAssignments=" + classAssignments.size() +
                '}';
    }

    // Override equals() and hashCode() for proper object comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Teacher teacher = (Teacher) o;

        if (teacherId != teacher.teacherId) return false;
        return omangPassportNo.equals(teacher.omangPassportNo);
    }

    @Override
    public int hashCode() {
        int result = teacherId;
        result = 31 * result + omangPassportNo.hashCode();
        return result;
    }

    // Additional utility methods
    public boolean isQualifiedForSubject(String subjectName) {
        if (qualifiedSubjects == null) return false;
        return qualifiedSubjects.stream()
                .anyMatch(s -> s.getSubjectName().equalsIgnoreCase(subjectName));
    }

    public int getTeachingLoad() {
        return getNumberOfClasses();
    }

    public boolean hasMaximumLoad(int maxLoad) {
        return getTeachingLoad() >= maxLoad;
    }

    public ClassAssignment getAssignmentForClass(int classId) {
        if (classAssignments == null) return null;
        return classAssignments.stream()
                .filter(ca -> ca.getClassId() == classId)
                .findFirst()
                .orElse(null);
    }
}
