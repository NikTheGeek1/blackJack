import URLs from '../DEV-URLs';
import ErrorHandling from '../ErrorHandling';
import ResponseOptions from '../ResponseOptions';

export const createUser = (userObj, cbSuccess, cbError) => {
    fetch(URLs.SIGN_USER_UP, ResponseOptions.POSTResponse(userObj))
    .then(res => res.json())
    .then(response => {
        ErrorHandling.simpleErrorHandler(response);
        cbSuccess(response)
    })
    .catch(err => cbError(err));
};