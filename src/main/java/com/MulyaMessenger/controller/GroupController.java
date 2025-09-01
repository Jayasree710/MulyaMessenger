package com.MulyaMessenger.controller;

import com.MulyaMessenger.entity.ChatGroup;
import com.MulyaMessenger.entity.ChatMessage;
import com.MulyaMessenger.repository.ChatGroupRepository;
import com.MulyaMessenger.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private ChatGroupRepository chatGroupRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @GetMapping("/{groupId}/messages")
    public List<ChatMessage> getGroupMessages(@PathVariable Long groupId) {
        // ✅ Correct method name with timeStamp
        return chatMessageRepository.findByGroup_IdOrderByTimeStampAsc(groupId);
    }

    @PostMapping
    public ChatGroup createGroup(@RequestBody ChatGroup group) {
        return chatGroupRepository.save(group);
    }

    @GetMapping
    public List<ChatGroup> getAllGroups() {
        return chatGroupRepository.findAll();
    }
}
