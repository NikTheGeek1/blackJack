import { initStore } from '../store';
import { storeUserCookie, removeUserCookie } from '../../utils/cookie-utils';

export const LOG_USER_OUT = "LOG_USER_OUT";
export const LOG_USER_IN = "LOG_USER_IN";

const configureStore = () => {
    const actions = {
        [LOG_USER_IN]: (curState, userObj) => {
            storeUserCookie(userObj.email, userObj.password);
            return { userState: { isLoggedIn: true, userObj: userObj }};
        },
        [LOG_USER_OUT]: () => {
            removeUserCookie();
            return { userState: { isLoggedIn: false, userObj: null }};
        }
    }
    initStore(actions, { userState: { isLoggedIn: false, userObj: null } });
}


export default configureStore;