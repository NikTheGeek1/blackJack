import { initStore } from '../store';
import PlayerChoice from '../../models/matches/PlayerChoice';

export const SET_PLAYER_CHOICE = "SET_PLAYER_CHOICE";
export const UNSET_PLAYER_CHOICE = "UNSET_PLAYER_CHOICE";

const configureStore = () => {
    const actions = {
        [SET_PLAYER_CHOICE]: (curState, playerChoiceObj) => {
            return { playerChoiceState: { playerChoiceObj: new PlayerChoice(playerChoiceObj) }};
        },
        [UNSET_PLAYER_CHOICE]: () => {
            return { playerChoiceState: { playerChoiceObj: null }};
        }
    }
    initStore(actions, { playerChoiceState: { playerChoiceObj: null }}); 
}


export default configureStore;