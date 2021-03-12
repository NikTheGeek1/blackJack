import { useStore } from '../../hooks-store/store';
import { SET_MATCH } from '../../hooks-store/stores/match-store';
import { SET_PLAYER } from '../../hooks-store/stores/player-store';
import { LOG_USER_OUT } from '../../hooks-store/stores/user-credential-store';
import './UserInterface.css';
import { initSocket } from '../../websockets/web-sockets-matches-rep';
import { useEffect, useState } from 'react';
import { addMatch, addUserToMatch } from '../../services/websocketsREST/match-services';
import { addLobbyUser, removeLobbyUser } from '../../services/websocketsREST/lobby-user-services';
import URLs from '../../services/DEV-URLs';
import { useHistory } from 'react-router';
import NewMatchForm from '../../components/NewMatch/NewMatchForm/NewMatchForm';

let matchesSocket;

const UserInterface = () => {
    const [globalState, dispatch] = useStore();
    const [matches, setMatches] = useState([]);
    const history = useHistory();

    useEffect(() => {
        matchesSocket = initSocket();
    }, []);

    useEffect(() => {
        let availableMatchesSubscription;
        matchesSocket.connect({}, frame => {
            availableMatchesSubscription = matchesSocket.subscribe(URLs.REPLY_TO_LIST_OF_MATCHES, (msg) => {
                const matchesParsed = JSON.parse(msg.body);
                setMatches(Object.values(matchesParsed));
            });
            matchesSocket.send(URLs.REQUEST_LIST_OF_MATCHES, {}, "requesting list of matches");
        });


        return () => {
            removeLobbyUser(globalState.userState.userObj.email, sucRes => console.log(sucRes), errRes => console.log(errRes));
            availableMatchesSubscription && availableMatchesSubscription.unsubscribe();
            matchesSocket.disconnect();
        };
    }, []);

    useEffect(() => {
        if (matchesSocket) {
            addLobbyUser(globalState.userState.userObj,
                sucRes => console.log(sucRes),
                errorRes => console.log(errorRes)
            );
        }
    }, []);

    const joinMatchHandler = (matchName) => {
        addUserToMatch(matchName, globalState.userState.userObj.email,
            sucRes => goToMatchPage(sucRes),
            errRes => console.log(errRes))
    };

    const addMatchHandler = (match) => {
        addMatch(match, globalState.userState.userObj.email,
            resSucc => goToMatchPage(resSucc),
            resError => console.log(resError))
    };

    const goToMatchPage = (response) => {
        dispatch(SET_MATCH, response.match);
        dispatch(SET_PLAYER, response.player);
        history.push("/match");
    };

    const matchesJSX = matches.map(match => {
        return (
            <div key={match.matchName}>
                <p>{match.matchName} max players: {match.maxNumberOfPlayers} players num: {match.users.length} duration: {match.duration}</p>
                <button onClick={() => joinMatchHandler(match.matchName)}>Join</button>
            </div>
        );
    });

    return (
        <>
            <p>this is the user interface page</p>
            <button onClick={() => dispatch(LOG_USER_OUT)}>Logout</button>
            <NewMatchForm addMatchHandler={addMatchHandler}/>
            <p>
            start working on humans game logic <br/>
            represent players as a table (in MatchGame component) <br/>
            the creator decides when the game starts, if the players num {'>'}= 2<br/>
            </p>
            <p>List of matches</p>
            {matchesJSX}
        </>
    );
};

export default UserInterface;