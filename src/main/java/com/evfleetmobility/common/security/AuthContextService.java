package com.evfleetmobility.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthContextService {

    public String getCurrentRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !auth.getAuthorities().isEmpty()) {
            String authority = auth.getAuthorities().iterator().next().getAuthority();
            if (authority.startsWith("ROLE_")) {
                return authority.substring(5);
            }
            return authority;
        }
        return null;
    }

    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            try {
                return Long.parseLong(auth.getName());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public String getCurrentSubject() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }
}
