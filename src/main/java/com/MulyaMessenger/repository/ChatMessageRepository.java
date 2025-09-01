package com.MulyaMessenger.repository;

import com.MulyaMessenger.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Retrieves private messages exchanged between two users (both directions),
     * filtered by type = 'PRIVATE_MESSAGE', ordered by timeStamp.
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.type = 'PRIVATE_MESSAGE' AND " +
            "((cm.sender = :user1 AND cm.recipient = :user2) OR (cm.sender = :user2 AND cm.recipient = :user1)) " +
            "ORDER BY cm.timeStamp ASC")
    List<ChatMessage> findPrivateMessagesBetweenTwoUsers(
            @Param("user1") String user1,
            @Param("user2") String user2
    );

    /**
     * Retrieves all messages for a specific group, ordered by timeStamp.
     */
    List<ChatMessage> findByGroup_IdOrderByTimeStampAsc(Long groupId);
}
