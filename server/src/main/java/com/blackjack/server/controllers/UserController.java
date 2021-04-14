package com.blackjack.server.controllers;

import com.blackjack.server.models.User;
import com.blackjack.server.repositories.UserRepository;
import com.blackjack.server.urls.URLs;
import com.blackjack.server.utils.User.UserValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @PostMapping(URLs.SIGN_USER_IN)
    public ResponseEntity signUserIn(
            @RequestParam(name = "email") String email,
            @RequestParam(name = "password") String password) {
        User user = userRepository.findByEmail(UserValidation.normaliseEmail(email));
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage("Email does not exist."));
        if (!UserValidation.isPasswordCorrect(user.getPassword(), password))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Wrong password."));
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping(URLs.SIGN_USER_UP)
    public ResponseEntity signUserUp(@RequestBody User user) {
        try {
            user.setMoney(1000);
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("User with that email already exists."));
        }
    }

    @PatchMapping(URLs.ADD_MONEY)
    public ResponseEntity addMoney(
            @RequestParam("userEmail") String userEmail,
            @RequestParam("amount") int amount
    ) {

        try {
            userRepository.increaseMoneyByEmail(amount, userEmail);
            User user = userRepository.findByEmail(userEmail);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage(e.getMessage()));
        }
    }

    @PatchMapping(URLs.CHANGE_NAME)
    public ResponseEntity changeName(
            @RequestParam("userEmail") String userEmail,
            @RequestParam("newName") String name
    ) {
        try {
            userRepository.updateNameByEmail(name, userEmail);
            User user = userRepository.findByEmail(userEmail);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage(e.getMessage()));
        }
    }

    @PatchMapping(URLs.CHANGE_PASSWORD)
    public ResponseEntity changePassword(
            @RequestParam("userEmail") String userEmail,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("oldPassword") String oldPassword
    ) {
        try {
            User user = userRepository.findByEmail(userEmail);
            if (!UserValidation.isPasswordCorrect(user.getPassword(), oldPassword)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Old password is incorrect"));
            }
            userRepository.updatePasswordByEmail(newPassword, userEmail);
            user.setPassword(newPassword);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage(e.getMessage()));
        }
    }

    @PatchMapping(URLs.CHANGE_EMAIL)
    public ResponseEntity changeEmail(
            @RequestParam("userId") Long userId,
            @RequestParam("newEmail") String newEmail
    ) {
        try {
            if (userRepository.existsByEmail(newEmail)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Email already in use."));
            }
            userRepository.updateEmailById(newEmail, userId);
            User user = userRepository.findByEmail(newEmail);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage(e.getMessage()));
        }
    }



}
