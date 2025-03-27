package com.example.project13.service;

import org.springframework.stereotype.Service;

@Service
public class TestService {

    public String getPublicMessage() {
        return "This is public data (No auth required) 🎉";
    }

    public String getUserMessage() {
        return "User-only content! 🔐 (ROLE_USER required)";
    }

    public String getAdminMessage() {
        return "Admin secret area! 🚨 (ROLE_ADMIN required)";
    }

    public String getCommonMessage() {
        return "Common message for all authenticated users 👋";
    }
}
