package com.blackjack.server.urls;

public class URLs {

    // CHAT WEBSOCKETS
    public static final String WEBSOCKETS_CHAT = "/websockets/chat";
    public static String UPDATE_CHAT_HISTORY(String gameName) {
        return "/topic/update-chat-history/" + gameName;
    }

    public static final String SEND_MESSAGE = "/websockets/chat/send-message/{gameName}";
    public static final String LEAVE_CHAT = "/websockets/chat/leave-chat/{gameName}";
    public static final String GET_CHAT_HISTORY = "/websockets/chat/get-chat-history/{gameName}";

    // WEBSOCKETS GAME
    public static final String WEBSOCKETS_GAME = "/websockets/game";
    public static final String ENTER_GAME = "/websockets/game/enter-game/{gameName}";

    public static String UPDATE_GAME (String gameName) {
        return "/topic/update-game/" + gameName;
    }
    public final static String LEAVE_GAME = "/websockets/game/leave-game/{gameName}";
    public final static String START_GAME = "/websockets/game/start-humans-game/{gameName}/{playerEmail}";
    public final static String PLACE_BET = "/websockets/game/place-bet/{gameName}";
    public final static String STICK = "/websockets/game/stick/{gameName}/{playerEmail}";
    public final static String DRAW = "/websockets/game/draw/{gameName}/{playerEmail}";


    // WEBSOCKETS GAME REST

    // WEBSOCKETS MATCHES REST
    public final static String ADD_MATCH = "/websockets/REST/matches/add-match";
    public static final String ADD_USER_TO_MATCH = "/websockets/REST/matches/add-user-to-match";

    // WEBSOCKETS MATCHES
    public final static String WEBSOCKETS_MATCHES = "/websockets/matches";
    public final static String ON_LIST_OF_MATCHES = "/websockets/matches/list-of-matches";
    public final static String REPLY_TO_LIST_OF_MATCHES = "/queue/available-matches";

    // WEBSOCKETS LOBBY USERS REST
    public final static String ADD_USER_TO_LOBBY = "/websockets/REST/users/add-user-to-lobby";
    public final static String REMOVE_USER_FROM_LOBBY = "/websockets/REST/users/remove-user-from-lobby";

    // USER
    public final static String SIGN_USER_IN = "/user/sign-in";
    public final static String SIGN_USER_UP = "/user/sign-up";
    public final static String ADD_MONEY = "/user/add-money";
    public static final String CHANGE_NAME = "/user/change-name";
    public static final String CHANGE_PASSWORD = "/user/change-password";
    public static final String CHANGE_EMAIL = "/user/change-email";

    // DEV CLIENT
    public final static String DEV_CLIENT = "http://localhost:3000";


}
