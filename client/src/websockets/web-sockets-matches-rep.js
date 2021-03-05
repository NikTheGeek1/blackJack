import URLs from "../services/DEV-URLs";
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

let stompClient;
export const initSocket = () => {
    const socket = new SockJS(URLs.WEBSOCKETS_MATCHES);
    stompClient = Stomp.over(socket);
    socket.onopen = function() {
        console.log('Socket connection is open', 'web-sockets-rep.js', 'line: ', '10');
    };

    return stompClient;
};

export const getSocket = () => {
    if (!stompClient) {
        throw new Error("Socket.io is not initialised");
    }
    return stompClient;
};