package com.blackjack.server.utils.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class TokenUtilsTest {

    @Test
    void moneyToTokens100() {
        Integer money = 100;
        HashMap<String, Integer> tokens = TokenUtils.moneyToTokens(money);
        HashMap<String, Integer> expected = new HashMap<>();
        expected.put("1", 10);
        expected.put("10", 4);
        expected.put("50", 1);
        expected.put("100", 0);
        expected.put("200", 0);
        expected.put("500", 0);
        assertEquals(expected, tokens);
    }

    @Test
    void moneyToTokens1000() {
        Integer money = 1000;
        HashMap<String, Integer> tokens = TokenUtils.moneyToTokens(money);
        HashMap<String, Integer> expected = new HashMap<>();
        expected.put("1", 10);
        expected.put("10", 4);
        expected.put("50", 3);
        expected.put("100", 1);
        expected.put("200", 1);
        expected.put("500", 1);
        assertEquals(expected, tokens);
    }

    @Test
    void moneyToTokens500() {
        Integer money = 5000;
        HashMap<String, Integer> tokens = TokenUtils.moneyToTokens(money);
        HashMap<String, Integer> expected = new HashMap<>();
        expected.put("1", 10);
        expected.put("10", 9);
        expected.put("50", 8);
        expected.put("100", 8);
        expected.put("200", 6);
        expected.put("500", 5);
        assertEquals(expected, tokens);
    }

    @Test
    void moneyToTokens5001() {
        Integer money = 5001;
        HashMap<String, Integer> tokens = TokenUtils.moneyToTokens(money);
        HashMap<String, Integer> expected = new HashMap<>();
        expected.put("1", 11);
        expected.put("10", 9);
        expected.put("50", 6);
        expected.put("100", 6);
        expected.put("200", 5);
        expected.put("500", 6);
        assertEquals(expected, tokens);
    }


    @Test
    void tokensToMoney100() {
        HashMap<String, Integer> tokens = new HashMap<>();
        tokens.put("1", 10);
        tokens.put("10", 4);
        tokens.put("50", 1);
        tokens.put("100", 0);
        tokens.put("200", 0);
        tokens.put("500", 0);
        Integer expected = 100;
        assertEquals(expected, TokenUtils.tokensToMoney(tokens));
    }


    @Test
    void tokensToMoney1000() {
        HashMap<String, Integer> tokens = new HashMap<>();
        tokens.put("1", 10);
        tokens.put("10", 4);
        tokens.put("50", 3);
        tokens.put("100", 1);
        tokens.put("200", 1);
        tokens.put("500", 1);
        Integer expected = 1000;
        assertEquals(expected, TokenUtils.tokensToMoney(tokens));
    }

    @Test
    void tokensToMoney5000() {
        HashMap<String, Integer> tokens = new HashMap<>();
        tokens.put("1", 10);
        tokens.put("10", 9);
        tokens.put("50", 8);
        tokens.put("100", 8);
        tokens.put("200", 6);
        tokens.put("500", 5);
        Integer expected = 5000;
        assertEquals(expected, TokenUtils.tokensToMoney(tokens));
    }

    @Test
    void tokensToMoney5001() {
        HashMap<String, Integer> tokens = new HashMap<>();
        tokens.put("1", 11);
        tokens.put("10", 9);
        tokens.put("50", 6);
        tokens.put("100", 6);
        tokens.put("200", 5);
        tokens.put("500", 6);
        int expected = 5001;
        assertEquals(expected, TokenUtils.tokensToMoney(tokens));
    }



}