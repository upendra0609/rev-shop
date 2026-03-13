package com.revshop.service;

import com.revshop.model.Notification;
import com.revshop.model.User;
import com.revshop.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    @Autowired
    private UserRepository userRepository;


    // Send Notification
    public Notification send(User user, String msg) {

        Notification n = new Notification();

        n.setUser(user);
        n.setMessage(msg);
        n.setCreatedAt(LocalDateTime.now());

        user.getNotifications().add(n);

        userRepository.save(user);

        return n;
    }
}
