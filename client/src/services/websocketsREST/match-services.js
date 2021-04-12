import URLs from '../DEV-URLs';
import ResponseOptions from '../ResponseOptions';
import ErrorHandling from '../ErrorHandling';

export const addMatch = (match, userEmail, cbSuccess, cbError) => {
    fetch(URLs.ADD_MATCH(userEmail, match.matchPassword), ResponseOptions.POSTResponse(match))
        .then(res => res.json())
        .then(response => {
            ErrorHandling.simpleErrorHandler(response);
            cbSuccess(response);
        })
        .catch(err => cbError(err));
};

export const addUserToMatch = (matchName, matchPassword, userEmail, cbSuccess, cbError) => {
    fetch(URLs.ADD_USER_TO_MATCH(matchName, userEmail, matchPassword), ResponseOptions.POSTResponse())
        .then(res => res.json())
        .then(response => {
            ErrorHandling.simpleErrorHandler(response);
            cbSuccess(response);
        })
        .catch(err => cbError(err));
};
