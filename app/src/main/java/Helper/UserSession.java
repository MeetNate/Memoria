package Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserSession {
    private static UserSession instance;
    private String userName;
    private String userEmail;
    private String userPassword;
    private Map<String, String> classDetails; // Map for class, academic year, classroomId
    private List<String> groupChats; // List for group chat IDs or names
    private List<Map<String, String>> classList; // Holds list of classes
    private List<Map<String, String>> activities; // List for user activities
    private List<Map<String, String>> achievements; // List for user achievements
    private List<Map<String, Object>> results; // List for user results

    // Private constructor to prevent instantiation
    private UserSession() {
        classDetails = new HashMap<>(); // Initialize classDetails map
        classList = new ArrayList<>(); // Initialize classList
        groupChats = new ArrayList<>(); // Initialize groupChats
        activities = new ArrayList<>(); // Initialize activities
        achievements = new ArrayList<>(); // Initialize achievements
        results = new ArrayList<>(); // Initialize results
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

    public Map<String, String> getClassDetails() {
        return classDetails;
    }

    public void setClassDetails(Map<String, String> classDetails) {
        this.classDetails = classDetails;
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
    public List<Map<String, String>> getActivities() {
        return activities;
    }

    public void addActivity(String title, String description) {
        Map<String, String> activity = new HashMap<>();
        activity.put("title", title);
        activity.put("description", description);
        activities.add(activity);
    }

    public void clearActivities() {
        activities.clear();
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

    public void clearAchievements() {
        achievements.clear();
    }

    // Methods for managing results
    public List<Map<String, Object>> getResults() {
        return results;
    }

    public void addResult(String grade, double percentage) {
        Map<String, Object> result = new HashMap<>();
        result.put("grade", grade);
        result.put("percentage", percentage);
        results.add(result);
    }

    public void clearResults() {
        results.clear();
    }

    // Get all classes the user has joined
    public List<Map<String, String>> getClassList() {
        return classList;
    }
}
