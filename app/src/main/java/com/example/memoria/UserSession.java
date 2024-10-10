package com.example.memoria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserSession {
    private static UserSession instance;
    private String userName;
    private String userEmail;
    private String userPassword;
    private Map<String, String> classDetails; // Map for class, academic year, and division
    private List<String> groupChats; // List for group chat IDs or names
    private List<Map<String, String>> classList = new ArrayList<>(); // Holds list of classes

    // Private constructor to prevent instantiation
    private UserSession() {
        classDetails = new HashMap<>(); // Initialize classDetails map
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // Getters and Setters for user data
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Map<String, String> getClassDetails() {
        return classDetails;
    }

    public void setClassDetails(Map<String, String> classDetails) {
        this.classDetails = classDetails;
    }

    public List<String> getGroupChats() {
        return groupChats;
    }

    public List<Map<String, String>> getClassList() {
        return classList;
    }

    public void setGroupChats(List<String> groupChats) {
        this.groupChats = groupChats;
    }

    // Convenience method to set class details (class, academic year, division)
    public void setClassDetails(String classVal, String academicYear, String division) {
        if (this.classDetails == null) {
            this.classDetails = new HashMap<>(); // Ensure classDetails map is initialized
        }
        this.classDetails.put("classVal", classVal);
        this.classDetails.put("academicYear", academicYear);
        this.classDetails.put("division", division);
    }


    // Method to add a new class to the list
    public void addClassToList(String classVal, String division, String academicYear) {
        Map<String, String> newClass = new HashMap<>();
        newClass.put("classVal", classVal);
        newClass.put("division", division);
        newClass.put("academicYear", academicYear);
        classList.add(newClass);
    }

    // Convenience method to get specific class details
    public String getClassVal() {
        return classDetails != null ? classDetails.get("classVal") : null;
    }

    public String getAcademicYear() {
        return classDetails != null ? classDetails.get("academicYear") : null;
    }

    public String getDivision() {
        return classDetails != null ? classDetails.get("division") : null;
    }
}
