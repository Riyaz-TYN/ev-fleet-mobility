package com.evfleetmobility.useronboarding.noticificationservices.services;

import com.evfleetmobility.common.exception.NotificationNotFoundException;
import com.evfleetmobility.common.exception.UserNotFoundException;
import com.evfleetmobility.useronboarding.authservices.entity.User;
import com.evfleetmobility.useronboarding.authservices.repository.UserRepository;
import com.evfleetmobility.useronboarding.noticificationservices.entity.Notification;
import com.evfleetmobility.useronboarding.noticificationservices.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository repository;

    @Autowired
    private UserRepository userRepository;

    public Notification createForReceiver(Long receiverId, String title, String message) {
        User user = userRepository.findById(receiverId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setUser(user);
        return repository.save(notification);
    }

    public List<Notification> getAllForUser(Long userId) {
        return repository.findByUser_IdOrderByCreatedAtDesc(userId);
    }

    public Notification markAsRead(Long userId, Long id) {
        Notification notification = repository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found"));

        notification.setRead(true);
        return repository.save(notification);
    }
}
