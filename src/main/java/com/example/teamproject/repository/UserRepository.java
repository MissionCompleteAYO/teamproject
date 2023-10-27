package com.example.teamproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.teamproject.model.User;
import java.util.Optional;

<<<<<<< HEAD
=======


>>>>>>> 87561eaa2f63df4555bc2836e4eab85f403f18dd
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNum(String phoneNum);
}
