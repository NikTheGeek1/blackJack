package com.blackjack.server.utils.User;

public class UserValidation {
    public static boolean isPasswordCorrect(String existingPw, String incomingPw) {
        return existingPw.equals(incomingPw);
    }

    public static boolean isValidPassword(String password) {
        // TODO: check if incoming string is a valid password
        return false;
    }

    public static boolean isValidEmail (String email) {
        // TODO: check if incoming string has the valid form of an email
        return false;
    }


    public static String normaliseEmail (String incomingEmail) {
        return incomingEmail.trim().toLowerCase();
    }
}
