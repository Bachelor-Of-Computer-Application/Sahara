package com.sahara.model;

import java.time.LocalDateTime;

public class Notification {

    private int notificationId;
    private int userId;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    public Notification() {}

    public Notification(int notificationId, int userId,
                        String message, boolean isRead,
                        LocalDateTime createdAt) {
        this.notificationId = notificationId;
        this.userId         = userId;
        this.message        = message;
        this.isRead         = isRead;
        this.createdAt      = createdAt;
    }

    public int getNotificationId()      { return notificationId; }
    public int getUserId()              { return userId;         }
    public String getMessage()          { return message;        }
    public boolean isRead()             { return isRead;         }
    public LocalDateTime getCreatedAt() { return createdAt;      }

    public void setNotificationId(int notificationId)    { this.notificationId = notificationId; }
    public void setUserId(int userId)                    { this.userId         = userId;         }
    public void setMessage(String message)               { this.message        = message;        }
    public void setRead(boolean isRead)                  { this.isRead         = isRead;         }
    public void setCreatedAt(LocalDateTime createdAt)    { this.createdAt      = createdAt;      }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", userId=" + userId +
                ", message='" + message + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}