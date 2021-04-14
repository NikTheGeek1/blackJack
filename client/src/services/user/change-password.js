import URLs from '../DEV-URLs';
import ResponseOptions from '../ResponseOptions';
import ErrorHandling from '../ErrorHandling';

export const changePassword = (userEmail, oldPassword, newPassword, cbSuccess, cbError) => {
    fetch(URLs.CHANGE_PASSWORD(userEmail, oldPassword, newPassword), ResponseOptions.PATCHResponse())
        .then(res => res.json())
        .then(response => {
            ErrorHandling.simpleErrorHandler(response);
            cbSuccess(response);
        })
        .catch(err => cbError(err));
};