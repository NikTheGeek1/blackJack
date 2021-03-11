package com.blackjack.server.controllers;

import com.blackjack.server.models.User;
import com.blackjack.server.repositories.UserRepository;
import com.blackjack.server.urls.URLs;
import com.blackjack.server.utils.User.UserValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
