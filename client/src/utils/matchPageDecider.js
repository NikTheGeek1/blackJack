import MatchPage from '../pages/MatchPage/MatchPage';
import { Route } from 'react-router-dom';
const matchPageDecider = (isUserLoggedIn, isUserInMatch) => {
    if (isUserLoggedIn && isUserInMatch)
        return <Route path="/match"><MatchPage /></Route>;
};

export default matchPageDecider;