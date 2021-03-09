package com.blackjack.server.websockets.controllers.game;

import com.blackjack.server.controllers.ErrorMessage;
import com.blackjack.server.controllers.SuccessMessage;
import com.blackjack.server.models.User;
import com.blackjack.server.models.game.Player;
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
        System.out.println(activeMatchesManager.getAll());
        if (activeMatchesManager.getAll().get(match.getMatchName()) != null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Game with that name already exists."));
        User user = activeLobbyUsersManager.removeLobbyUser(userEmail);
        if (user == null) // null because it might be already removed from lobby since they left lobby
            user = userRepository.findByEmail(userEmail);
        Player player = new Player(user);
        match.addPlayer(player);
        activeMatchesManager.add(match);
        System.out.println("Match " + match.getMatchName() + " added");
        HashMap<String, Object> response = new HashMap<>();
        response.put("match", match);
        response.put("player", player);
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
        Player player = new Player(user);
        match.addPlayer(player);
        HashMap<String, Object> response = new HashMap<>();
        response.put("match", match);
        response.put("player", player);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(URLs.REMOVE_USER_FROM_MATCH)
    public ResponseEntity removeUserFromMatch(@RequestParam("userEmail") String userEmail,
                                         @RequestParam("matchName") String matchName) {
        Match match = activeMatchesManager.getMatch(matchName);
        Player player = match.removePlayer(userEmail);
        System.out.println("removing user from match");
        if (match.isEmpty()) {
            activeMatchesManager.remove(matchName);
        }
//      User user = new User(player);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessMessage("User " + userEmail + " removed from match " + matchName));
    }



//
//    @PostMapping("/websck/user-disconnect")
//    public String userDisconnect(@RequestParam("userName") String userName) {
//        System.out.println(userName + " DISCONNECTED");
//        activeMatchesManager.remove(userName);
//        return "disconnected";
//    }
//
//    @GetMapping("/websck/active-users-except/{userName}")
//    public HashMap<String, HashMap<String, String>> getActiveUsersEsceptCurrentUser(@PathVariable String userName) {
//        return activeMatchesManager.getActiveUsersExceptCurrentUser(userName);
//    }
//
//    @GetMapping("/websck/get-admin")
//    public HashMap<String, HashMap<String, String>> getAdmin() {
//        return activeMatchesManager.getAdmin();
//    }
}
