package com.sahara.model;

import java.time.LocalDateTime;

public class User {

    private int userId;
    private String fullName;
    private String email;
    private String phone;
    private String passwordHash;
    private String role;
    private LocalDateTime createdAt;

    public User() {}

    public User(int userId, String fullName, String email,
                String phone, String passwordHash,
                String role, LocalDateTime createdAt) {
        this.userId       = userId;
        this.fullName     = fullName;
        this.email        = email;
        this.phone        = phone;
        this.passwordHash = passwordHash;
        this.role         = role;
        this.createdAt    = createdAt;
    }

    public int getUserId()              { return userId;       }
    public String getFullName()         { return fullName;     }
    public String getEmail()            { return email;        }
    public String getPhone()            { return phone;        }
    public String getPasswordHash()     { return passwordHash; }
    public String getRole()             { return role;         }
    public LocalDateTime getCreatedAt() { return createdAt;    }

    public void setUserId(int userId)            { this.userId       = userId;   }
    public void setFullName(String fullName)     { this.fullName     = fullName; }
    public void setEmail(String email)           { this.email        = email;    }
    public void setPhone(String phone)           { this.phone        = phone;    }
    public void setPasswordHash(String hash)     { this.passwordHash = hash;     }
    public void setRole(String role)             { this.role         = role;     }
    public void setCreatedAt(LocalDateTime date) { this.createdAt    = date;     }

    @Override
    public String toString() {
        return "User{userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' + '}';
    }
}