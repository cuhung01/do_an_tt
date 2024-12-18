package com.dulich.dto;

import java.util.List;

public class BookingStatisticsDTO {
    private List<BookingDTO> bookingStatistics;
    private List<BookingDTO> completedBookings;

    // Constructor, getters, and setters
    public BookingStatisticsDTO(List<BookingDTO> bookingStatistics, List<BookingDTO> completedBookings) {
        this.bookingStatistics = bookingStatistics;
        this.completedBookings = completedBookings;
    }

    // Getters and Setters
    public List<BookingDTO> getBookingStatistics() {
        return bookingStatistics;
    }

    public void setBookingStatistics(List<BookingDTO> bookingStatistics) {
        this.bookingStatistics = bookingStatistics;
    }

    public List<BookingDTO> getCompletedBookings() {
        return completedBookings;
    }

    public void setCompletedBookings(List<BookingDTO> completedBookings) {
        this.completedBookings = completedBookings;
    }
}
