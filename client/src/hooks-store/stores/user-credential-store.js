import { initStore } from '../store';

export const LOG_USER_OUT = "LOG_USER_OUT";
export const LOG_USER_IN = "LOG_USER_IN";

const configureStore = () => {
    const actions = {
        [LOG_USER_IN]: (curState, userObj) => {
            console.log(userObj, 'user-credential-store.js', 'line: ', '9');
            return { userState: { isLoggedIn: true, userObj: userObj }};
        },
        [LOG_USER_OUT]: () => {
            return { userState: { isLoggedIn: false, userObj: null }};
        }
    }
    initStore(actions, { userState: { isLoggedIn: false, userObj: null } });
}


export default configureStore;