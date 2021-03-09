import { initStore } from '../store';

export const SET_MATCH = "SET_MATCH";
export const UNSET_MATCH = "UNSET_MATCH";

const configureStore = () => {
    const actions = {
        [SET_MATCH]: (curState, matchObj) => {
            return { matchState: { inMatch: true, matchObj: matchObj }};
        },
        [UNSET_MATCH]: () => {
            return { matchState: { inMatch: false, matchObj: null }};
        }
    }
    initStore(actions, { matchState: { inMatch: false, matchObj: null }});
}


export default configureStore;