package com.sahara.util;

public class SessionManager {

    private static int loggedInUserId   = -1;
    private static String loggedInEmail = null;
    private static String loggedInRole  = null;
    private static String loggedInName  = null;

    // ── Save user info after login ────────────────
    public static void login(int userId, String name,
                             String email, String role) {
        loggedInUserId = userId;
        loggedInName   = name;
        loggedInEmail  = email;
        loggedInRole   = role;
        System.out.println("Session started for: " + name + " (" + role + ")");
    }

    // ── Clear session on logout ───────────────────
    public static void logout() {
        loggedInUserId = -1;
        loggedInName   = null;
        loggedInEmail  = null;
        loggedInRole   = null;
        System.out.println("Session ended.");
    }

    // ── Getters ───────────────────────────────────
    public static int    getUserId() { return loggedInUserId; }
    public static String getName()   { return loggedInName;   }
    public static String getEmail()  { return loggedInEmail;  }
    public static String getRole()   { return loggedInRole;   }

    // ── Check if anyone is logged in ─────────────
    public static boolean isLoggedIn() {
        return loggedInUserId != -1;
    }

    // ── Check specific role ───────────────────────
    public static boolean isPatient()   { return "PATIENT".equals(loggedInRole);   }
    public static boolean isCaregiver() { return "CAREGIVER".equals(loggedInRole); }
    public static boolean isAdmin()     { return "ADMIN".equals(loggedInRole);     }
}