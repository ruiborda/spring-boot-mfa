package com.example.springbootmfa.service;

import com.example.springbootmfa.model.User;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public interface UserService {
    User createUser(User user);
    List<User> getAllUsers();
    User getUserById(UUID id);
    User updateUser(UUID id, User user);
    void deleteUser(UUID id);
}