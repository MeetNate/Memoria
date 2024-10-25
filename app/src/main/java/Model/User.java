package Model;

public class User {
    private String name;
    private String email;
    private String classVal;        // Class Value
    private String academicYear;    // Academic Year

    public User(String name, String email, String classVal, String academicYear) {
        this.name = name;
        this.email = email;
        this.classVal = classVal;
        this.academicYear = academicYear;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getClassVal() {
        return classVal;
    }

    public String getAcademicYear() {
        return academicYear;
    }
}
