package com.sahara.model;

import java.time.LocalDateTime;

public class Feedback {

    private int feedbackId;
    private int bookingId;
    private int patientId;
    private int caregiverId;
    private int rating;
    private String review;
    private LocalDateTime submittedAt;

    public Feedback() {}

    public Feedback(int feedbackId, int bookingId, int patientId,
                    int caregiverId, int rating, String review,
                    LocalDateTime submittedAt) {
        this.feedbackId  = feedbackId;
        this.bookingId   = bookingId;
        this.patientId   = patientId;
        this.caregiverId = caregiverId;
        this.rating      = rating;
        this.review      = review;
        this.submittedAt = submittedAt;
    }

    public int getFeedbackId()          { return feedbackId;  }
    public int getBookingId()           { return bookingId;   }
    public int getPatientId()           { return patientId;   }
    public int getCaregiverId()         { return caregiverId; }
    public int getRating()              { return rating;      }
    public String getReview()           { return review;      }
    public LocalDateTime getSubmittedAt(){ return submittedAt; }

    public void setFeedbackId(int feedbackId)          { this.feedbackId  = feedbackId;  }
    public void setBookingId(int bookingId)            { this.bookingId   = bookingId;   }
    public void setPatientId(int patientId)            { this.patientId   = patientId;   }
    public void setCaregiverId(int caregiverId)        { this.caregiverId = caregiverId; }
    public void setRating(int rating)                  { this.rating      = rating;      }
    public void setReview(String review)               { this.review      = review;      }
    public void setSubmittedAt(LocalDateTime submitted){ this.submittedAt = submitted;   }

    @Override
    public String toString() {
        return "Feedback{" +
                "feedbackId=" + feedbackId +
                ", bookingId=" + bookingId +
                ", rating=" + rating +
                '}';
    }
}