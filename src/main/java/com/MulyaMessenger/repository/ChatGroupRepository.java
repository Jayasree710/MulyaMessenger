package com.MulyaMessenger.repository;

import com.MulyaMessenger.entity.ChatGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChatGroupRepository extends JpaRepository<ChatGroup, Long> {
    Optional<ChatGroup> findByName(String name);
}

