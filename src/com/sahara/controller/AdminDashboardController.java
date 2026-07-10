package com.sahara.controller;

import com.sahara.dao.*;
import com.sahara.model.*;
import com.sahara.service.AuthService;
import com.sahara.util.PasswordUtil;
import com.sahara.util.SceneManager;
import com.sahara.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminDashboardController {

    // ── Top bar ────────────────────────────────────
    @FXML private Label welcomeLabel;

    // ── Statistics ─────────────────────────────────
    @FXML private Label totalPatientsLabel;
    @FXML private Label totalCaregiversLabel;
    @FXML private Label totalBookingsLabel;

    // ── Caregivers ─────────────────────────────────
    @FXML private TableView<CaregiverView> caregiverTable;
    @FXML private TableColumn<CaregiverView, String>  cgNameCol;
    @FXML private TableColumn<CaregiverView, String>  cgGenderCol;
    @FXML private TableColumn<CaregiverView, Integer> cgAgeCol;
    @FXML private TableColumn<CaregiverView, Integer> cgExperienceCol;
    @FXML private TableColumn<CaregiverView, Boolean> cgVerifiedCol;

    // ── Bookings ───────────────────────────────────
    @FXML private TableView<BookingView> bookingTable;
    @FXML private TableColumn<BookingView, String> bkPatientCol;
    @FXML private TableColumn<BookingView, String> bkCaregiverCol;
    @FXML private TableColumn<BookingView, String> bkHospitalCol;
    @FXML private TableColumn<BookingView, String> bkWardCol;
    @FXML private TableColumn<BookingView, String> bkStatusCol;
    @FXML private TableColumn<BookingView, Double> bkCostCol;

    // ── Hospitals ──────────────────────────────────
    @FXML private TableView<Hospital> hospitalTable;
    @FXML private TableColumn<Hospital, String> hpNameCol;
    @FXML private TableColumn<Hospital, String> hpAddressCol;
    @FXML private TableColumn<Hospital, String> hpCityCol;
    @FXML private TextField hospitalNameField;
    @FXML private TextField hospitalAddressField;
    @FXML private TextField hospitalCityField;

    // ── Add Member ─────────────────────────────────
    @FXML private TextField memberNameField;
    @FXML private TextField memberEmailField;
    @FXML private TextField memberPhoneField;
    @FXML private TextField memberPasswordField;
    @FXML private ComboBox<String> memberRoleCombo;
    @FXML private ComboBox<String> memberGenderCombo;
    @FXML private TextField memberAgeField;
    @FXML private TextField memberAddressField;

    // ── Notifications ──────────────────────────────
    @FXML private ComboBox<User> notifUserCombo;
    @FXML private TextField notifMessageField;
    @FXML private TableView<Notification> notifTable;
    @FXML private TableColumn<Notification, Integer> ntUserCol;
    @FXML private TableColumn<Notification, String>  ntMessageCol;
    @FXML private TableColumn<Notification, Boolean> ntReadCol;
    @FXML private TableColumn<Notification, String>  ntTimeCol;

    // ── DAOs ────────────────────────────────────────
    private final PatientDAO      patientDAO      = new PatientDAO();
    private final CaregiverDAO    caregiverDAO    = new CaregiverDAO();
    private final BookingDAO      bookingDAO      = new BookingDAO();
    private final HospitalDAO     hospitalDAO     = new HospitalDAO();
    private final UserDAO         userDAO         = new UserDAO();
    private final AdminDAO        adminDAO        = new AdminDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final AuthService     authService     = new AuthService();

    @FXML
    private void initialize() {
        if (welcomeLabel != null)
            welcomeLabel.setText("Welcome, " + SessionManager.getName());
        setupCaregiverTable();
        setupBookingTable();
        setupHospitalTable();
        setupNotificationTable();
        setupMemberForm();
        refreshStatistics();
        refreshCaregivers();
        refreshBookings();
        refreshHospitals();
        refreshNotifications();
        refreshUserCombo();
    }

    // ─────────────────────────────────────────────
    // STATISTICS
    // ─────────────────────────────────────────────
    private void refreshStatistics() {
        if (totalPatientsLabel != null)
            totalPatientsLabel.setText(String.valueOf(patientDAO.getAllPatients().size()));
        if (totalCaregiversLabel != null)
            totalCaregiversLabel.setText(String.valueOf(caregiverDAO.getAllCaregivers().size()));
        if (totalBookingsLabel != null)
            totalBookingsLabel.setText(String.valueOf(bookingDAO.getAllBookings().size()));
    }

    @FXML private void handleRefreshStatistics(ActionEvent e) { refreshStatistics(); }

    // ─────────────────────────────────────────────
    // CAREGIVERS
    // ─────────────────────────────────────────────
    private void setupCaregiverTable() {
        if (caregiverTable == null) return;
        cgNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        cgGenderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        cgAgeCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        cgExperienceCol.setCellValueFactory(new PropertyValueFactory<>("experienceYears"));
        cgVerifiedCol.setCellValueFactory(new PropertyValueFactory<>("verified"));
    }

    private void refreshCaregivers() {
        if (caregiverTable == null) return;
        caregiverTable.setItems(FXCollections.observableArrayList(
                caregiverDAO.getAllCaregiversWithNames()));
    }

    @FXML
    private void handleVerifyCaregiver(ActionEvent e) {
        CaregiverView selected = caregiverTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection", "Please select a caregiver.");
            return;
        }
        if (caregiverDAO.verifyCaregiver(selected.getCaregiverId())) {
            showAlert(Alert.AlertType.INFORMATION, "Verified", selected.getFullName() + " is now verified.");
            refreshCaregivers();
            refreshStatistics();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not verify caregiver.");
        }
    }

    @FXML
    private void handleRejectCaregiver(ActionEvent e) {
        CaregiverView selected = caregiverTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection", "Please select a caregiver.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Reject and remove " + selected.getFullName() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES) {
                if (caregiverDAO.deleteCaregiver(selected.getCaregiverId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Rejected", selected.getFullName() + " removed.");
                    refreshCaregivers();
                    refreshStatistics();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Could not reject caregiver.");
                }
            }
        });
    }

    @FXML private void handleRefreshCaregivers(ActionEvent e) { refreshCaregivers(); }

    // ─────────────────────────────────────────────
    // BOOKINGS
    // ─────────────────────────────────────────────
    private void setupBookingTable() {
        if (bookingTable == null) return;
        bkPatientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        bkCaregiverCol.setCellValueFactory(new PropertyValueFactory<>("caregiverName"));
        bkHospitalCol.setCellValueFactory(new PropertyValueFactory<>("hospitalName"));
        bkWardCol.setCellValueFactory(new PropertyValueFactory<>("ward"));
        bkStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        bkCostCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
    }

    private void refreshBookings() {
        if (bookingTable == null) return;
        bookingTable.setItems(FXCollections.observableArrayList(
                bookingDAO.getAllBookingsWithNames()));
    }

    @FXML private void handleMarkActive(ActionEvent e)    { updateBookingStatus("ACTIVE");    }
    @FXML private void handleMarkCompleted(ActionEvent e) { updateBookingStatus("COMPLETED"); }

    private void updateBookingStatus(String status) {
        BookingView selected = bookingTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection", "Please select a booking.");
            return;
        }
        if (bookingDAO.updateBookingStatus(selected.getBookingId(), status)) {
            showAlert(Alert.AlertType.INFORMATION, "Updated", "Booking marked as " + status + ".");
            refreshBookings();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not update booking.");
        }
    }

    @FXML private void handleRefreshBookings(ActionEvent e) { refreshBookings(); }

    // ─────────────────────────────────────────────
    // HOSPITALS
    // ─────────────────────────────────────────────
    private void setupHospitalTable() {
        if (hospitalTable == null) return;
        hpNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        hpAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        hpCityCol.setCellValueFactory(new PropertyValueFactory<>("city"));
    }

    private void refreshHospitals() {
        if (hospitalTable == null) return;
        hospitalTable.setItems(FXCollections.observableArrayList(
                hospitalDAO.getAllHospitals()));
    }

    @FXML
    private void handleAddHospital(ActionEvent e) {
        String name    = hospitalNameField.getText();
        String address = hospitalAddressField.getText();
        String city    = hospitalCityField.getText();
        if (isBlank(name) || isBlank(address) || isBlank(city)) {
            showAlert(Alert.AlertType.WARNING, "Missing info", "Please fill in all hospital fields.");
            return;
        }
        Hospital h = new Hospital();
        h.setName(name.trim());
        h.setAddress(address.trim());
        h.setCity(city.trim());
        if (hospitalDAO.createHospital(h)) {
            showAlert(Alert.AlertType.INFORMATION, "Added", "Hospital added successfully.");
            hospitalNameField.clear();
            hospitalAddressField.clear();
            hospitalCityField.clear();
            refreshHospitals();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not add hospital.");
        }
    }

    @FXML
    private void handleDeleteHospital(ActionEvent e) {
        Hospital selected = hospitalTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection", "Please select a hospital.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete " + selected.getName() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES) {
                if (hospitalDAO.deleteHospital(selected.getHospitalId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Deleted", "Hospital deleted.");
                    refreshHospitals();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Could not delete hospital.");
                }
            }
        });
    }

    @FXML private void handleRefreshHospitals(ActionEvent e) { refreshHospitals(); }
    @FXML private void handleRefreshPatients(ActionEvent e) { refreshPatients(); }

    // ─────────────────────────────────────────────
    // ADD MEMBER
    // ─────────────────────────────────────────────
    private void setupMemberForm() {
        if (memberRoleCombo != null)
            memberRoleCombo.setItems(FXCollections.observableArrayList(
                    "PATIENT", "CAREGIVER", "ADMIN"));
        if (memberGenderCombo != null)
            memberGenderCombo.setItems(FXCollections.observableArrayList(
                    "Male", "Female", "Other"));
    }

    @FXML
    private void handleAddMember(ActionEvent e) {
        String name     = memberNameField.getText();
        String email    = memberEmailField.getText();
        String phone    = memberPhoneField.getText();
        String password = memberPasswordField.getText();
        String role     = memberRoleCombo.getValue();
        String gender   = memberGenderCombo.getValue();
        String ageText  = memberAgeField.getText();
        String address  = memberAddressField.getText();

        if (isBlank(name) || isBlank(email) || isBlank(phone) ||
                isBlank(password) || role == null || gender == null ||
                isBlank(ageText) || isBlank(address)) {
            showAlert(Alert.AlertType.WARNING, "Missing info", "Please fill in all fields.");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageText.trim());
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.WARNING, "Invalid age", "Please enter a valid age number.");
            return;
        }

        if ("ADMIN".equals(role)) {
            if (userDAO.emailExists(email.trim())) {
                showAlert(Alert.AlertType.WARNING, "Email exists",
                        "An account with that email already exists.");
                return;
            }
            User user = new User();
            user.setFullName(name.trim());
            user.setEmail(email.trim());
            user.setPhone(phone.trim());
            user.setPasswordHash(PasswordUtil.hashPassword(password));
            user.setRole("ADMIN");
            int userId = userDAO.createUser(user);
            if (userId == -1) {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not create admin account.");
                return;
            }
            Admin admin = new Admin();
            admin.setUserId(userId);
            adminDAO.createAdmin(admin);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Admin account created!");
        } else {
            AuthService.Result result = authService.register(
                    name, email, phone, password, password,
                    role, gender, age, address);
            if (result.success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Member added successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", result.message);
                return;
            }
        }

        memberNameField.clear();
        memberEmailField.clear();
        memberPhoneField.clear();
        memberPasswordField.clear();
        memberRoleCombo.setValue(null);
        memberGenderCombo.setValue(null);
        memberAgeField.clear();
        memberAddressField.clear();
        refreshStatistics();
        refreshUserCombo();
    }

    @FXML
    private void handleClearMemberForm(ActionEvent e) {
        memberNameField.clear();
        memberEmailField.clear();
        memberPhoneField.clear();
        memberPasswordField.clear();
        memberRoleCombo.setValue(null);
        memberGenderCombo.setValue(null);
        memberAgeField.clear();
        memberAddressField.clear();
    }

    // ─────────────────────────────────────────────
    // NOTIFICATIONS
    // ─────────────────────────────────────────────
    private void setupNotificationTable() {
        if (notifTable == null) return;
        ntUserCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        ntMessageCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        ntReadCol.setCellValueFactory(new PropertyValueFactory<>("read"));
        ntTimeCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
    }

    private void refreshUserCombo() {
        if (notifUserCombo == null) return;
        ObservableList<User> users =
                FXCollections.observableArrayList(userDAO.getAllUsers());
        notifUserCombo.setItems(users);
        notifUserCombo.setConverter(new javafx.util.StringConverter<User>() {
            public String toString(User u) {
                return u == null ? "" : u.getFullName() + " (" + u.getRole() + ")";
            }
            public User fromString(String s) { return null; }
        });
    }

    private void refreshNotifications() {
        if (notifTable == null) return;
        ObservableList<Notification> all = FXCollections.observableArrayList();
        for (User u : userDAO.getAllUsers()) {
            all.addAll(notificationDAO.getNotificationsByUserId(u.getUserId()));
        }
        notifTable.setItems(all);
    }

    @FXML
    private void handleSendNotification(ActionEvent e) {
        User selectedUser = notifUserCombo.getValue();
        String message = notifMessageField.getText();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "No user", "Please select a user to notify.");
            return;
        }
        if (isBlank(message)) {
            showAlert(Alert.AlertType.WARNING, "Empty message", "Please type a message.");
            return;
        }
        Notification n = new Notification();
        n.setUserId(selectedUser.getUserId());
        n.setMessage(message.trim());
        if (notificationDAO.createNotification(n)) {
            showAlert(Alert.AlertType.INFORMATION, "Sent",
                    "Notification sent to " + selectedUser.getFullName() + ".");
            notifMessageField.clear();
            notifUserCombo.setValue(null);
            refreshNotifications();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not send notification.");
        }
    }

    @FXML private void handleRefreshNotifications(ActionEvent e) { refreshNotifications(); }

    // ─────────────────────────────────────────────
    // LOGOUT
    // ─────────────────────────────────────────────
    @FXML
    private void handleLogout(ActionEvent e) {
        SessionManager.logout();
        SceneManager.switchScene("/com/sahara/view/Login.fxml", "Login");
    }

    // ─────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}