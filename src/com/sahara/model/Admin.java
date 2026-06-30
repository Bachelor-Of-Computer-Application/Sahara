package com.sahara.model;

public class Admin {

    private int adminId;
    private int userId;

    public Admin() {}

    public Admin(int adminId, int userId) {
        this.adminId = adminId;
        this.userId  = userId;
    }

    public int getAdminId() { return adminId; }
    public int getUserId()  { return userId;  }

    public void setAdminId(int adminId) { this.adminId = adminId; }
    public void setUserId(int userId)   { this.userId  = userId;  }

    @Override
    public String toString() {
        return "Admin{adminId=" + adminId + ", userId=" + userId + '}';
    }
}