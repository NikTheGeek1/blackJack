package com.blackjack.server.controllers;

import com.blackjack.server.models.User;
import com.blackjack.server.repositories.UserRepository;
import com.blackjack.server.utils.User.UserValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @PostMapping("/sign-in")
    public ResponseEntity signUserIn(
            @RequestParam(name = "email") String email,
            @RequestParam(name = "password") String password) {
        User user = userRepository.findByEmail(UserValidation.makeEmailValid(email));
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email does not exist.");
        if (!UserValidation.isPasswordCorrect(user.getPassword(), password))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong password");
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping("/sign-up")
    public ResponseEntity signUserUp(@RequestBody User user) {
//        try {
            userRepository.save(user);
//        } catch () {

//        }

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }


}
