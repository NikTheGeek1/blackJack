import User from '../../models/users/User';
import { initStore } from '../store';

export const LOG_USER_OUT = "LOG_USER_OUT";
export const LOG_USER_IN = "LOG_USER_IN";
export const UPDATE_USER = "UPDATE_USER";

const configureStore = () => {
    const actions = {
        [LOG_USER_IN]: (curState, userObj) => {
            return { userState: { isLoggedIn: true, userObj: new User(userObj) }};
        },
        [LOG_USER_OUT]: () => {
            return { userState: { isLoggedIn: false, userObj: null }};
        },
        [UPDATE_USER]: (curState, updatedUserObj) => {
            return { userState: { isLoggedIn: true, userObj: new User(updatedUserObj) }};
        }
    }
    initStore(actions, { userState: { isLoggedIn: false, userObj: null } });
}


export default configureStore;