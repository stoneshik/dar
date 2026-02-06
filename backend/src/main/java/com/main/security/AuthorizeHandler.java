package com.main.security;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class AuthorizeHandler {
    private final ArrayList<String> sessionIds;
    private final HashMap<String, String> loginsBySessionIds;

    public AuthorizeHandler() {
        sessionIds = new ArrayList<>();
        loginsBySessionIds = new HashMap<>();
    }

    private String extractSessionIdFromHttpRequest(HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        return httpSession.getId();
    }

    private void closeSession(HttpServletRequest httpServletRequest) {
        final HttpSession httpSession = httpServletRequest.getSession();
        httpSession.invalidate();
    }

    private void addSessionId(String sessionId) {
        sessionIds.add(sessionId);
    }

    private void removeSessionId(String sessionId) {
        sessionIds.remove(sessionId);
    }

    public boolean isAuthorized(HttpServletRequest httpServletRequest) {
        final String sessionId = extractSessionIdFromHttpRequest(httpServletRequest);
        return sessionIds.contains(sessionId);
    }

    public boolean newAuth(HttpServletRequest httpServletRequest, String login) {
        final String sessionId = extractSessionIdFromHttpRequest(httpServletRequest);
        if (loginsBySessionIds.containsKey(sessionId)) {
            return false;
        }
        addSessionId(sessionId);
        loginsBySessionIds.put(sessionId, login);
        return true;
    }

    public boolean logout(HttpServletRequest httpServletRequest) {
        final String sessionId = extractSessionIdFromHttpRequest(httpServletRequest);
        if (!loginsBySessionIds.containsKey(sessionId)) {
            return false;
        }
        closeSession(httpServletRequest);
        removeSessionId(sessionId);
        loginsBySessionIds.remove(sessionId);
        return true;
    }

    public String getLoginBySessionId(HttpServletRequest httpServletRequest) {
        final String sessionId = extractSessionIdFromHttpRequest(httpServletRequest);
        if (!loginsBySessionIds.containsKey(sessionId)) {
            return "";
        }
        return loginsBySessionIds.get(sessionId);
    }
}
