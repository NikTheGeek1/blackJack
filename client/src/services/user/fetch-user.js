import URLs from '../DEV-URLs';
import ResponseOptions from '../ResponseOptions';
import ErrorHandling from '../ErrorHandling';

export const fetchUserByEmailAndPassword = (email, password, cbSuccess, cbError) => {
    fetch(URLs.SIGN_USER_IN(email, password), ResponseOptions.POSTResponse())
        .then(res => res.json())
        .then(response => {
            ErrorHandling.simpleErrorHandler(response);
            cbSuccess(response);
        })
        .catch(err => cbError(err));
};