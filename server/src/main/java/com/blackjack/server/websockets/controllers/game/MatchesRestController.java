package com.blackjack.server.websockets.controllers.game;

import com.blackjack.server.controllers.ErrorMessage;
import com.blackjack.server.models.User;
import com.blackjack.server.models.match.Match;
import com.blackjack.server.repositories.UserRepository;
import com.blackjack.server.urls.URLs;
import com.blackjack.server.websockets.managers.ActiveLobbyUsersManager;
import com.blackjack.server.websockets.managers.ActiveMatchesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class MatchesRestController {

    @Autowired
    private ActiveMatchesManager activeMatchesManager;

    @Autowired
    private ActiveLobbyUsersManager activeLobbyUsersManager;

    @Autowired
    private UserRepository userRepository;

    @PostMapping(URLs.ADD_MATCH)
    public ResponseEntity addMatch(@RequestBody Match match, @RequestParam("userEmail") String userEmail) {
        if (activeMatchesManager.getAll().get(match.getMatchName()) != null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Game with that name already exists."));
        User user = activeLobbyUsersManager.removeLobbyUser(userEmail);
        if (user == null) // null because it might be already removed from lobby since they left lobby
            user = userRepository.findByEmail(userEmail);
        activeMatchesManager.add(match);
        match.addUser(user);
        HashMap<String, Object> response = new HashMap<>();
        response.put("match", match);
        response.put("player", user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(URLs.ADD_USER_TO_MATCH)
    public ResponseEntity addUserToMatch(@RequestParam("userEmail") String userEmail,
                                         @RequestParam("matchName") String matchName) {
        Match match = activeMatchesManager.getMatch(matchName);
        if (!match.hasSpace())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Room is full."));
        User user = activeLobbyUsersManager.removeLobbyUser(userEmail);
        if (user == null) // null because it might be already removed from lobby since they left lobby
            user = userRepository.findByEmail(userEmail);
        match.addUser(user);
        HashMap<String, Object> response = new HashMap<>();
        response.put("match", match);
        response.put("player", user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
