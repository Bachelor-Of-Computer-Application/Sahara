package com.sahara.controller;

import com.sahara.dao.*;
import com.sahara.model.*;
import com.sahara.util.SceneManager;
import com.sahara.util.SessionManager;
import com.sahara.util.PasswordUtil;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

public class CaregiverDashboardController {

    // ── Header ──────────────────────────────────────────
    @FXML private Label welcomeLabel;
    @FXML private Button notificationBellBtn;

    // ── Tab 1: Booking Requests ────────────────────────
    @FXML private TableView<BookingRow> requestsTable;
    @FXML private TableColumn<BookingRow, Integer> reqColId;
    @FXML private TableColumn<BookingRow, String>  reqColPatient;
    @FXML private TableColumn<BookingRow, String>  reqColHospital;
    @FXML private TableColumn<BookingRow, String>  reqColAdmission;
    @FXML private TableColumn<BookingRow, String>  reqColDischarge;
    @FXML private TableColumn<BookingRow, Double>  reqColCost;

    // ── Tab 2: Active Assignments ──────────────────────
    @FXML private TableView<BookingRow> activeTable;
    @FXML private TableColumn<BookingRow, Integer> actColId;
    @FXML private TableColumn<BookingRow, String>  actColPatient;
    @FXML private TableColumn<BookingRow, String>  actColHospital;
    @FXML private TableColumn<BookingRow, String>  actColAdmission;
    @FXML private TableColumn<BookingRow, String>  actColDischarge;
    @FXML private TableColumn<BookingRow, Double>  actColCost;

    // ── Tab 3: Completed History ───────────────────────
    @FXML private TableView<BookingRow> completedTable;
    @FXML private TableColumn<BookingRow, Integer> compColId;
    @FXML private TableColumn<BookingRow, String>  compColPatient;
    @FXML private TableColumn<BookingRow, String>  compColHospital;
    @FXML private TableColumn<BookingRow, String>  compColAdmission;
    @FXML private TableColumn<BookingRow, String>  compColDischarge;
    @FXML private TableColumn<BookingRow, Double>  compColCost;

    // ── Tab 4: Availability ────────────────────────────
    @FXML private DatePicker availabilityDatePicker;
    @FXML private TableView<AvailabilityRow> availabilityTable;
    @FXML private TableColumn<AvailabilityRow, String> availColDate;
    @FXML private TableColumn<AvailabilityRow, String> availColStatus;

    // ── Tab 5: My Profile ──────────────────────────────
    @FXML private Label profileNameLabel;
    @FXML private Label profileGenderLabel;
    @FXML private Label profileAgeLabel;
    @FXML private Label profileRatingLabel;
    @FXML private TextField profileExperienceField;
    @FXML private TextArea  profileBioArea;
    @FXML private ListView<String> feedbackListView;

    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmNewPasswordField;
    @FXML private Label passwordChangeErrorLabel;

    // ── DAOs ────────────────────────────────────────────
    private final CaregiverDAO    caregiverDAO    = new CaregiverDAO();
    private final BookingDAO      bookingDAO      = new BookingDAO();
    private final PatientDAO      patientDAO      = new PatientDAO();
    private final HospitalDAO     hospitalDAO     = new HospitalDAO();
    private final UserDAO         userDAO         = new UserDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final AvailabilityDAO availabilityDAO = new AvailabilityDAO();
    private final FeedbackDAO     feedbackDAO     = new FeedbackDAO();
    private final CareTierDAO     careTierDAO     = new CareTierDAO();

    private Caregiver caregiver;
    private int caregiverId = -1;

    @FXML
    private void initialize() {
        welcomeLabel.setText("Welcome, " + SessionManager.getName());

        setupRequestsColumns();
        setupActiveColumns();
        setupCompletedColumns();
        setupAvailabilityColumns();

        caregiver = caregiverDAO.getCaregiverByUserId(SessionManager.getUserId());
        if (caregiver == null) {
            showAlert("Caregiver profile not found for this account. Contact admin.");
            return;
        }
        caregiverId = caregiver.getCaregiverId();

        loadRequests();
        loadActive();
        loadCompleted();
        loadAvailability();
        loadProfile();
        refreshNotificationBadge();
    }

    // ══════════════════════════════════════════════════
    //  TAB 1 — BOOKING REQUESTS  (status = PENDING)
    // ══════════════════════════════════════════════════

