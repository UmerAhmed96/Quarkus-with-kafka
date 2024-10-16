package org.lucerna.product.websocket;

import io.vertx.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.jboss.logging.Logger;
import org.lucerna.product.exception.WebSocketException;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/api/websocket")
@ApplicationScoped
public class DataWebSocket {

    private static final Logger LOG = Logger.getLogger(DataWebSocket.class);
    private final Set<Session> sessions = new CopyOnWriteArraySet<>();

    @Inject
    Vertx vertx;

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        LOG.info("Session opened, id: " + session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        LOG.info("Session closed, id: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        LOG.info("Received message: " + message + " from session id: " + session.getId());
        try {
            broadcast("Broadcast: " + message);
        } catch (WebSocketException e) {
            LOG.error("Failed to process message", e);
            try {
                session.getBasicRemote().sendText("Error: Failed to process your message.");
            } catch (IOException ioException) {
                LOG.error("Failed to send error message to session", ioException);
            }
        }
    }

    private void broadcast(String message) {
        vertx.executeBlocking(promise -> {
            sessions.forEach(session -> {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    LOG.error("Error broadcasting message", e);
                    throw new WebSocketException("Failed to broadcast message", e);
                }
            });
            promise.complete();
        }, res -> {
            if (res.failed()) {
                LOG.error("Failed to broadcast message", res.cause());
            }
        });
    }
}
