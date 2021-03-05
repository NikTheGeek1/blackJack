package com.blackjack.server.websockets.controllers.game;

import com.blackjack.server.models.match.Match;
import com.blackjack.server.urls.URLs;
import com.blackjack.server.websockets.managers.ActiveMatchesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class MatchesConnectionRestController {

    @Autowired
    private ActiveMatchesManager activeMatchesManager;

    @PostMapping(URLs.ADD_MATCH)
    public ResponseEntity addMatch(HttpServletRequest request, @RequestBody Match match) {
        if (activeMatchesManager.getAll().get(match.getMatchName()) != null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game with that name already exists.");
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("Remote_Addr");
            if (StringUtils.isEmpty(remoteAddr)) {
                remoteAddr = request.getHeader("X-FORWARDED-FOR");
                if (remoteAddr == null || "".equals(remoteAddr)) {
                    remoteAddr = request.getRemoteAddr();
                }
            }
        }
        match.setRemoteAddr(remoteAddr);
        activeMatchesManager.add(match);
        return ResponseEntity.status(HttpStatus.OK).body(remoteAddr);
    }



//
//    @PostMapping("/websck/user-disconnect")
//    public String userDisconnect(@RequestParam("userName") String userName) {
//        System.out.println(userName + " DISCONNECTED");
//        activeMatchesManager.remove(userName);
//        return "disconnected";
//    }
//
//    @GetMapping("/websck/active-users-except/{userName}")
//    public HashMap<String, HashMap<String, String>> getActiveUsersEsceptCurrentUser(@PathVariable String userName) {
//        return activeMatchesManager.getActiveUsersExceptCurrentUser(userName);
//    }
//
//    @GetMapping("/websck/get-admin")
//    public HashMap<String, HashMap<String, String>> getAdmin() {
//        return activeMatchesManager.getAdmin();
//    }
}
