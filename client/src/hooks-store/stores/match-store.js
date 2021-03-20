import { initStore } from '../store';
import Match from '../../models/matches/Match';

export const SET_MATCH = "SET_MATCH";
export const UNSET_MATCH = "UNSET_MATCH";

const configureStore = () => {
    const actions = {
        [SET_MATCH]: (curState, matchObj) => {
            return { matchState: { inMatch: true, matchObj: new Match(matchObj) }};
        },
        [UNSET_MATCH]: () => {
            return { matchState: { inMatch: false, matchObj: null }};
        }
    }
    initStore(actions, { matchState: { inMatch: false, matchObj: {matchName: ''} }});  // TODO: set matchObj to null after done with front-end dev
}


export default configureStore;