import { initStore } from '../store';
import Player from '../../models/matches/Player';

export const SET_PLAYER = "SET_PLAYER";
export const UNSET_PLAYER = "UNSET_PLAYER";

const configureStore = () => {
    const actions = {
        [SET_PLAYER]: (curState, playerObj) => {
            return { playerState: { playerObj: new Player(playerObj) }};
        },
        [UNSET_PLAYER]: () => {
            return { playerState: { playerObj: null }};
        }
    }
    initStore(actions, { playerState: { playerObj: null }}); 
};


export default configureStore;


// new Player({name: '', email: 'ff', money: 199, bet: 0 }) 