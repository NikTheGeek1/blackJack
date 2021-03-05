package com.blackjack.server.websockets.managers;

import com.blackjack.server.models.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ActiveLobbyUsersManager {

    private final HashMap<String, User> lobbyUsers;

    public ActiveLobbyUsersManager() {
        this.lobbyUsers = new HashMap<>();
    }

    public void addLobbyUser(User user) {
        lobbyUsers.put(user.getEmail(), user);
    }

    public User removeLobbyUser(String email) {
        return lobbyUsers.remove(email);
    }

    public HashMap<String, User> getAllLobbyUsers() {
        return lobbyUsers;
    }

    public boolean isUserInList(String email) {
        return lobbyUsers.get(email) != null;
    }



}
