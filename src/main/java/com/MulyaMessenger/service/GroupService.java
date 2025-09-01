package com.MulyaMessenger.service;

import com.MulyaMessenger.entity.ChatGroup;
import com.MulyaMessenger.entity.GroupMember;
import com.MulyaMessenger.entity.User;
import com.MulyaMessenger.repository.ChatGroupRepository;
import com.MulyaMessenger.repository.GroupMemberRepository;
import com.MulyaMessenger.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class GroupService {

    private final ChatGroupRepository groupRepo;
    private final GroupMemberRepository memberRepo;
    private final UserRepository userRepo;

    public GroupService(ChatGroupRepository g, GroupMemberRepository m, UserRepository u) {
        this.groupRepo = g;
        this.memberRepo = m;
        this.userRepo = u;
    }

    public ChatGroup createGroup(String name) {
        ChatGroup g = new ChatGroup();
        g.setName(name);
        return groupRepo.save(g);
    }

    public GroupMember addMember(Long groupId, Long empId) {
        ChatGroup g = groupRepo.findById(groupId).orElseThrow();
        User u = userRepo.findById(empId).orElseThrow(); // ✅ empId is the PK in User

        // ✅ updated repository call
        if (memberRepo.existsByGroup_IdAndUser_EmpId(groupId, empId)) return null;

        GroupMember gm = new GroupMember();
        gm.setGroup(g);
        gm.setUser(u);
        return memberRepo.save(gm);
    }

    public boolean isMember(Long groupId, Long empId) {
        // ✅ updated repository call
        return memberRepo.existsByGroup_IdAndUser_EmpId(groupId, empId);
    }

    public ChatGroup getGroup(Long id) {
        return groupRepo.findById(id).orElseThrow();
    }

    public List<GroupMember> members(Long groupId) {
        return memberRepo.findByGroup_Id(groupId);
    }
}
