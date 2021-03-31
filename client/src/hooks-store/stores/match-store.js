import { initStore } from '../store';
import Match from '../../models/matches/Match';

export const SET_MATCH = "SET_MATCH";
export const UNSET_MATCH = "UNSET_MATCH";

const configureStore = () => {
    const actions = {
        [SET_MATCH]: (curState, matchObj) => {
            return { matchState: { inMatch: true, matchObj: new Match(matchObj) } };
        },
        [UNSET_MATCH]: () => {
            return { matchState: { inMatch: false, matchObj: null } };
        }
    }
    initStore(actions, {
        matchState: {
            inMatch: false, matchObj: null
        }
    });  
}


export default configureStore;



// new Match({
//     privacy: '',
//     gameType: '',
//     matchName: '', game: {
//         players: [{ id: 2, name: "bb", email: "bb", money: 1000, displayedCards: [{ suit: null, rank: null, visibility: "HIDDEN" }, { suit: "DIAMONDS", rank: "THREE", visibility: "REVEALED" },], bet: 0, isDealer: false, status: "WAITING_GAME" },
//         { id: 3, name: "cc", email: "cc", money: 1000, displayedCards: [{ suit: null, rank: null, visibility: "HIDDEN" }, { suit: "HEARTS", rank: "FOUR", visibility: "REVEALED" },], bet: 0, isDealer: false, status: "WAITING_GAME" },
//         { id: 4, name: "dd", email: "dd", money: 1000, displayedCards: [{ suit: null, rank: null, visibility: "HIDDEN" }, { suit: "CLUBS", rank: "FIVE", visibility: "REVEALED" },], bet: 0, isDealer: false, status: "WAITING_GAME" },
//         { id: 5, name: "ee", email: "ee", money: 1000, displayedCards: [{ suit: null, rank: null, visibility: "HIDDEN" }, { suit: "SPADES", rank: "SIX", visibility: "REVEALED" },], bet: 0, isDealer: false, status: "WAITING_GAME" },
//         { id: 6, name: "ff", email: "ff", money: 199, displayedCards: [{ suit: null, rank: null, visibility: "HIDDEN" }, { suit: "HEARTS", rank: "SEVEN", visibility: "REVEALED" },], bet: 0, isDealer: false, status: "WAITING_GAME" },
//         { id: 7, name: "gg", email: "gg", money: 1000, displayedCards: [{ suit: null, rank: null, visibility: "HIDDEN" }, { suit: "DIAMONDS", rank: "EIGHT", visibility: "REVEALED" },], bet: 0, isDealer: false, status: "WAITING_GAME" }
//         ],
//         dealer: { id: 1, name: "aa", email: "aa", money: 1000, displayedCards: [{ suit: null, rank: null, visibility: "HIDDEN" }, { suit: "CLUBS", rank: "ACE11", visibility: "REVEALED" },], bet: 0, isDealer: true, status: "WAITING_GAME" },
//         allPlayersDealerFirst: [
//             { id: 1, name: "aa", email: "aa", money: 1000, displayedCards: [{ suit: null, rank: null, visibility: "HIDDEN" }, { suit: "CLUBS", rank: "ACE11", visibility: "REVEALED" },], bet: 0, isDealer: true, status: "WAITING_GAME" },
//             { id: 2, name: "bb", email: "bb", money: 1000, displayedCards: [{ suit: null, rank: null, visibility: "HIDDEN" }, { suit: "DIAMONDS", rank: "THREE", visibility: "REVEALED" },], bet: 0, isDealer: false, status: "WAITING_GAME" },
//             { id: 3, name: "cc", email: "cc", money: 1000, displayedCards: [{ suit: null, rank: null, visibility: "HIDDEN" }, { suit: "HEARTS", rank: "FOUR", visibility: "REVEALED" },], bet: 0, isDealer: false, status: "WAITING_GAME" },
//             { id: 4, name: "dd", email: "dd", money: 1000, displayedCards: [{ suit: null, rank: null, visibility: "HIDDEN" }, { suit: "CLUBS", rank: "FIVE", visibility: "REVEALED" },], bet: 0, isDealer: false, status: "WAITING_GAME" },
//             { id: 5, name: "ee", email: "ee", money: 1000, displayedCards: [{ suit: null, rank: null, visibility: "HIDDEN" }, { suit: "SPADES", rank: "SIX", visibility: "REVEALED" },], bet: 0, isDealer: false, status: "WAITING_GAME" },
//             { id: 6, name: "ff", email: "ff", money: 199, displayedCards: [{ suit: null, rank: null, visibility: "HIDDEN" }, { suit: "HEARTS", rank: "SEVEN", visibility: "REVEALED" },], bet: 0, isDealer: false, status: "WAITING_GAME" },
//             { id: 7, name: "gg", email: "gg", money: 1000, displayedCards: [{ suit: null, rank: null, visibility: "HIDDEN" }, { suit: "DIAMONDS", rank: "EIGHT", visibility: "REVEALED" },], bet: 0, isDealer: false, status: "WAITING_GAME" },
//         ]
//     }
// })