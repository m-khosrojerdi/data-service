package com.mapnadigital.insights.insights.data.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class WebSocketService extends TextWebSocketHandler {

    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = getUsername(session);
        sessions.put(username, session);
        log.info("User connected: " + username);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("Received from user: %s, payload: %s", getUsername(session), message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = getUsername(session);
        sessions.remove(username);
        System.out.println("User disconnected: " + username);
    }

    public void sendToUser(String username, String msg) throws Exception {
        WebSocketSession session = sessions.get(username);

        if (session == null) {
            throw new Exception("session not found");
        }

        if (session.isOpen()) {
            session.sendMessage(new TextMessage(msg));
        }
    }

    private String getUsername(WebSocketSession session) {
        return (String) session.getAttributes().get("username");
    }
}