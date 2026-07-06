package com.sahara.controller;

import com.sahara.dao.BookingDAO;
import com.sahara.dao.CaregiverDAO;
import com.sahara.dao.HospitalDAO;
import com.sahara.dao.PatientDAO;
import com.sahara.model.BookingView;
import com.sahara.model.CaregiverView;
import com.sahara.model.Hospital;
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

    // ── Statistics tab ─────────────────────────────
    @FXML private Label totalPatientsLabel;
    @FXML private Label totalCaregiversLabel;
    @FXML private Label totalBookingsLabel;

    // ── Caregiver table ────────────────────────────
    @FXML private TableView<CaregiverView> caregiverTable;
    @FXML private TableColumn<CaregiverView, String>  cgNameCol;
    @FXML private TableColumn<CaregiverView, String>  cgGenderCol;
    @FXML private TableColumn<CaregiverView, Integer> cgAgeCol;
    @FXML private TableColumn<CaregiverView, Integer> cgExperienceCol;
    @FXML private TableColumn<CaregiverView, Boolean> cgVerifiedCol;

    // ── Bookings table ─────────────────────────────
    @FXML private TableView<BookingView> bookingTable;
    @FXML private TableColumn<BookingView, String> bkPatientCol;
    @FXML private TableColumn<BookingView, String> bkCaregiverCol;
    @FXML private TableColumn<BookingView, String> bkHospitalCol;
    @FXML private TableColumn<BookingView, String> bkWardCol;
    @FXML private TableColumn<BookingView, String> bkStatusCol;
    @FXML private TableColumn<BookingView, Double> bkCostCol;

    // ── Hospitals table ────────────────────────────
    @FXML private TableView<Hospital> hospitalTable;
    @FXML private TableColumn<Hospital, String> hpNameCol;
    @FXML private TableColumn<Hospital, String> hpAddressCol;
    @FXML private TableColumn<Hospital, String> hpCityCol;
    @FXML private TextField hospitalNameField;
    @FXML private TextField hospitalAddressField;
    @FXML private TextField hospitalCityField;

    // ── DAOs ────────────────────────────────────────
    private final PatientDAO   patientDAO   = new PatientDAO();
    private final CaregiverDAO caregiverDAO = new CaregiverDAO();
    private final BookingDAO   bookingDAO   = new BookingDAO();
    private final HospitalDAO  hospitalDAO  = new HospitalDAO();

    @FXML
    private void initialize() {
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + SessionManager.getName());
        }
        setupCaregiverTable();
        setupBookingTable();
        setupHospitalTable();
        refreshStatistics();
        refreshCaregivers();
        refreshBookings();
        refreshHospitals();
    }

    // ─────────────────────────────────────────────
    // STATISTICS
    // ─────────────────────────────────────────────
    private void refreshStatistics() {
        int totalPatients   = patientDAO.getAllPatients().size();
        int totalCaregivers = caregiverDAO.getAllCaregivers().size();
        int totalBookings   = bookingDAO.getAllBookings().size();
        if (totalPatientsLabel   != null) totalPatientsLabel.setText(String.valueOf(totalPatients));
        if (totalCaregiversLabel != null) totalCaregiversLabel.setText(String.valueOf(totalCaregivers));
        if (totalBookingsLabel   != null) totalBookingsLabel.setText(String.valueOf(totalBookings));
    }

    @FXML
    private void handleRefreshStatistics(ActionEvent event) {
        refreshStatistics();
    }

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
        ObservableList<CaregiverView> data =
                FXCollections.observableArrayList(caregiverDAO.getAllCaregiversWithNames());
        caregiverTable.setItems(data);
    }

    @FXML
    private void handleVerifyCaregiver(ActionEvent event) {
        CaregiverView selected = caregiverTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection", "Please select a caregiver to verify.");
            return;
        }
        boolean success = caregiverDAO.verifyCaregiver(selected.getCaregiverId());
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", selected.getFullName() + " has been verified.");
            refreshCaregivers();
            refreshStatistics();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not verify caregiver. Please try again.");
        }
    }

    @FXML
    private void handleRejectCaregiver(ActionEvent event) {
        CaregiverView selected = caregiverTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection", "Please select a caregiver to reject.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Reject and remove " + selected.getFullName() + "? This cannot be undone.",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Rejection");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                // TODO: swap this delete for a REJECTED status update later
                boolean success = caregiverDAO.deleteCaregiver(selected.getCaregiverId());
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Rejected",
                            selected.getFullName() + " has been rejected and removed.");
                    refreshCaregivers();
                    refreshStatistics();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Could not reject caregiver.");
                }
            }
        });
    }

    @FXML
    private void handleRefreshCaregivers(ActionEvent event) {
        refreshCaregivers();
    }

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
        ObservableList<BookingView> data =
                FXCollections.observableArrayList(bookingDAO.getAllBookingsWithNames());
        bookingTable.setItems(data);
    }

    @FXML
    private void handleMarkActive(ActionEvent event) {
        updateSelectedBookingStatus("ACTIVE");
    }

    @FXML
    private void handleMarkCompleted(ActionEvent event) {
        updateSelectedBookingStatus("COMPLETED");
    }

    private void updateSelectedBookingStatus(String status) {
        BookingView selected = bookingTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection", "Please select a booking first.");
            return;
        }
        boolean success = bookingDAO.updateBookingStatus(selected.getBookingId(), status);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Updated",
                    "Booking #" + selected.getBookingId() + " marked as " + status + ".");
            refreshBookings();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not update booking status.");
        }
    }

    @FXML
    private void handleRefreshBookings(ActionEvent event) {
        refreshBookings();
    }

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
        ObservableList<Hospital> data =
                FXCollections.observableArrayList(hospitalDAO.getAllHospitals());
        hospitalTable.setItems(data);
    }

    @FXML
    private void handleAddHospital(ActionEvent event) {
        String name    = hospitalNameField.getText();
        String address = hospitalAddressField.getText();
        String city    = hospitalCityField.getText();

        if (name == null || name.trim().isEmpty()
                || address == null || address.trim().isEmpty()
                || city == null || city.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing info", "Please fill in all hospital fields.");
            return;
        }
        Hospital hospital = new Hospital();
        hospital.setName(name.trim());
        hospital.setAddress(address.trim());
        hospital.setCity(city.trim());

        boolean success = hospitalDAO.createHospital(hospital);
        if (success) {
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
    private void handleDeleteHospital(ActionEvent event) {
        Hospital selected = hospitalTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection", "Please select a hospital to delete.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete " + selected.getName() + "? This cannot be undone.",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Deletion");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                boolean success = hospitalDAO.deleteHospital(selected.getHospitalId());
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Deleted", "Hospital deleted successfully.");
                    refreshHospitals();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Could not delete hospital.");
                }
            }
        });
    }

    @FXML
    private void handleRefreshHospitals(ActionEvent event) {
        refreshHospitals();
    }

    // ─────────────────────────────────────────────
    // LOGOUT
    // ─────────────────────────────────────────────
    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.logout();
        SceneManager.switchScene("/com/sahara/view/Login.fxml", "Login");
    }

    // ─────────────────────────────────────────────
    // HELPER
    // ─────────────────────────────────────────────
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}