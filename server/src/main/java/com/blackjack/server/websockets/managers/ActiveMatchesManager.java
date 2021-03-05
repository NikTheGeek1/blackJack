package com.blackjack.server.websockets.managers;

import com.blackjack.server.models.match.Match;
import com.blackjack.server.websockets.interfaces.ActiveMatchesChangeListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ActiveMatchesManager {

    private final HashMap<String, Match> matches;
    private final List<ActiveMatchesChangeListener> listeners;
    private final ThreadPoolExecutor notifyPool;

    private ActiveMatchesManager() {
        matches = new HashMap<>();
        listeners = new CopyOnWriteArrayList<>();
        notifyPool = new ThreadPoolExecutor(1, 5, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));
    }

    public void add(Match match) {
        matches.put(match.getMatchName(), match);
        notifyListeners();
    }

    public void remove(String matchName) {
        matches.remove(matchName);
        notifyListeners();
    }

    public HashMap<String, Match> getAll() {
        return matches;
    }

    public void registerListener(ActiveMatchesChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ActiveMatchesChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        notifyPool.submit(() -> listeners.forEach(ActiveMatchesChangeListener::notifyActiveMatchChange));
    }

}
