package com.evfleetmobility.useronboarding.noticificationservices.controller;

import com.evfleetmobility.useronboarding.noticificationservices.dto.NotificationRequest;
import com.evfleetmobility.useronboarding.noticificationservices.entity.Notification;
import com.evfleetmobility.useronboarding.noticificationservices.services.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService service;

    @GetMapping
    public List<Notification> getNotifications(Authentication authentication) {
        Long userId = requireUserId(authentication);
        return service.getAllForUser(userId);
    }

    @PostMapping
    public Notification create(@Valid @RequestBody NotificationRequest request,
                               Authentication authentication) {
        requireUserId(authentication);
        return service.createForReceiver(request.getReceiverId(), request.getTitle(), request.getMessage());
    }

    @PutMapping("/{id}/read")
    public Notification markAsRead(@PathVariable Long id,
                                   Authentication authentication) {
        Long userId = requireUserId(authentication);
        return service.markAsRead(userId, id);
    }

    private Long requireUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("Authentication required.");
        }
        return Long.valueOf(authentication.getName());
    }
}
