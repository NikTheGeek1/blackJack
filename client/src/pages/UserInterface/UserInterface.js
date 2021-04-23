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
import NewMatch from '../../components/NewMatch/NewMatch';
import JoinPrivateMatch from '../../components/UserInterface/JoinPrivateMatch/JoinPrivateMatch';
import ProfileWindow from '../../components/UserInterface/ProfileWindow/ProfileWindow';
import UIBodies from '../../constants/userInterfaceTabItems';
import JoinGame from '../../components/UserInterface/TableTabs/JoinGame/JoinGame';

let matchesSocket;
const UserInterface = () => {

    const [globalState, dispatch] = useStore();
    const [matches, setMatches] = useState([]);
    const [UIBody, setUIBody] = useState(UIBodies.JOIN_GAME);
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

    const joinMatchHandler = (matchName, matchPassword) => {
        // TODO: Add proper validation
        if (!matchName) return;
        if (matchPassword && matchPassword.trim() === '') return;
        addUserToMatch(matchName, matchPassword, globalState.userState.userObj.email,
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

    let UIBodyJSX;
    switch (UIBody) {
        case UIBodies.JOIN_GAME:
            UIBodyJSX = <JoinGame matches={matches} joinMatchHandler={joinMatchHandler}/>;
            break;
        case UIBodies.CREATE_GAME:
            UIBodyJSX = <NewMatch addMatchHandler={addMatchHandler} />;
            break;
        default:
            UIBodyJSX = <h1>something went wrong</h1>
    }

    const tabHandler = tab => {
        setUIBody(UIBodies[tab]);
    }

    const tabsJSX = Object.keys(UIBodies)
        .map(key => {
            const tab = UIBodies[key];
            const selectedTabClass = tab === UIBody && "selected-tab";
            return (
                <li className={"user-interface-tab-item " + selectedTabClass}
                    key={key}
                    onClick={() => tabHandler(key)}
                >
                    {tab}
                </li>
            );
        });

    return (
        // <>
        //     <p>this is the user interface page</p>
        //     <button onClick={() => dispatch(LOG_USER_OUT)}>Logout</button>
        //     <ProfileWindow />
        //     <NewMatch addMatchHandler={addMatchHandler} />
        //     <JoinPrivateMatch joinMatchHandler={joinMatchHandler} />
        //     <p>List of matches</p>
        //     <MatchesTable matches={matches} joinMatchHandler={joinMatchHandler} />
        // </>
        <section className="user-interface-section">
            <div className="user-interface-container">
                <div className="user-interface-header">
                    <ul className="user-interface-tabs-list">
                        {tabsJSX}
                    </ul>
                </div>
                <div className="user-interface-body">
                    {UIBodyJSX}
                </div>
            </div>
        </section>
    );
};

export default UserInterface;