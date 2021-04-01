package com.blackjack.server.utils.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class TokenUtils {
    public static HashMap<String, Integer> moneyToTokens(int money) {
        if (money < 0) throw new Error("Money have a negative value");
        LinkedHashMap<String, Integer> results = new LinkedHashMap<>();
        results.put("1", 0);
        results.put("10", 0);
        results.put("50", 0);
        results.put("100", 0);
        results.put("200", 0);
        results.put("500", 0);
        int restMoney = money;
        ArrayList<String> tokenColumns = new ArrayList(results.keySet());
        if (money > 5000) {
            // if we have more than 5k, give priority to bigger tokens (start loop opposite)
            Collections.sort(tokenColumns, Collections.reverseOrder());
        }
        while (restMoney > 0) {
            for (String tokenColumn : tokenColumns) {
                if ((restMoney - Integer.parseInt(tokenColumn)) >= 0) {
                    restMoney -= Integer.parseInt(tokenColumn);
                    results.put(tokenColumn, results.get(tokenColumn) + 1);
                }
            }
        }
        return results;
    }

    public static int tokensToMoney(HashMap<String, Integer> tokens) {
        int total = 0;
        for (String tokenColumn : tokens.keySet()) {
            total += tokens.get(tokenColumn) * Integer.parseInt(tokenColumn);
        }
        return total;
    }
}
