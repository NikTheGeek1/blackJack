import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import { useStore } from '../../hooks-store/store';
import './LandingPage.css';
import { isUserCookieValid, getCookie, USER_COOKIE } from '../../utils/cookie-utils';
import { fetchUserByEmailAndPassword } from '../../services/user/fetch-user';
import signUpORUserInterfaceDecider from '../../utils/signUpOrUserInterface';
import { LOG_USER_IN } from '../../hooks-store/stores/user-credential-store';
import matchPageDecider from '../../utils/matchPageDecider';
import MatchPage from '../../pages/MatchPage/MatchPage';

const LandingPage = () => {
    const [globalState, dispatch] = useStore();

    // CHANGE THIS LOGIC WHEN YOU IMPLEMENT COOKIES LOGIC
    // useEffect(() => {
    //     // checks if cookie is valid, and if it is, 
    //     // fetches the user using the cookie info 
    //     // (else it does nothing and signUp is rendered)
    //     if (!isUserCookieValid()) return;
    //     const userCookie = getCookie(USER_COOKIE);
    //     fetchUserByEmailAndPassword(userCookie.email, userCookie.password,
    //         succResp => dispatch(LOG_USER_IN, succResp),
    //         errResp => console.log(errResp));
    // }, []);

    const slashRouteJSX = signUpORUserInterfaceDecider(globalState.userState.isLoggedIn);
    const slashMatchRouteJSX = matchPageDecider(globalState.userState.isLoggedIn, globalState.matchState.inMatch);
// TODO: REMOVE /match Route and uncomment slachMatchRouteJSX. Also remove import for MatchPage
    return (
        <Router>
            <Switch>
                <Route exact path="/">{slashRouteJSX}</Route>
                <Route path="/match"><MatchPage /></Route>; 
                {/* {slashMatchRouteJSX} */}
            </Switch>
        </Router>
    );
};

export default LandingPage;