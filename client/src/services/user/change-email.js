import URLs from '../DEV-URLs';
import ResponseOptions from '../ResponseOptions';
import ErrorHandling from '../ErrorHandling';

export const changeEmail = (userId, newEmail, cbSuccess, cbError) => {
    fetch(URLs.CHANGE_EMAIL(userId, newEmail), ResponseOptions.PATCHResponse())
        .then(res => res.json())
        .then(response => {
            ErrorHandling.simpleErrorHandler(response);
            cbSuccess(response);
        })
        .catch(err => cbError(err));
};