package Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserSession {
    private static UserSession instance;
    private String userName;
    private String userEmail;
    private Map<String, String> classDetails; // Map for class, academic year, classroomId
    private List<Map<String, String>> classList; // Holds list of classes
    private List<Map<String, String>> sports; // List for user activities
    private List<Map<String, String>> achievements; // List for user achievements
    private List<Map<String, Object>> results; // List for user results
    private List<Map<String, String>> subjects; // List for subjects

    // Private constructor to prevent instantiation
    private UserSession() {
        classDetails = new HashMap<>(); // Initialize classDetails map
        classList = new ArrayList<>(); // Initialize classList
        sports = new ArrayList<>(); // Initialize activities
        achievements = new ArrayList<>(); // Initialize achievements
        results = new ArrayList<>(); // Initialize results
        subjects = new ArrayList<>(); // Initialize subjects
    }

    // Singleton pattern for UserSession
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


    // Convenience method to set class details (class, academic year, classroomId)
    public void setClassDetails(String classVal, String academicYear, String classroomId) {
        if (this.classDetails == null) {
            this.classDetails = new HashMap<>(); // Ensure classDetails map is initialized
        }
        this.classDetails.put("classVal", classVal);
        this.classDetails.put("academicYear", academicYear);
        this.classDetails.put("classroomId", classroomId);
    }



    // Method to add a new class to the user's class list
    public void addClassToList(String classVal, String academicYear, String classroomId) {
        if (!hasClassroomId(classroomId)) {
            Map<String, String> newClass = new HashMap<>();
            newClass.put("classVal", classVal);
            newClass.put("academicYear", academicYear);
            newClass.put("classroomId", classroomId);
            classList.add(newClass);
        }
    }

    // Method to update the class details at the 0th index of the class list
    public void updateFirstClassInList(String classVal, String academicYear, String classroomId) {
        // Check if the classList is not empty
        if (!classList.isEmpty()) {
            // Get the first class from the list
            Map<String, String> firstClass = classList.get(0);

            // Update the values
            firstClass.put("classVal", classVal);
            firstClass.put("academicYear", academicYear);
            firstClass.put("classroomId", classroomId);
        } else {
            // Handle the case where classList is empty
            System.out.println("Class list is empty. Cannot update the first class.");
        }
    }


    // Get all classes the user has joined
    public List<Map<String, String>> getClassList() {
        return classList;
    }

    // Method to check if a classroomId is already in the class list
    public boolean hasClassroomId(String classroomId) {
        for (Map<String, String> classItem : classList) {
            if (classItem.containsValue(classroomId)) {
                return true;
            }
        }
        return false;
    }

    // Method to clear the current user's class list
    public void clearClassList() {
        classList.clear(); // Clear the list to prevent duplication
    }

    // Convenience method to get specific class details
    public String getClassVal() {
        return classDetails != null ? classDetails.get("classVal") : null;
    }

    public String getClassroomId() {
        return classDetails != null ? classDetails.get("classroomId") : null;
    }

    public String getAcademicYear() {
        return classDetails != null ? classDetails.get("academicYear") : null;
    }

    // Methods for managing activities
    public List<Map<String, String>> getSports() {
        return sports;
    }

    public void addSport(String title, String description) {
        Map<String, String> activity = new HashMap<>();
        activity.put("title", title);
        activity.put("description", description);
        sports.add(activity);
    }

    public void clearActivities() {
        sports.clear();
    }

    // Methods for managing achievements
    public List<Map<String, String>> getAchievements() {
        return achievements;
    }

    public void addAchievement(String title, String description) {
        Map<String, String> achievement = new HashMap<>();
        achievement.put("title", title);
        achievement.put("description", description);
        achievements.add(achievement);
    }


    public void addResult(String grade, Double percentage) {
        Map<String, Object> result = new HashMap<>();
        result.put("grade", grade);
        result.put("percentage", percentage);
        results.add(result);
    }




}
