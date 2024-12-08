package com.studentinformationmanagementsystem.model;

import java.sql.Date;



public class Student {
    private int studentId;
    private String birthCertificateNo;
    private String firstName;
    private String lastName;
    private String gender;
    private Date dateOfBirth;
    private String address;
    private String guardianName;
    private String guardianContact;
    private String guardianEmail;
    private Date registrationDate;
    private String status;
    private Integer currentClassId;
    private String additionalNotes;

    // Constructors
    public Student() {this.registrationDate = new Date(System.currentTimeMillis()); // Initialize registration date
        this.status = "ACTIVE"; // Initialize status
    }


    public Student(String birthCertificateNo, String firstName, String lastName,
                   String gender, Date dateOfBirth, String address,
                   String guardianName, String guardianContact, String guardianEmail) {
        this.birthCertificateNo = birthCertificateNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.guardianName = guardianName;
        this.guardianContact = guardianContact;
        this.guardianEmail = guardianEmail;
        this.registrationDate = new Date(System.currentTimeMillis());
        this.status = "ACTIVE";
    }

    // Getters and Setters
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getBirthCertificateNo() {
        return birthCertificateNo;
    }

    public void setBirthCertificateNo(String birthCertificateNo) {
        this.birthCertificateNo = birthCertificateNo;
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

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public String getGuardianContact() {
        return guardianContact;
    }

    public void setGuardianContact(String guardianContact) {
        this.guardianContact = guardianContact;
    }

    public String getGuardianEmail() {
        return guardianEmail;
    }

    public void setGuardianEmail(String guardianEmail) {
        this.guardianEmail = guardianEmail;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCurrentClassId() {
        return currentClassId;
    }

    public void setCurrentClassId(Integer currentClassId) {
        this.currentClassId = currentClassId;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }
    private String className;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    // Utility methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    public boolean isGraduated() {
        return "GRADUATED".equals(status);
    }

    public boolean isTransferred() {
        return "TRANSFERRED".equals(status);
    }

    private double currentGrade;
    private String comments;

    // Add getters and setters
    public double getCurrentGrade() {
        return currentGrade;
    }

    public void setCurrentGrade(double currentGrade) {
        this.currentGrade = currentGrade;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    // Override toString() method for better object representation
    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", birthCertificateNo='" + birthCertificateNo + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                ", guardianName='" + guardianName + '\'' +
                ", guardianContact='" + guardianContact + '\'' +
                ", guardianEmail='" + guardianEmail + '\'' +
                ", registrationDate=" + registrationDate +
                ", status='" + status + '\'' +
                ", currentClassId=" + currentClassId +
                ", additionalNotes='" + additionalNotes + '\'' +
                '}';
    }

    // Override equals() and hashCode() methods for proper object comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        if (studentId != student.studentId) return false;
        return birthCertificateNo.equals(student.birthCertificateNo);
    }

    @Override
    public int hashCode() {
        int result = studentId;
        result = 31 * result + birthCertificateNo.hashCode();
        return result;
    }

    // Additional utility methods
    public int getAge() {
        if (dateOfBirth == null) return 0;

        java.util.Date currentDate = new java.util.Date();
        java.util.Date birthDate = new java.util.Date(dateOfBirth.getTime());

        long diffInMillies = Math.abs(currentDate.getTime() - birthDate.getTime());
        long diffInYears = diffInMillies / (1000L * 60 * 60 * 24 * 365);
        return (int) diffInYears;
    }

    public boolean isEligibleForGraduation(int minimumAge) {
        return getAge() >= minimumAge && isActive();
    }

    public boolean needsGuardianContactUpdate() {
        return guardianContact == null || guardianContact.trim().isEmpty();
    }

    public boolean hasValidEmail() {
        return guardianEmail != null &&
                guardianEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
