package com.MulyaMessenger.service;

import com.MulyaMessenger.entity.User;
import com.MulyaMessenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // check if user exists
    public boolean userExists(String userName) {
        return userRepository.findByUserName(userName).isPresent();
    }

    // find user by userName (used in ChatController)
    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found: " + userName));
    }

    // set user online/offline
    public void setUserOnlineStatus(String userName, boolean isOnline) {
        User user = findByUserName(userName);
        user.setOnline(isOnline);
        userRepository.save(user);
    }
}
