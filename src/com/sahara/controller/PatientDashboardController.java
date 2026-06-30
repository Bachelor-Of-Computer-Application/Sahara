package com.sahara.controller;

import com.sahara.dao.*;
import com.sahara.model.*;
import com.sahara.util.SceneManager;
import com.sahara.util.SessionManager;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PatientDashboardController {

    @FXML private Label welcomeLabel;

    @FXML private ComboBox<String> tierFilter;
    @FXML private TableView<CaregiverRow> caregiverTable;
    @FXML private TableColumn<CaregiverRow, String>  colName;
    @FXML private TableColumn<CaregiverRow, String>  colGender;
    @FXML private TableColumn<CaregiverRow, Integer> colAge;
    @FXML private TableColumn<CaregiverRow, Integer> colExperience;
    @FXML private TableColumn<CaregiverRow, Double>  colRating;
    @FXML private TableColumn<CaregiverRow, String>  colBio;

    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<BookingRow> bookingTable;
    @FXML private TableColumn<BookingRow, Integer> colBookingId;
    @FXML private TableColumn<BookingRow, String>  colHospital;
    @FXML private TableColumn<BookingRow, String>  colWard;
    @FXML private TableColumn<BookingRow, String>  colAdmission;
    @FXML private TableColumn<BookingRow, Integer> colDays;
    @FXML private TableColumn<BookingRow, Double>  colCost;
    @FXML private TableColumn<BookingRow, String>  colStatus;

    private final CaregiverDAO  caregiverDAO  = new CaregiverDAO();
    private final CareTierDAO   careTierDAO   = new CareTierDAO();
    private final BookingDAO    bookingDAO    = new BookingDAO();
    private final PatientDAO    patientDAO    = new PatientDAO();
    private final HospitalDAO   hospitalDAO   = new HospitalDAO();
    private final UserDAO       userDAO       = new UserDAO();
    private final FeedbackDAO   feedbackDAO   = new FeedbackDAO();

    private List<CareTier>  allTiers;
    private List<Caregiver> currentCaregivers;

    @FXML
    private void initialize() {
        welcomeLabel.setText("Welcome, " + SessionManager.getName());

        colName.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().fullName));
        colGender.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().gender));
        colAge.setCellValueFactory(
                d -> new SimpleIntegerProperty(d.getValue().age).asObject());
        colExperience.setCellValueFactory(
                d -> new SimpleIntegerProperty(
                        d.getValue().experienceYears).asObject());
        colRating.setCellValueFactory(
                d -> new SimpleDoubleProperty(
                        d.getValue().avgRating).asObject());
        colBio.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().bio));

        colBookingId.setCellValueFactory(
                d -> new SimpleIntegerProperty(
                        d.getValue().bookingId).asObject());
        colHospital.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().hospitalName));
        colWard.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().ward));
        colAdmission.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().admissionDate));
        colDays.setCellValueFactory(
                d -> new SimpleIntegerProperty(
                        d.getValue().totalDays).asObject());
        colCost.setCellValueFactory(
                d -> new SimpleDoubleProperty(
                        d.getValue().totalCost).asObject());
        colStatus.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().status));

        allTiers = careTierDAO.getAllTiers();
        tierFilter.getItems().add("All Tiers");
        for (CareTier tier : allTiers) {
            tierFilter.getItems().add(tier.getTierName());
        }
        tierFilter.getSelectionModel().selectFirst();

        statusFilter.getItems().addAll(
                "All", "PENDING", "CONFIRMED",
                "ACTIVE", "COMPLETED", "CANCELLED"
        );
        statusFilter.getSelectionModel().selectFirst();

        loadCaregivers();
        loadBookings();
    }

    private void loadCaregivers() {
        currentCaregivers = caregiverDAO.getVerifiedCaregivers();
        ObservableList<CaregiverRow> rows =
                FXCollections.observableArrayList();
        for (Caregiver c : currentCaregivers) {
            User user = userDAO.getUserById(c.getUserId());
            String name = (user != null) ? user.getFullName() : "Unknown";
            rows.add(new CaregiverRow(
                    c.getCaregiverId(), name, c.getGender(),
                    c.getAge(), c.getExperienceYears(),
                    c.getAvgRating(),
                    c.getBio() != null ? c.getBio() : ""
            ));
        }
        caregiverTable.setItems(rows);
    }

    private void loadBookings() {
        int userId = SessionManager.getUserId();
        Patient patient = patientDAO.getPatientByUserId(userId);
        if (patient == null) return;

        List<Booking> bookings =
                bookingDAO.getBookingsByPatientId(patient.getPatientId());
        ObservableList<BookingRow> rows =
                FXCollections.observableArrayList();
        for (Booking b : bookings) {
            Hospital hospital =
                    hospitalDAO.getHospitalById(b.getHospitalId());
            String hospitalName =
                    (hospital != null) ? hospital.getName() : "Unknown";
            rows.add(new BookingRow(
                    b.getBookingId(), hospitalName, b.getWard(),
                    b.getAdmissionDate() != null ?
                            b.getAdmissionDate().toString() : "",
                    b.getTotalDays(), b.getTotalCost(), b.getStatus()
            ));
        }
        bookingTable.setItems(rows);
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        loadCaregivers();
    }

    @FXML
    private void handleViewProfile(ActionEvent event) {
        CaregiverRow selected =
                caregiverTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a caregiver first!");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Caregiver Profile");
        alert.setHeaderText(selected.fullName);
        alert.setContentText(
                "Gender     : " + selected.gender + "\n" +
                        "Age        : " + selected.age + "\n" +
                        "Experience : " + selected.experienceYears + " years\n" +
                        "Rating     : " + selected.avgRating + " / 5.0\n" +
                        "Bio        : " + selected.bio
        );
        alert.showAndWait();
    }

    @FXML
    private void handleBookCaregiver(ActionEvent event) {
        CaregiverRow selected =
                caregiverTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a caregiver first!");
            return;
        }
        Patient patient =
                patientDAO.getPatientByUserId(SessionManager.getUserId());
        if (patient == null) {
            showAlert("Patient profile not found!");
            return;
        }
        List<Hospital> hospitals = hospitalDAO.getAllHospitals();
        if (hospitals.isEmpty()) {
            showAlert("No hospitals available. Please contact admin.");
            return;
        }

        Dialog<Booking> dialog = new Dialog<>();
        dialog.setTitle("Book Caregiver");
        dialog.setHeaderText("Booking: " + selected.fullName);

        ButtonType bookBtn = new ButtonType(
                "Confirm Booking", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes()
                .addAll(bookBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        ComboBox<String> hospitalBox = new ComboBox<>();
        for (Hospital h : hospitals) hospitalBox.getItems().add(h.getName());
        hospitalBox.getSelectionModel().selectFirst();

        ComboBox<String> tierBox = new ComboBox<>();
        for (CareTier t : allTiers) {
            tierBox.getItems().add(
                    t.getTierName() + " - NPR " + t.getPricePerDay() + "/day");
        }
        tierBox.getSelectionModel().selectFirst();

        TextField wardField = new TextField();
        wardField.setPromptText("e.g. Ward 3");

        DatePicker admissionPicker = new DatePicker(LocalDate.now());
        DatePicker dischargePicker =
                new DatePicker(LocalDate.now().plusDays(1));

        TextField notesField = new TextField();
        notesField.setPromptText("Any special notes...");

        grid.add(new Label("Hospital:"),       0, 0);
        grid.add(hospitalBox,                  1, 0);
        grid.add(new Label("Care Tier:"),      0, 1);
        grid.add(tierBox,                      1, 1);
        grid.add(new Label("Ward:"),           0, 2);
        grid.add(wardField,                    1, 2);
        grid.add(new Label("Admission Date:"), 0, 3);
        grid.add(admissionPicker,              1, 3);
        grid.add(new Label("Discharge Date:"), 0, 4);
        grid.add(dischargePicker,              1, 4);
        grid.add(new Label("Notes:"),          0, 5);
        grid.add(notesField,                   1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == bookBtn) {
                int tierIndex =
                        tierBox.getSelectionModel().getSelectedIndex();
                CareTier chosenTier = allTiers.get(tierIndex);
                int hospitalIndex =
                        hospitalBox.getSelectionModel().getSelectedIndex();
                Hospital chosenHospital = hospitals.get(hospitalIndex);
                LocalDate admDate = admissionPicker.getValue();
                LocalDate disDate = dischargePicker.getValue();
                long days = java.time.temporal.ChronoUnit.DAYS
                        .between(admDate, disDate);
                if (days <= 0) days = 1;
                double totalCost = days * chosenTier.getPricePerDay();

                Booking b = new Booking();
                b.setPatientId(patient.getPatientId());
                b.setCaregiverId(selected.caregiverId);
                b.setTierId(chosenTier.getTierId());
                b.setHospitalId(chosenHospital.getHospitalId());
                b.setWard(wardField.getText());
                b.setAdmissionDate(admDate);
                b.setDischargeDate(disDate);
                b.setTotalDays((int) days);
                b.setTotalCost(totalCost);
                b.setNotes(notesField.getText());
                return b;
            }
            return null;
        });

        Optional<Booking> result = dialog.showAndWait();
        result.ifPresent(booking -> {
            int bookingId = bookingDAO.createBooking(booking);
            if (bookingId != -1) {
                showAlert("Booking confirmed! Your Booking ID: "
                        + bookingId);
                loadBookings();
            } else {
                showAlert("Booking failed. Please try again.");
            }
        });
    }

    @FXML
    private void handleCancelBooking(ActionEvent event) {
        BookingRow selected =
                bookingTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a booking to cancel!");
            return;
        }
        if (selected.status.equals("COMPLETED") ||
                selected.status.equals("CANCELLED")) {
            showAlert("This booking cannot be cancelled.\n" +
                    "Status: " + selected.status);
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Booking");
        confirm.setHeaderText("Cancel Booking #" + selected.bookingId);
        confirm.setContentText(
                "Are you sure you want to cancel?\n" +
                        "Hospital : " + selected.hospitalName + "\n" +
                        "Status   : " + selected.status
        );
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean cancelled = bookingDAO.updateBookingStatus(
                        selected.bookingId, "CANCELLED");
                if (cancelled) {
                    showAlert("Booking #" + selected.bookingId
                            + " cancelled.");
                    loadBookings();
                } else {
                    showAlert("Failed to cancel. Try again.");
                }
            }
        });
    }

    @FXML
    private void handleSubmitFeedback(ActionEvent event) {
        BookingRow selected =
                bookingTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a completed booking first!");
            return;
        }
        if (!selected.status.equals("COMPLETED")) {
            showAlert("You can only submit feedback for COMPLETED bookings.\n"
                    + "This booking is: " + selected.status);
            return;
        }
        if (feedbackDAO.getFeedbackByBookingId(
                selected.bookingId) != null) {
            showAlert("You already submitted feedback for this booking.");
            return;
        }

        Dialog<Feedback> dialog = new Dialog<>();
        dialog.setTitle("Submit Feedback");
        dialog.setHeaderText("Rate your experience - Booking #"
                + selected.bookingId);

        ButtonType submitBtn = new ButtonType(
                "Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes()
                .addAll(submitBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        ComboBox<Integer> ratingBox = new ComboBox<>();
        ratingBox.getItems().addAll(1, 2, 3, 4, 5);
        ratingBox.getSelectionModel().select(4);

        TextArea reviewArea = new TextArea();
        reviewArea.setPromptText("Write your review here...");
        reviewArea.setPrefRowCount(4);
        reviewArea.setWrapText(true);

        grid.add(new Label("Rating (1-5):"), 0, 0);
        grid.add(ratingBox,                  1, 0);
        grid.add(new Label("Review:"),       0, 1);
        grid.add(reviewArea,                 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == submitBtn) {
                Booking booking =
                        bookingDAO.getBookingById(selected.bookingId);
                Patient patient =
                        patientDAO.getPatientByUserId(
                                SessionManager.getUserId());
                Feedback f = new Feedback();
                f.setBookingId(selected.bookingId);
                f.setPatientId(patient.getPatientId());
                f.setCaregiverId(booking.getCaregiverId());
                f.setRating(ratingBox.getValue());
                f.setReview(reviewArea.getText());
                return f;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(feedback -> {
            boolean saved = feedbackDAO.createFeedback(feedback);
            if (saved) {
                showAlert("Thank you for your feedback!");
            } else {
                showAlert("Failed to submit feedback. Try again.");
            }
        });
    }

    @FXML
    private void handleRefreshBookings(ActionEvent event) {
        loadBookings();
    }

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

    public static class CaregiverRow {
        int caregiverId, age, experienceYears;
        String fullName, gender, bio;
        double avgRating;

        CaregiverRow(int id, String name, String gender,
                     int age, int exp, double rating, String bio) {
            this.caregiverId     = id;
            this.fullName        = name;
            this.gender          = gender;
            this.age             = age;
            this.experienceYears = exp;
            this.avgRating       = rating;
            this.bio             = bio;
        }
    }

    public static class BookingRow {
        int bookingId, totalDays;
        String hospitalName, ward, admissionDate, status;
        double totalCost;

        BookingRow(int id, String hospital, String ward,
                   String admission, int days,
                   double cost, String status) {
            this.bookingId     = id;
            this.hospitalName  = hospital;
            this.ward          = ward;
            this.admissionDate = admission;
            this.totalDays     = days;
            this.totalCost     = cost;
            this.status        = status;
        }
    }
}