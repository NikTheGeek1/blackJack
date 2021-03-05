package com.blackjack.server.urls;

public class URLs {
    // WEBSOCKETS MATCHES REST
    public final static String ADD_MATCH = "/websockets/REST/matches/add-match";

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

    // DEV CLIENT
    public final static String DEV_CLIENT = "http://localhost:3000";

}
