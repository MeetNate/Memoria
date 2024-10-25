package Helper;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import java.util.regex.Pattern;

public class ValidationHelper {

    // Define constants for validation
    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 15;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MIN_CLASS_VALUE = 1;
    private static final int MAX_CLASS_VALUE = 12;

    // Method to validate user inputs (name, email, password)
    public static boolean validateUserInputs(Context context, String name, String email, String password) {
        if (!isValidName(context, name)) return false;
        if (!isValidEmail(context, email)) return false;
        if (!isValidPassword(context, password)) return false;

        return true;
    }

    public static boolean validateUserInputs(Context context, String name, String email) {
        if (!isValidName(context, name)) return false;
        if (!isValidEmail(context, email)) return false;

        return true;
    }

    // Method to validate class name and academic year
    public static boolean validateClassAndYear(Context context, String className, String academicYear) {
        if (!validateClassValue(context, className)) return false;
        if (!validateAcademicYear(context, academicYear)) return false;

        return true;
    }

    // Validate user name
    private static boolean isValidName(Context context, String name) {
        if (TextUtils.isEmpty(name)) {
            showToast(context, "Please enter your name");
            return false;
        } else if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
            showToast(context, "Name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters");
            return false;
        }
        return true;
    }

    // Validate email address
    private static boolean isValidEmail(Context context, String email) {
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast(context, "Please enter a valid email address");
            return false;
        }
        return true;
    }

    // Validate password
    private static boolean isValidPassword(Context context, String password) {
        if (TextUtils.isEmpty(password)) {
            showToast(context, "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
            return false;
        } else if (!password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#\\$%^&*(),.?\":{}|<>]).{" + MIN_PASSWORD_LENGTH + ",}$")) {
            showToast(context, "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
            return false;
        }
        return true;
    }

    // Validate class value (1 to 12)
    private static boolean validateClassValue(Context context, String className) {
        try {
            int classVal = Integer.parseInt(className);
            if (classVal < MIN_CLASS_VALUE || classVal > MAX_CLASS_VALUE) {
                showToast(context, "Class value must be between " + MIN_CLASS_VALUE + " and " + MAX_CLASS_VALUE);
                return false;
            }
        } catch (NumberFormatException e) {
            showToast(context, "Class value must be a number.");
            return false;
        }
        return true;
    }

    // Validate academic year (YYYY-YY format)
    private static boolean validateAcademicYear(Context context, String academicYear) {
        if (!Pattern.matches("\\d{4}-\\d{2}", academicYear)) {
            showToast(context, "Academic year must be in the format 'YYYY-YY' (e.g., 2020-21).");
            return false;
        }

        String[] years = academicYear.split("-");
        int startYear = Integer.parseInt(years[0]);
        int endYear = Integer.parseInt("20" + years[1]); // Convert 'YY' to '20YY' format

        if (startYear >= endYear || startYear < 1900 || endYear > 2099) {
            showToast(context, "Academic year is invalid. \nEnsure the start year is less \n than the end year and both years are in a valid range.");
            return false;
        }

        return true;
    }

    // Show Toast messages for errors
    private static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