    private void setupRequestsColumns() {
        reqColId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().bookingId).asObject());
        reqColPatient.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().patientName));
        reqColHospital.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().hospitalName));
        reqColAdmission.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().admissionDate));
        reqColDischarge.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().dischargeDate));
        reqColCost.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().totalCost).asObject());
    }

    private void loadRequests() {
        requestsTable.setItems(loadBookingsByStatus("PENDING"));
    }

    @FXML
    private void handleAccept(ActionEvent event) {
        BookingRow selected = requestsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a booking request first!");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Accept Request");
        confirm.setHeaderText("Accept Booking #" + selected.bookingId + "?");
        confirm.setContentText("You're agreeing to care for " + selected.patientName +
                " at " + selected.hospitalName + ".");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean updated = bookingDAO.updateBookingStatus(selected.bookingId, "CONFIRMED");
                if (updated) {
                    notifyPatient(selected.bookingId, "Your booking #" + selected.bookingId +
                            " has been accepted by the caregiver.");
                    showAlert("Booking #" + selected.bookingId + " accepted.");
                    loadRequests();
                } else {
                    showAlert("Failed to accept booking. Try again.");
                }
            }
        });
    }

    @FXML
    private void handleDecline(ActionEvent event) {
        BookingRow selected = requestsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a booking request first!");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Decline Request");
        confirm.setHeaderText("Decline Booking #" + selected.bookingId);
        confirm.setContentText("Are you sure you want to decline this request?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean updated = bookingDAO.updateBookingStatus(selected.bookingId, "CANCELLED");
                if (updated) {
                    notifyPatient(selected.bookingId, "Your booking #" + selected.bookingId +
                            " was declined by the caregiver.");
                    showAlert("Booking #" + selected.bookingId + " declined.");
                    loadRequests();
                } else {
                    showAlert("Failed to decline booking. Try again.");
                }
            }
        });
    }

    @FXML
    private void handleRefreshRequests(ActionEvent event) {
        loadRequests();
    }

    @FXML
    private void handleViewRequestDetails(ActionEvent event) {
        showBookingDetails(requestsTable.getSelectionModel().getSelectedItem());
    }

    // ══════════════════════════════════════════════════
    //  TAB 2 — ACTIVE ASSIGNMENTS  (status = ACTIVE)
    // ══════════════════════════════════════════════════

    private void setupActiveColumns() {
        actColId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().bookingId).asObject());
        actColPatient.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().patientName));
        actColHospital.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().hospitalName));
        actColAdmission.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().admissionDate));
        actColDischarge.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().dischargeDate));
        actColCost.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().totalCost).asObject());
    }

    private void loadActive() {
        activeTable.setItems(loadBookingsByStatus("ACTIVE"));
    }

    @FXML
    private void handleRefreshActive(ActionEvent event) {
        loadActive();
    }

    @FXML
    private void handleViewActiveDetails(ActionEvent event) {
        showBookingDetails(activeTable.getSelectionModel().getSelectedItem());
    }

    // ══════════════════════════════════════════════════
    //  TAB 3 — COMPLETED HISTORY  (status = COMPLETED)
    // ══════════════════════════════════════════════════

    private void setupCompletedColumns() {
        compColId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().bookingId).asObject());
        compColPatient.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().patientName));
        compColHospital.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().hospitalName));
        compColAdmission.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().admissionDate));
        compColDischarge.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().dischargeDate));
        compColCost.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().totalCost).asObject());
    }

    private void loadCompleted() {
        completedTable.setItems(loadBookingsByStatus("COMPLETED"));
    }

    @FXML
    private void handleRefreshCompleted(ActionEvent event) {
        loadCompleted();
    }

    @FXML
    private void handleViewCompletedDetails(ActionEvent event) {
        showBookingDetails(completedTable.getSelectionModel().getSelectedItem());
    }

    // ── Shared popup used by all 3 booking tabs' "View Details" button ──
    private void showBookingDetails(BookingRow row) {
        if (row == null) {
            showAlert("Please select a booking first!");
            return;
        }
        Booking booking = bookingDAO.getBookingById(row.bookingId);
        if (booking == null) {
            showAlert("Could not load booking details.");
            return;
        }

        CareTier tier = careTierDAO.getTierById(booking.getTierId());
        String tierName = (tier != null) ? tier.getTierName() : "Unknown";

        String notes = (booking.getNotes() != null && !booking.getNotes().isBlank())
                ? booking.getNotes() : "(none)";

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Booking Details");
        alert.setHeaderText("Booking #" + booking.getBookingId());
        alert.setContentText(
                "Patient       : " + row.patientName + "\n" +
                "Hospital      : " + row.hospitalName + "\n" +
                "Ward          : " + booking.getWard() + "\n" +
                "Care Tier     : " + tierName + "\n" +
                "Admission     : " + booking.getAdmissionDate() + "\n" +
                "Discharge     : " + booking.getDischargeDate() + "\n" +
                "Total Days    : " + booking.getTotalDays() + "\n" +
                "Total Cost    : NPR " + booking.getTotalCost() + "\n" +
                "Status        : " + booking.getStatus() + "\n" +
                "Notes         : " + notes
        );
        alert.showAndWait();
    }

    // ── Shared helper used by all 3 booking tabs above ──
    private ObservableList<BookingRow> loadBookingsByStatus(String status) {
        List<Booking> bookings = bookingDAO.getBookingsByCaregiverIdAndStatus(caregiverId, status);
        ObservableList<BookingRow> rows = FXCollections.observableArrayList();
        for (Booking b : bookings) {
            rows.add(toRow(b));
        }
        return rows;
    }

    private BookingRow toRow(Booking b) {
        String patientName = "Unknown";
        Patient patient = patientDAO.getPatientById(b.getPatientId());
        if (patient != null) {
            User user = userDAO.getUserById(patient.getUserId());
            if (user != null) patientName = user.getFullName();
        }
        String hospitalName = "Unknown";
        Hospital hospital = hospitalDAO.getHospitalById(b.getHospitalId());
        if (hospital != null) hospitalName = hospital.getName();

        return new BookingRow(
                b.getBookingId(), patientName, hospitalName,
                b.getAdmissionDate() != null ? b.getAdmissionDate().toString() : "",
                b.getDischargeDate() != null ? b.getDischargeDate().toString() : "",
                b.getTotalCost()
        );
    }

    private void notifyPatient(int bookingId, String message) {
        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null) return;
        Patient patient = patientDAO.getPatientById(booking.getPatientId());
        if (patient == null) return;

        Notification n = new Notification();
        n.setUserId(patient.getUserId());
        n.setMessage(message);
        notificationDAO.createNotification(n);
    }

    // ══════════════════════════════════════════════════
    //  TAB 4 — AVAILABILITY
    // ══════════════════════════════════════════════════

    private void setupAvailabilityColumns() {
        availColDate.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().date));
        availColStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().status));
    }

    private void loadAvailability() {
        List<Availability> list = availabilityDAO.getAvailabilityByCaregiverId(caregiverId);
        ObservableList<AvailabilityRow> rows = FXCollections.observableArrayList();
        for (Availability a : list) {
            rows.add(new AvailabilityRow(
                    a.getAvailabilityId(),
                    a.getAvailableDate().toString(),
                    a.isAvailable() ? "Available" : "Unavailable",
                    a.isAvailable()
            ));
        }
        availabilityTable.setItems(rows);
    }

    // ── UPDATE — toggle a date between Available / Unavailable ──
    @FXML
    private void handleToggleAvailability(ActionEvent event) {
        AvailabilityRow selected = availabilityTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a date first!");
            return;
        }
        boolean newStatus = !selected.isAvailable; // flip it
        boolean updated = availabilityDAO.updateAvailability(selected.availabilityId, newStatus);
        if (updated) {
            showAlert(selected.date + " is now marked " + (newStatus ? "Available" : "Unavailable") + ".");
            loadAvailability();
        } else {
            showAlert("Failed to update. Try again.");
        }
    }

    @FXML
    private void handleAddAvailability(ActionEvent event) {
        LocalDate chosenDate = availabilityDatePicker.getValue();
        if (chosenDate == null) {
            showAlert("Please pick a date first!");
            return;
        }
        if (chosenDate.isBefore(LocalDate.now())) {
            showAlert("You can't add a date in the past.");
            return;
        }

        Availability a = new Availability();
        a.setCaregiverId(caregiverId);
        a.setAvailableDate(chosenDate);
        a.setAvailable(true);

        boolean added = availabilityDAO.addAvailability(a);
        if (added) {
            showAlert("Added " + chosenDate + " to your availability.");
            loadAvailability();
        } else {
            showAlert("Failed to add availability. Try again.");
        }
    }

    @FXML
    private void handleRemoveAvailability(ActionEvent event) {
        AvailabilityRow selected = availabilityTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a date to remove first!");
            return;
        }
        boolean removed = availabilityDAO.deleteAvailability(selected.availabilityId);
        if (removed) {
            showAlert("Removed " + selected.date + " from your availability.");
            loadAvailability();
        } else {
            showAlert("Failed to remove. Try again.");
        }
    }

    @FXML
    private void handleRefreshAvailability(ActionEvent event) {
        loadAvailability();
    }

    // ══════════════════════════════════════════════════
    //  TAB 5 — MY PROFILE
    // ══════════════════════════════════════════════════

    private void loadProfile() {
        User user = userDAO.getUserById(caregiver.getUserId());
        profileNameLabel.setText(user != null ? user.getFullName() : "Unknown");
        profileGenderLabel.setText(caregiver.getGender());
        profileAgeLabel.setText(String.valueOf(caregiver.getAge()));
        profileRatingLabel.setText(String.format("%.1f / 5.0", caregiver.getAvgRating()));
        profileExperienceField.setText(String.valueOf(caregiver.getExperienceYears()));
        profileBioArea.setText(caregiver.getBio() != null ? caregiver.getBio() : "");

        List<Feedback> feedbackList = feedbackDAO.getFeedbackByCaregiverId(caregiverId);
        ObservableList<String> feedbackItems = FXCollections.observableArrayList();
        if (feedbackList.isEmpty()) {
            feedbackItems.add("No feedback yet.");
        } else {
            for (Feedback f : feedbackList) {
                feedbackItems.add("★ " + f.getRating() + "/5 — " + f.getReview());
            }
        }
        feedbackListView.setItems(feedbackItems);
    }

    @FXML
    private void handleSaveProfile(ActionEvent event) {
        int experience;
        try {
            experience = Integer.parseInt(profileExperienceField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Experience years must be a whole number, e.g. 3");
            return;
        }
        if (experience < 0) {
            showAlert("Experience years can't be negative.");
            return;
        }

        caregiver.setExperienceYears(experience);
        caregiver.setBio(profileBioArea.getText());

        boolean saved = caregiverDAO.updateCaregiver(caregiver);
        if (saved) {
            showAlert("Profile updated successfully!");
        } else {
            showAlert("Failed to update profile. Try again.");
        }
    }

    // ── Change Password ───────────────────────────
    @FXML
    private void handleChangePassword(ActionEvent event) {
        passwordChangeErrorLabel.setText("");

        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmNewPasswordField.getText();

        if (currentPassword == null || currentPassword.isEmpty()) {
            passwordChangeErrorLabel.setText("Enter your current password.");
            return;
        }

        User user = userDAO.getUserById(SessionManager.getUserId());
        if (user == null || !PasswordUtil.verifyPassword(currentPassword, user.getPasswordHash())) {
            passwordChangeErrorLabel.setText("Current password is incorrect.");
            return;
        }

        if (newPassword == null || newPassword.length() < 6) {
            passwordChangeErrorLabel.setText("New password must be at least 6 characters.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            passwordChangeErrorLabel.setText("New passwords do not match.");
            return;
        }

        String newHash = PasswordUtil.hashPassword(newPassword);
        boolean updated = userDAO.updatePassword(user.getUserId(), newHash);

        if (updated) {
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmNewPasswordField.clear();
            showAlert("Password changed successfully!");
        } else {
            passwordChangeErrorLabel.setText("Something went wrong. Please try again.");
        }
    }

    // ══════════════════════════════════════════════════
    //  NOTIFICATIONS
    // ══════════════════════════════════════════════════

    private void refreshNotificationBadge() {
        int unread = notificationDAO.getUnreadCount(SessionManager.getUserId());
        notificationBellBtn.setText(unread > 0 ? "🔔 (" + unread + ")" : "🔔");
    }

    @FXML
    private void handleNotifications(ActionEvent event) {
        int userId = SessionManager.getUserId();
        List<Notification> notifications = notificationDAO.getNotificationsByUserId(userId);

        ListView<String> list = new ListView<>();
        ObservableList<String> items = FXCollections.observableArrayList();
        if (notifications.isEmpty()) {
            items.add("You have no notifications yet.");
        } else {
            for (Notification n : notifications) {
                String prefix = n.isRead() ? "⚪" : "🔴";
                items.add(prefix + "  " + n.getMessage());
            }
        }
        list.setItems(items);
        list.setPrefSize(380, 300);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Notifications");
        dialog.setHeaderText("🔴 = unread   ⚪ = read");
        dialog.getDialogPane().setContent(list);

        ButtonType markAllBtn = new ButtonType("Mark All as Read", ButtonBar.ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().addAll(markAllBtn, ButtonType.CLOSE);

        Button markAllButtonNode = (Button) dialog.getDialogPane().lookupButton(markAllBtn);
        markAllButtonNode.setOnAction(e -> notificationDAO.markAllAsRead(userId));

        dialog.showAndWait();
        refreshNotificationBadge();
    }

    // ══════════════════════════════════════════════════
    //  LOGOUT
    // ══════════════════════════════════════════════════

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.logout();
        SceneManager.switchScene("/com/sahara/view/Login.fxml", "Login");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sahara");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ── Row wrapper classes for the TableViews ──────────
    public static class BookingRow {
        int bookingId;
        String patientName, hospitalName, admissionDate, dischargeDate;
        double totalCost;

        BookingRow(int id, String patient, String hospital,
                   String admission, String discharge, double cost) {
            this.bookingId     = id;
            this.patientName   = patient;
            this.hospitalName  = hospital;
            this.admissionDate = admission;
            this.dischargeDate = discharge;
            this.totalCost     = cost;
        }
    }

    public static class AvailabilityRow {
        int availabilityId;
        String date, status;
        boolean isAvailable;

        AvailabilityRow(int id, String date, String status, boolean isAvailable) {
            this.availabilityId = id;
            this.date        = date;
            this.status       = status;
            this.isAvailable  = isAvailable;
        }
    }
}