package com.MulyaMessenger.listener;

import com.MulyaMessenger.entity.ChatMessage;
import com.MulyaMessenger.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

@Component
@Slf4j
public class WebSocketListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketListener.class);

    @Autowired
    UserService userService;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;



    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // extract username from CONNECT headers
        String userName = headerAccessor.getFirstNativeHeader("userName");

        if (userName != null) {
            headerAccessor.getSessionAttributes().put("userName", userName); // 🔥 store in session
            userService.setUserOnlineStatus(userName, true);
            System.out.println("User connected: " + userName);
        } else {
            System.out.println("User connected without username header!");
        }
    }


    @EventListener
    public void handleWebsocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        String userName = null;

        if (sessionAttributes != null) {
            Object userAttr = sessionAttributes.get("userName");
            if (userAttr != null) {
                userName = userAttr.toString();
            }
        }

        if (userName != null) {
            userService.setUserOnlineStatus(userName, false);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(userName);
            simpMessagingTemplate.convertAndSend("/topic/public", chatMessage);

            System.out.println("User disconnected from websocket: " + userName);
        } else {
            System.out.println("Disconnect event fired, but no userName found in session attributes");
        }
    }
}
