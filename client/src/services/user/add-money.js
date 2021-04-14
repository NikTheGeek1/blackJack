import URLs from '../DEV-URLs';
import ResponseOptions from '../ResponseOptions';
import ErrorHandling from '../ErrorHandling';

export const addMoney = (userEmail, amount, cbSuccess, cbError) => {
    fetch(URLs.ADD_MONEY(userEmail, amount), ResponseOptions.PATCHResponse())
        .then(res => res.json())
        .then(response => {
            ErrorHandling.simpleErrorHandler(response);
            cbSuccess(response);
        })
        .catch(err => cbError(err));
};