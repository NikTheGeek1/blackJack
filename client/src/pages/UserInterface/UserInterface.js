import { useStore } from '../../hooks-store/store';
import { LOG_USER_OUT } from '../../hooks-store/stores/user-credential-store';
import './UserInterface.css';
import { initSocket } from '../../websockets/web-sockets-matches-rep';
import { useEffect, useState } from 'react';
import { addLobbyUser, removeLobbyUser } from '../../services/websocketsREST/lobby-user-services';
import URLs from '../../services/DEV-URLs';

let matchesSocket;

const UserInterface = () => {
    const [globalState, dispatch] = useStore();
    
    useEffect(() => {
        matchesSocket = initSocket();
    }, []);

    useEffect(() => {
        let availableMatchesSubscription;
        matchesSocket.connect({}, frame => {
            availableMatchesSubscription = matchesSocket.subscribe(URLs.REPLY_TO_LIST_OF_MATCHES, (msg) => {
                console.log(msg, 'UserInterface.js', 'line: ', '19');
            });
            matchesSocket.send(URLs.REQUEST_LIST_OF_MATCHES, {}, "requesting list of matches");
        });


        return () => {
            removeLobbyUser(globalState.userState.userObj.email, succRes => console.log(succRes), errRes => console.log(errRes));
            availableMatchesSubscription && availableMatchesSubscription.unsubscribe();
            matchesSocket.disconnect();
        };
    }, []);

    useEffect(() => {
        if (matchesSocket) {
            addLobbyUser(globalState.userState.userObj,
                succRes => console.log(succRes),
                errorRes => console.log(errorRes)
                );
        }
    }, []);

    return (
        <>
            <p>this is the user interface page</p>
            <button onClick={() => dispatch(LOG_USER_OUT)}>Logout</button>
        </>
    );
};

export default UserInterface;