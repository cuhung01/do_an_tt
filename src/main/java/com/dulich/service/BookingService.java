package com.dulich.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import com.dulich.dto.BookingDetailDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dulich.dto.BookingDTO;

public interface BookingService {

	Page<BookingDTO> findAllBooking(Integer trang_thai,String ten_tour,Pageable pageable);

	List<BookingDTO> findBookingByUserId(Long userId);

	Page<BookingDTO> findBookingByTourId(Long tour_Id,Pageable pageable);

	boolean addNewBooking(BookingDTO newBooking);

	boolean approveBooking(Long bookingId,Integer trang_thai);

	boolean deleteBooking(Long id);

	BookingDTO getBookingById(Long id);

	List<BookingDTO> getStatisticsByDate(LocalDate date);
	List<BookingDTO> getStatisticsByMonth(int month, int year);
	List<BookingDTO> getStatisticsByYear(int year);

	List<BookingDTO> findCompleteBookingsByDate(LocalDate date);
	List<BookingDTO> findCompleteBookingsByMonth(int month, int year);
	List<BookingDTO> findCompleteBookingsByYear(int year);

	BookingDetailDTO getBookingDetailById(Long id);

	List<BookingDTO> findCompleteBookings();
	List<BookingDTO> getBookingStatistics();

}
