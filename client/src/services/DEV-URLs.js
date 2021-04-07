class URLs {
    // DEV SERVER
    static DEV_SERVER = "http://192.168.1.2:8080";
    
    // WEBSOCKETS GAME
    static WEBSOCKETS_GAME = this.DEV_SERVER + "/websockets/game";
    static ENTER_GAME (gameName) {
        return `/app/websockets/game/enter-game/${gameName}`;
    }
    static LEAVE_GAME (gameName) {
        return `/app/websockets/game/leave-game/${gameName}`;
    }
    static UPDATE_GAME (gameName) {
        return `/topic/update-game/${gameName}`;
    }
    static START_GAME (gameName, playerEmail) {
        return `/app/websockets/game/start-humans-game/${gameName}/${playerEmail}`;
    }
    static PLACE_BET (gameName) {
        return `/app/websockets/game/place-bet/${gameName}`;
    }
    static STICK (gameName, playerEmail) {
        return `/app/websockets/game/stick/${gameName}/${playerEmail}`;
    }
    static DRAW (gameName, playerEmail) {
        return `/app/websockets/game/draw/${gameName}/${playerEmail}`;
    }
    static PLAYER_CHOICE(gameName) {
        return `/app/websockets/game/player-choice/${gameName}`;
    }

    // WEBSOCKETS CHAT
    static WEBSOCKETS_CHAT = this.DEV_SERVER + "/websockets/chat";
    static SEND_MESSAGE (gameName) {
        return `/app/websockets/chat/send-message/${gameName}`
    }
    static UPDATE_CHAT_HISTORY (gameName) {
        return `/topic/update-chat-history/${gameName}`;
    }
    static LEAVE_CHAT (gameName) {
        return `/app/websockets/chat/leave-chat/${gameName}`;
    }
    static GET_CHAT_HISTORY (gameName) {
        return `/app/websockets/chat/get-chat-history/${gameName}`;
    }

    // WEBSOCKETS MATCHES REST
    static ADD_USER_TO_MATCH (matchName, userEmail) {
        return this.DEV_SERVER + `/websockets/REST/matches/add-user-to-match?matchName=${matchName}&userEmail=${userEmail}`;
    }
    
    static ADD_MATCH (userEmail) {
        return this.DEV_SERVER + `/websockets/REST/matches/add-match?userEmail=${userEmail}`;  
    } 

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