package com.sanjeev.celestialsys;

/**
 * Created by HP on 1/15/17.
 */
public final class ValidationUtils {

    public static boolean isEmailValid(String email) {
        return Constants.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}
