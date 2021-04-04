import { useState, useEffect } from 'react';

let globalState = {};
let listeners = [];
let actions = {};

export const useStore = (shouldListen = true) => {
    const setState = useState(globalState)[1];

    const dispatch = (actionIdentifief, payload, shouldNotify = true) => {
        const newState = actions[actionIdentifief](globalState, payload);
        globalState = { ...globalState, ...newState };

        if (shouldNotify) {
            for (let i = 0; i < listeners.length; i++) {
                listeners[i](globalState);
            }
        }
    };

    useEffect(() => {
        if (shouldListen) {
            listeners.push(setState);
        }

        return () => {
            if (shouldListen) {
                listeners = listeners.filter(li => li !== setState);
            }
        };

    }, [setState, shouldListen]);

    return [globalState, dispatch];
};

export const initStore = (userActions, initialState) => {
    if (initialState) {
        globalState = { ...globalState, ...initialState };
    }
    actions = { ...actions, ...userActions };
};
