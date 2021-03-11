package com.blackjack.server.websockets.controllers.lobbyUsers;

import com.blackjack.server.controllers.SuccessMessage;
import com.blackjack.server.models.User;
import com.blackjack.server.urls.URLs;
import com.blackjack.server.websockets.managers.ActiveLobbyUsersManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
public class LobbyUsersRestController {

    @Autowired
    private ActiveLobbyUsersManager activeLobbyUsersManager;


    @PostMapping(URLs.ADD_USER_TO_LOBBY)
    private ResponseEntity addUserToLobby(HttpServletRequest request,
                                          @RequestBody User user) {
        activeLobbyUsersManager.addLobbyUser(user);

        HashMap<String, User> lobbyUsers = activeLobbyUsersManager.getAllLobbyUsers();
        for (String email : lobbyUsers.keySet()) {
            System.out.println(email);
        }

        return ResponseEntity.status(HttpStatus.OK).body(new SuccessMessage("User added in the lobby."));
    }


    @PostMapping(URLs.REMOVE_USER_FROM_LOBBY)
    private ResponseEntity removeUserFromLobby(HttpServletRequest request,
                                               @RequestParam(name = "email") String email)
    {
        System.out.println("removing user " + email);
        activeLobbyUsersManager.removeLobbyUser(email);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessMessage("User removed from lobby."));
    }

}
