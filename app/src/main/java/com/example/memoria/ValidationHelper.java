package com.example.memoria;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

public class ValidationHelper {

    // Define constants for validation
    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 10;
    private static final int PHONE_LENGTH = 10;
    private static final int MIN_PASSWORD_LENGTH = 8;

    public static boolean validateInputs(Context context, String name, String email, String password, String phone) {
        if (!isValidName(context, name)) return false;
        if (!isValidEmail(context, email)) return false;
        if (!isValidPassword(context, password)) return false;
        if (!isValidPhone(context, phone)) return false;

        return true;
    }

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

    private static boolean isValidEmail(Context context, String email) {
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast(context, "Please enter a valid email address");
            return false;
        }
        return true;
    }

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

    private static boolean isValidPhone(Context context, String phone) {
        if (TextUtils.isEmpty(phone) || phone.length() != PHONE_LENGTH || !TextUtils.isDigitsOnly(phone)) {
            showToast(context, "Please enter a valid " + PHONE_LENGTH + "-digit phone number");
            return false;
        }
        return true;
    }

    private static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

}
