import { initStore } from '../store';

export const SET_PLAYER = "SET_PLAYER";
export const UNSET_PLAYER = "UNSET_PLAYER";

const configureStore = () => {
    const actions = {
        [SET_PLAYER]: (curState, playerObj) => {
            return { playerState: { playerObj: playerObj }};
        },
        [UNSET_PLAYER]: () => {
            return { playerState: { playerObj: null }};
        }
    }
    initStore(actions, { playerState: { playerObj: null }});
}


export default configureStore;