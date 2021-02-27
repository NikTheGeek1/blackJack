package com.blackjack.server.utils.User;

public class UserValidation {
    public static boolean isPasswordCorrect(String existingPw, String incomingPw) {
        return existingPw.equals(incomingPw);
    }
    public static String makeEmailValid(String incomingEmail) {
        return incomingEmail.trim().toLowerCase();
    }
}
