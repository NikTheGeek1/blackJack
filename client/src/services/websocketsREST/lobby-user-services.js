import URLs from '../DEV-URLs';
import ResponseOptions from '../ResponseOptions'
import ErrorHandling from '../ErrorHandling';

export const addLobbyUser = (user, cbSuccess, cbError) => {
    fetch(URLs.ADD_USER_TO_LOBBY, ResponseOptions.POSTResponse(user))
    .then(res => res.json())
    .then(response => {
        ErrorHandling.simpleErrorHandler(response);
        cbSuccess(response);
    })
    .catch(err => cbError(err));
};

export const removeLobbyUser = (email, cbSuccess, cbError) => {
    fetch(URLs.REMOVE_USER_FROM_LOBBY(email), ResponseOptions.POSTResponse())
    .then(res => res.json())
    .then(response => {
        ErrorHandling.simpleErrorHandler(response);
        cbSuccess(response);
    })
    .catch(err => cbError(err));
};