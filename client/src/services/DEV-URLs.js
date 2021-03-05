class URLs {
    // DEV SERVER
    static DEV_SERVER = "http://localhost:8080";
    
    // WEBSOCKETS MATCHES REST
    static ADD_MATCH = "/websockets/REST/matches/add-match";

    // WEBSOCKETS MATCHES
    static WEBSOCKETS_MATCHES = this.DEV_SERVER + "/websockets/matches";
    static REQUEST_LIST_OF_MATCHES = "/app/websockets/matches/list-of-matches";
    static REPLY_TO_LIST_OF_MATCHES = "/queue/available-matches";

    // WEBSOCKETS LOBBY USERS REST
    static ADD_USER_TO_LOBBY = this.DEV_SERVER + "/websockets/REST/users/add-user-to-lobby";
    static REMOVE_USER_FROM_LOBBY(email){
         return this.DEV_SERVER + `/websockets/REST/users/remove-user-from-lobby?email=${email}`;
    }

    // USER
    static SIGN_USER_UP = this.DEV_SERVER + "/user/sign-up";

    static SIGN_USER_IN(email, password) {
        return this.DEV_SERVER + `/user/sign-in?email=${email}&password=${password}`;
    }
    
}

export default URLs;