-- ================================================
-- SAHARA - Hospital Caregiver Request System
-- Database Schema
-- ================================================

CREATE DATABASE IF NOT EXISTS sahara_db;
USE sahara_db;

-- ------------------------------------------------
-- 1. USER (parent table for all roles)
-- ------------------------------------------------
CREATE TABLE users (
                       user_id       INT AUTO_INCREMENT PRIMARY KEY,
                       full_name     VARCHAR(100) NOT NULL,
                       email         VARCHAR(100) NOT NULL UNIQUE,
                       phone         VARCHAR(15)  NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       role          ENUM('PATIENT', 'CAREGIVER', 'ADMIN') NOT NULL,
                       created_at    DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------
-- 2. ADMIN
-- ------------------------------------------------
CREATE TABLE admins (
                        admin_id  INT AUTO_INCREMENT PRIMARY KEY,
                        user_id   INT NOT NULL UNIQUE,
                        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ------------------------------------------------
-- 3. PATIENT
-- ------------------------------------------------
CREATE TABLE patients (
                          patient_id        INT AUTO_INCREMENT PRIMARY KEY,
                          user_id           INT NOT NULL UNIQUE,
                          gender            ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
                          age               INT NOT NULL,
                          address           VARCHAR(255),
                          emergency_contact VARCHAR(15),
                          FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ------------------------------------------------
-- 4. CAREGIVER
-- ------------------------------------------------
CREATE TABLE caregivers (
                            caregiver_id     INT AUTO_INCREMENT PRIMARY KEY,
                            user_id          INT NOT NULL UNIQUE,
                            gender           ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
                            age              INT NOT NULL,
                            address          VARCHAR(255),
                            experience_years INT DEFAULT 0,
                            bio              TEXT,
                            is_verified      BOOLEAN DEFAULT FALSE,
                            avg_rating       DECIMAL(3,2) DEFAULT 0.00,
                            FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ------------------------------------------------
-- 5. CARE_TIER
-- ------------------------------------------------
CREATE TABLE care_tiers (
                            tier_id       INT AUTO_INCREMENT PRIMARY KEY,
                            tier_name     VARCHAR(50) NOT NULL,
                            description   TEXT,
                            price_per_day DECIMAL(10,2) NOT NULL
);

-- ------------------------------------------------
-- 6. CAREGIVER_TIER (junction table)
-- ------------------------------------------------
CREATE TABLE caregiver_tiers (
                                 caregiver_id INT NOT NULL,
                                 tier_id      INT NOT NULL,
                                 is_qualified BOOLEAN DEFAULT FALSE,
                                 PRIMARY KEY (caregiver_id, tier_id),
                                 FOREIGN KEY (caregiver_id) REFERENCES caregivers(caregiver_id) ON DELETE CASCADE,
                                 FOREIGN KEY (tier_id)      REFERENCES care_tiers(tier_id)      ON DELETE CASCADE
);

-- ------------------------------------------------
-- 7. HOSPITAL
-- ------------------------------------------------
CREATE TABLE hospitals (
                           hospital_id INT AUTO_INCREMENT PRIMARY KEY,
                           name        VARCHAR(150) NOT NULL,
                           address     VARCHAR(255),
                           city        VARCHAR(100)
);

-- ------------------------------------------------
-- 8. BOOKING
-- ------------------------------------------------
CREATE TABLE bookings (
                          booking_id   INT AUTO_INCREMENT PRIMARY KEY,
                          patient_id   INT NOT NULL,
                          caregiver_id INT NOT NULL,
                          tier_id      INT NOT NULL,
                          hospital_id  INT NOT NULL,
                          ward         VARCHAR(100),
                          admission_date  DATE NOT NULL,
                          discharge_date  DATE,
                          total_days      INT DEFAULT 1,
                          total_cost      DECIMAL(10,2),
                          status       ENUM('PENDING','CONFIRMED','ACTIVE','COMPLETED','CANCELLED') DEFAULT 'PENDING',
                          notes        TEXT,
                          booked_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (patient_id)   REFERENCES patients(patient_id)     ON DELETE CASCADE,
                          FOREIGN KEY (caregiver_id) REFERENCES caregivers(caregiver_id) ON DELETE CASCADE,
                          FOREIGN KEY (tier_id)      REFERENCES care_tiers(tier_id)      ON DELETE CASCADE,
                          FOREIGN KEY (hospital_id)  REFERENCES hospitals(hospital_id)   ON DELETE CASCADE
);

-- ------------------------------------------------
-- 9. FEEDBACK
-- ------------------------------------------------
CREATE TABLE feedback (
                          feedback_id  INT AUTO_INCREMENT PRIMARY KEY,
                          booking_id   INT NOT NULL UNIQUE,
                          patient_id   INT NOT NULL,
                          caregiver_id INT NOT NULL,
                          rating       INT CHECK (rating BETWEEN 1 AND 5),
                          review       TEXT,
                          submitted_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (booking_id)   REFERENCES bookings(booking_id)    ON DELETE CASCADE,
                          FOREIGN KEY (patient_id)   REFERENCES patients(patient_id)     ON DELETE CASCADE,
                          FOREIGN KEY (caregiver_id) REFERENCES caregivers(caregiver_id) ON DELETE CASCADE
);

-- ------------------------------------------------
-- 10. AVAILABILITY
-- ------------------------------------------------
CREATE TABLE availability (
                              availability_id INT AUTO_INCREMENT PRIMARY KEY,
                              caregiver_id    INT NOT NULL,
                              available_date  DATE NOT NULL,
                              is_available    BOOLEAN DEFAULT TRUE,
                              FOREIGN KEY (caregiver_id) REFERENCES caregivers(caregiver_id) ON DELETE CASCADE
);

-- ------------------------------------------------
-- 11. NOTIFICATION
-- ------------------------------------------------
CREATE TABLE notifications (
                               notification_id INT AUTO_INCREMENT PRIMARY KEY,
                               user_id         INT NOT NULL,
                               message         TEXT NOT NULL,
                               is_read         BOOLEAN DEFAULT FALSE,
                               created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ------------------------------------------------
-- DEFAULT DATA — Care Tiers
-- ------------------------------------------------
INSERT INTO care_tiers (tier_name, description, price_per_day) VALUES
                                                                   ('Basic Companion Care',    'Light support — company, errands, medication reminders',         1000.00),
                                                                   ('Standard Care',           'Active support — feeding, mobility, grooming, overnight sitting', 1800.00),
                                                                   ('Intensive Personal Care', 'Full hands-on care — hygiene, washroom, bedsore prevention',     3000.00),
                                                                   ('Specialized Support',     'Round-the-clock care for high-dependency patients',               5000.00);

-- ------------------------------------------------
-- DEFAULT ADMIN ACCOUNT
-- email    = admin@sahara.com
-- password = admin123
-- (SHA-256 hash with a 16-byte Base64 salt, in PasswordUtil's "salt:hash" format)
-- ------------------------------------------------
INSERT INTO users (full_name, email, phone, password_hash, role) VALUES
    ('Sahara Admin', 'admin@sahara.com', '9800000000', 'g26tE9lE83kDAvOhlPjSfQ==:8413462061ddd7e012f39010bcb09bad3558566819b9bd61ebbe63704d2ae026', 'ADMIN');

INSERT INTO admins (user_id) VALUES (1);