package com.dulich.service.impl;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import com.dulich.dto.BookingDetailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dulich.dto.BookingDTO;
import com.dulich.dto.TourDTO;
import com.dulich.entity.Booking;
import com.dulich.repository.BookingRepository;
import com.dulich.repository.TourRepository;
import com.dulich.service.BookingService;

@Service
public class BookingServiceImpl implements BookingService{

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private TourRepository tourRepository;




	@Override
	public List<BookingDTO> getStatisticsByDate(LocalDate date) {
		return bookingRepository.countByDate(date);
	}

	@Override
	public List<BookingDTO> getStatisticsByMonth(int month, int year) {
		return bookingRepository.countByMonth(month, year);
	}

	@Override
	public List<BookingDTO> getStatisticsByYear(int year) {
		return bookingRepository.countByYear(year);
	}


	@Override
	public List<BookingDTO> findCompleteBookingsByDate(LocalDate date) {
		return bookingRepository.findStatisticsByDate(date);
	}

	@Override
	public List<BookingDTO> findCompleteBookingsByMonth(int month, int year) {
		return bookingRepository.findStatisticsByMonth(month, year);
	}
	@Override
	public List<BookingDTO> findCompleteBookingsByYear(int year) {
		return bookingRepository.findStatisticsByYear(year);
	}





	@Override
	public List<BookingDTO> getBookingStatistics() {
		// Gọi phương thức của BookingRepository để lấy thống kê booking
		return bookingRepository.findBookingStatistics();
	}



	@Override
	public List<BookingDTO> findCompleteBookings() {
		// Gọi phương thức từ BookingRepository để lấy danh sách booking đã hoàn thành
		return bookingRepository.findCompleteBookings();
	}

	@Override
	public Page<BookingDTO> findAllBooking(Integer trang_thai,String ten_tour,Pageable pageable) {
		return this.bookingRepository.findAllBooking(trang_thai,ten_tour, pageable);
	}

	@Override
	public List<BookingDTO> findBookingByUserId(Long userId) {
		return this.bookingRepository.findBookingByUserId(userId);
	}

	@Override
	public Page<BookingDTO> findBookingByTourId(Long tour_Id,Pageable pageable) {
		return this.bookingRepository.findBookingByTourId(tour_Id, pageable);
	}
	@Override
	public boolean addNewBooking(BookingDTO newBooking) {
		// Kiểm tra xem tour có tồn tại không
		TourDTO tourDTO = this.tourRepository.findTourById(newBooking.getTour_id());
		if (tourDTO == null) {
			return false; // Tour không tồn tại
		}

		// Tạo booking mới
		Booking booking = new Booking();
		booking.setSo_luong_nguoi_lon(newBooking.getSo_luong_nguoi_lon());
		booking.setSo_luong_tre_em_13(newBooking.getSo_luong_tre_em_13());
		booking.setSo_luong_tre_em_46(newBooking.getSo_luong_tre_em_46());
		booking.setNgay_khoi_hanh(newBooking.getNgay_khoi_hanh());
		booking.setTong_tien (tourDTO.getGia_tour()*newBooking.getSo_luong_nguoi_lon()+tourDTO.getGia_tour()*newBooking.getSo_luong_tre_em_46()/2);
		booking.setTour_id(newBooking.getTour_id());
		booking.setUser_id(newBooking.getUser_id());
		booking.setGhi_chu(newBooking.getGhi_chu());
		booking.setPt_thanh_toan(newBooking.getPt_thanh_toan());
		booking.setTrang_thai(0); // Trạng thái mặc định là chờ xử lý

		this.bookingRepository.save(booking);

		return true;
	}


	@SuppressWarnings("deprecation")
	@Override
	public boolean approveBooking(Long bookingId,Integer trang_thai) {
		Optional<Booking> booking  = this.bookingRepository.findById(bookingId);

		if(booking.isPresent()) {
			Booking bookingUpdated = booking.get();
			bookingUpdated.setTrang_thai(trang_thai);
			this.bookingRepository.save(bookingUpdated);
			return true;
		}

		return false;
	}

	@Override
	public boolean deleteBooking(Long id) {

		BookingDTO booking = this.getBookingById(id);

		if(booking.getTrang_thai()==3|| booking.getTrang_thai()==4) {
			this.bookingRepository.deleteById(id);
			return true;
		}

		return false;
	}

	@Override
	public BookingDTO getBookingById(Long id) {
		return this.bookingRepository.findBookingById(id);
	}

	@Override
	public BookingDetailDTO getBookingDetailById(Long id) {
		return this.bookingRepository.findBookingDetailById(id);
	}

}
