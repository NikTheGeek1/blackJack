import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import { useEffect } from 'react';
import { useStore } from '../../hooks-store/store';
import './LandingPage.css';
import { isUserCookieValid, getCookie, USER_COOKIE } from '../../utils/cookie-utils';
import { fetchUserByEmailAndPassword } from '../../services/user/fetch-user';
import signUpORUserInterface from '../../utils/signUpOrUserInterface';
import { LOG_USER_IN } from '../../hooks-store/stores/user-credential-store';

const LandingPage = () => {
    const [globalState, dispatch] = useStore();
    useEffect(() => {
        // checks if cookie is valid, and if it is, 
        // fetches the user using the cookie info 
        // (else it does nothing and signUp is rendered)
        if (!isUserCookieValid()) return;
        const userCookie = getCookie(USER_COOKIE);
        fetchUserByEmailAndPassword(userCookie.email, userCookie.password,
            succResp => dispatch(LOG_USER_IN, succResp),
            errResp => console.log(errResp));
    }, []);

    const slashRouteJSX = signUpORUserInterface(globalState.userState.isLoggedIn);

    return (
        <Router>
            <Switch>
                <Route exact path="/">{slashRouteJSX}</Route>
            </Switch>
        </Router>
    );
};

export default LandingPage;