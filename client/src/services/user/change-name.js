import URLs from '../DEV-URLs';
import ResponseOptions from '../ResponseOptions';
import ErrorHandling from '../ErrorHandling';

export const changeName = (userEmail, newName, cbSuccess, cbError) => {
    fetch(URLs.CHANGE_NAME(userEmail, newName), ResponseOptions.PATCHResponse())
        .then(res => res.json())
        .then(response => {
            ErrorHandling.simpleErrorHandler(response);
            cbSuccess(response);
        })
        .catch(err => cbError(err));
};