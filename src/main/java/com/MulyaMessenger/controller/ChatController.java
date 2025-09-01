package com.MulyaMessenger.controller;

import com.MulyaMessenger.entity.ChatMessage;
import com.MulyaMessenger.repository.ChatMessageRepository;
import com.MulyaMessenger.service.GroupService;
import com.MulyaMessenger.service.UserService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

@Controller
public class ChatController {

    UserService userService;

    ChatMessageRepository chatRepository;

    private SimpMessagingTemplate messagingTemplate;

    private final GroupService groupService;

    public ChatController(UserService userService,
                          ChatMessageRepository chatRepository,
                          SimpMessagingTemplate messagingTemplate,
                          GroupService groupService) {
        this.userService = userService;
        this.chatRepository = chatRepository;
        this.messagingTemplate = messagingTemplate;
        this.groupService = groupService;
    }

    //Add User
    @MessageMapping("/chat.addUser") //websocket destination
    @SendTo("/topic/public") //channel
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor){
        if(userService.userExists(chatMessage.getSender())){
            //store username in session
            headerAccessor.getSessionAttributes().put("userName", chatMessage.getSender());
            userService.setUserOnlineStatus(chatMessage.getSender(), true);

            System.out.println("user added successfully "+chatMessage.getSender()+" with session id"
            +headerAccessor.getSessionId());

            chatMessage.setTimeStamp(LocalDateTime.now());
            if(chatMessage.getContent()==null){
                chatMessage.setContent("");
            }
            return chatRepository.save(chatMessage);
        }
        return null;
    }

    //public message
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage){
        System.out.println("Received message from: " + chatMessage.getSender() + ", content: " + chatMessage.getContent());
        if(userService.userExists(chatMessage.getSender())){
             if(chatMessage.getTimeStamp()==null){
                 chatMessage.setTimeStamp(LocalDateTime.now());
             }

             if(chatMessage.getContent()==null){
                 chatMessage.setContent("");
             }
             return chatRepository.save(chatMessage);
        }
        return null;
    }

    //private Message
    @MessageMapping("/chat.setPrivateMessage")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage) {
        if(userService.userExists(chatMessage.getSender()) && userService.userExists(chatMessage.getRecipient())) {
             if(chatMessage.getTimeStamp() == null) {
                 chatMessage.setTimeStamp(LocalDateTime.now());
             }
             if(chatMessage.getContent() == null) {
                 chatMessage.setContent("");
             }

        chatMessage.setType(ChatMessage.MessageType.PRIVATE_MESSAGE);

        ChatMessage saveMessage = chatRepository.save(chatMessage);
        System.out.println("message saved successfully " + saveMessage.getId());

        try {
            messagingTemplate.convertAndSend("/queue/private/" + chatMessage.getRecipient(), saveMessage);
            messagingTemplate.convertAndSend("/queue/private/" + chatMessage.getSender(), saveMessage);
        } catch (Exception e) {
            System.out.println("Error occurred while sending the message " + e.getMessage());
            e.printStackTrace();
        }
    } else {
        System.out.println("Error: Sender " + chatMessage.getSender() + " or recipient " + chatMessage.getRecipient() + " does not exist");
    }
}

    //Group Message
    //Group Message
    @MessageMapping("/group/{groupId}/send")
    public void sendToGroup(@DestinationVariable Long groupId,
                            @Payload ChatMessage message,
                            Principal principal) {

        String sender = principal != null ? principal.getName() : message.getSender();

        // updated method name
        Long senderId = userService.findByUserName(sender).getEmpId();

        if (!groupService.isMember(groupId, senderId)) {
            throw new org.springframework.messaging.MessageDeliveryException("Not a member of this group");
        }

        if (message.getTimeStamp() == null) {
            message.setTimeStamp(LocalDateTime.now());
        }

        message.setGroup(groupService.getGroup(groupId));
        ChatMessage savedMessage = chatRepository.save(message);

        // broadcast to all subscribers of this group
        messagingTemplate.convertAndSend("/topic/group." + groupId, savedMessage);
    }

}
