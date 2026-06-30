package com.sahara.service;

import com.sahara.dao.CaregiverDAO;
import com.sahara.dao.PatientDAO;
import com.sahara.dao.UserDAO;
import com.sahara.model.Caregiver;
import com.sahara.model.Patient;
import com.sahara.model.User;
import com.sahara.util.PasswordUtil;
import com.sahara.util.SessionManager;

/**
 * AuthService — central place for login & registration logic.
 * Keeps controllers thin: they only handle the screen, this handles the rules.
 */
public class AuthService {

    private final UserDAO userDAO           = new UserDAO();
    private final PatientDAO patientDAO     = new PatientDAO();
    private final CaregiverDAO caregiverDAO = new CaregiverDAO();

    /** Result wrapper so the UI knows what happened and why. */
    public static class Result {
        public final boolean success;
        public final String  message;
        public final String  role;   // only set on a successful login

        private Result(boolean success, String message, String role) {
            this.success = success;
            this.message = message;
            this.role    = role;
        }

        public static Result ok(String message, String role) {
            return new Result(true, message, role);
        }

        public static Result fail(String message) {
            return new Result(false, message, null);
        }
    }

    // ─────────────────────────────────────────────
    // LOGIN — verify credentials and start a session
    // ─────────────────────────────────────────────
    public Result login(String email, String password) {
        if (isBlank(email) || isBlank(password)) {
            return Result.fail("Please enter both email and password.");
        }

        User user = userDAO.getUserByEmail(email.trim());
        if (user == null) {
            return Result.fail("No account found with that email.");
        }

        if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            return Result.fail("Incorrect password.");
        }

        SessionManager.login(user.getUserId(), user.getFullName(),
                user.getEmail(), user.getRole());
        return Result.ok("Welcome back, " + user.getFullName() + "!", user.getRole());
    }

    // ─────────────────────────────────────────────
    // REGISTER — create a user + their role profile
    // role must be "PATIENT" or "CAREGIVER"
    // ─────────────────────────────────────────────
    public Result register(String fullName, String email, String phone,
                           String password, String confirmPassword,
                           String role, String gender, int age, String address) {

        // ── Basic validation ──
        if (isBlank(fullName) || isBlank(email) || isBlank(phone) || isBlank(password)) {
            return Result.fail("Please fill in all required fields.");
        }
        if (!email.contains("@") || !email.contains(".")) {
            return Result.fail("Please enter a valid email address.");
        }
        if (password.length() < 6) {
            return Result.fail("Password must be at least 6 characters.");
        }
        if (!password.equals(confirmPassword)) {
            return Result.fail("Passwords do not match.");
        }
        if (age <= 0 || age > 120) {
            return Result.fail("Please enter a valid age.");
        }
        if (userDAO.emailExists(email.trim())) {
            return Result.fail("An account with that email already exists.");
        }

        // ── Create the user row ──
        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(email.trim());
        user.setPhone(phone.trim());
        user.setPasswordHash(PasswordUtil.hashPassword(password));
        user.setRole(role);

        int userId = userDAO.createUser(user);
        if (userId == -1) {
            return Result.fail("Could not create account. The phone or email may already be in use.");
        }

        // ── Create the matching role profile ──
        boolean profileCreated;
        if ("PATIENT".equals(role)) {
            Patient patient = new Patient();
            patient.setUserId(userId);
            patient.setGender(gender);
            patient.setAge(age);
            patient.setAddress(address);
            profileCreated = patientDAO.createPatient(patient);
        } else if ("CAREGIVER".equals(role)) {
            Caregiver caregiver = new Caregiver();
            caregiver.setUserId(userId);
            caregiver.setGender(gender);
            caregiver.setAge(age);
            caregiver.setAddress(address);
            caregiver.setExperienceYears(0);
            caregiver.setBio("");
            profileCreated = caregiverDAO.createCaregiver(caregiver);
        } else {
            return Result.fail("Invalid role selected.");
        }

        if (!profileCreated) {
            // roll back the orphan user row so a retry can succeed
            userDAO.deleteUser(userId);
            return Result.fail("Could not create your profile. Please try again.");
        }

        return Result.ok("Account created! You can now log in.", role);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
