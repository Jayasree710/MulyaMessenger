package com.MulyaMessenger.repository;

import com.MulyaMessenger.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // match entity field name "userName"
    Optional<User> findByUserName(String userName);
}
