package com.MulyaMessenger.repository;

import com.MulyaMessenger.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    // ✅ corrected method
    boolean existsByGroup_IdAndUser_EmpId(Long groupId, Long empId);

    List<GroupMember> findByGroup_Id(Long groupId);
}
