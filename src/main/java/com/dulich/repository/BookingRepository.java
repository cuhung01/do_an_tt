package com.dulich.repository;

import com.dulich.dto.BookingDetailDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dulich.dto.BookingDTO;
import com.dulich.entity.Booking;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

	@Query("SELECT new com.dulich.dto.BookingDTO(b.id, b.user_id, b.tour_id, t.ten_tour, " +
			"SUM(b.so_luong_nguoi_lon + b.so_luong_tre_em_13 + b.so_luong_tre_em_46) ,"
			+ "b.trang_thai, b.booking_at, SUM(b.tong_tien) ,SUM(b.tong_tien/5))" +
			"FROM Booking b JOIN Tour t ON b.tour_id = t.id " +
			"WHERE b.trang_thai IS NOT NULL AND b.booking_at IS NOT NULL " +
			"GROUP BY b.tour_id, b.trang_thai, DATE(b.booking_at)")
	List<BookingDTO> findBookingStatistics();

	@Query("SELECT new com.dulich.dto.BookingDTO(b.trang_thai, " +
			"SUM(CASE " +
			"        WHEN b.trang_thai = 3 THEN b.tong_tien " +
			"        WHEN b.trang_thai IN (1, 2) THEN b.tong_tien / 5 " +
			"        ELSE 0 " +
			"    END)) " +
			"FROM Booking b " +
			"WHERE b.booking_at IS NOT NULL " )

	List<BookingDTO> findCompleteBookings();

	// Thống kê tổng số người và tổng tiền theo ngày
	@Query("SELECT new com.dulich.dto.BookingDTO(b.id, b.user_id, b.tour_id, t.ten_tour, SUM(b.so_luong_nguoi_lon + b.so_luong_tre_em_13 + b.so_luong_tre_em_46), b.trang_thai, b.booking_at, SUM(b.tong_tien), SUM(b.tong_tien/5)) " +
	"FROM Booking b JOIN Tour t ON b.tour_id = t.id " + "WHERE DATE(b.booking_at) = :date GROUP BY b.booking_at")
	List<BookingDTO> countByDate(@Param("date") LocalDate date);

	// Thống kê tổng số người và tổng tiền theo tháng
	@Query("SELECT new com.dulich.dto.BookingDTO(b.id, b.user_id, b.tour_id, t.ten_tour, SUM(b.so_luong_nguoi_lon + b.so_luong_tre_em_13 + b.so_luong_tre_em_46), b.trang_thai, b.booking_at, SUM(b.tong_tien), SUM(b.tong_tien/5)) " +
			"FROM Booking b JOIN Tour t ON b.tour_id = t.id " + "WHERE MONTH(b.booking_at) = :month AND YEAR(b.booking_at) = :year GROUP BY b.booking_at")
	List<BookingDTO> countByMonth(@Param("month") int month, @Param("year") int year);

	// Thống kê tổng số người và tổng tiền theo năm
	@Query("SELECT new com.dulich.dto.BookingDTO(b.id, b.user_id, b.tour_id, t.ten_tour, SUM(b.so_luong_nguoi_lon + b.so_luong_tre_em_13 + b.so_luong_tre_em_46), b.trang_thai, b.booking_at, SUM(b.tong_tien), SUM(b.tong_tien/5)) " +
			"FROM Booking b JOIN Tour t ON b.tour_id = t.id " + "WHERE YEAR(b.booking_at) = :year GROUP BY b.booking_at")
	List<BookingDTO> countByYear(@Param("year") int year);


	@Query("SELECT new com.dulich.dto.BookingDTO(b.trang_thai, " +
			"SUM(CASE " +
			"        WHEN b.trang_thai = 3 THEN b.tong_tien " +
			"        WHEN b.trang_thai IN (1, 2) THEN b.tong_tien / 5 " +
			"        ELSE 0 " +
			"    END)) " +
			"FROM Booking b " +
			"WHERE DATE(b.booking_at) = :date AND b.booking_at IS NOT NULL " +
			"GROUP BY b.trang_thai")
	List<BookingDTO> findStatisticsByDate(@Param("date") LocalDate date);

	@Query("SELECT new com.dulich.dto.BookingDTO(b.trang_thai, " +
			"SUM(CASE " +
			"        WHEN b.trang_thai = 3 THEN b.tong_tien " +
			"        WHEN b.trang_thai IN (1, 2) THEN b.tong_tien / 5 " +
			"        ELSE 0 " +
			"    END)) " +
			"FROM Booking b " +
			"WHERE MONTH(b.booking_at) = :month AND YEAR(b.booking_at) = :year " +
			"AND b.booking_at IS NOT NULL " +
			"GROUP BY b.trang_thai")
	List<BookingDTO> findStatisticsByMonth(@Param("month") int month, @Param("year") int year);

	@Query("SELECT new com.dulich.dto.BookingDTO(b.trang_thai, " +
			"SUM(CASE " +
			"        WHEN b.trang_thai = 3 THEN b.tong_tien " +
			"        WHEN b.trang_thai IN (1, 2) THEN b.tong_tien / 5 " +
			"        ELSE 0 " +
			"    END)) " +
			"FROM Booking b " +
			"WHERE YEAR(b.booking_at) = :year " +
			"AND b.booking_at IS NOT NULL " +
			"GROUP BY b.trang_thai")
	List<BookingDTO> findStatisticsByYear(@Param("year") int year);









	@Query(value = "SELECT new com.dulich.dto.BookingDTO(b.id, b.user_id, b.tour_id, t.ten_tour, b.so_luong_nguoi_lon, b.so_luong_tre_em_13, b.so_luong_tre_em_46, b.ngay_khoi_hanh,b.tong_tien, b.trang_thai, b.pt_thanh_toan, b.ghi_chu, b.booking_at) "
			+ "FROM Booking b JOIN Tour t ON b.tour_id = t.id "
			+ "WHERE (:trang_thai IS NULL OR b.trang_thai = :trang_thai) "
			+ "AND (:ten_tour IS NULL OR :ten_tour = '' OR t.ten_tour LIKE %:ten_tour%) "
			+ "ORDER BY b.id")
	public Page<BookingDTO> findAllBooking(@Param("trang_thai") Integer trang_thai, @Param("ten_tour") String ten_tour, Pageable pageable);

	@Query(value = "SELECT new com.dulich.dto.BookingDTO(b.id, b.user_id, b.tour_id, t.ten_tour, b.so_luong_nguoi_lon, b.so_luong_tre_em_13, b.so_luong_tre_em_46, b.ngay_khoi_hanh,b.tong_tien, b.trang_thai, b.pt_thanh_toan, b.ghi_chu, b.booking_at) "
			+ "FROM Booking b JOIN User u ON b.user_id = u.id JOIN Tour t ON b.tour_id = t.id WHERE b.user_id = :userId")
	List<BookingDTO> findBookingByUserId(@Param("userId") Long userId);

	@Query(value = "SELECT new com.dulich.dto.BookingDTO(b.id, b.user_id, b.tour_id, t.ten_tour, b.so_luong_nguoi_lon, b.so_luong_tre_em_13, b.so_luong_tre_em_46, b.ngay_khoi_hanh,b.tong_tien, b.trang_thai, b.pt_thanh_toan, b.ghi_chu, b.booking_at) "
			+ "FROM Booking b JOIN User u ON b.user_id = u.id JOIN Tour t ON b.tour_id = t.id WHERE b.user_id = :userId AND b.trang_thai != 3 AND b.trang_thai != 4")
	List<BookingDTO> checkBookingByUserId(@Param("userId") Long userId);

	@Query(value = "SELECT new com.dulich.dto.BookingDTO(b.id, b.user_id, b.tour_id, t.ten_tour, b.so_luong_nguoi_lon, b.so_luong_tre_em_13, b.so_luong_tre_em_46, b.ngay_khoi_hanh,b.tong_tien, b.trang_thai, b.pt_thanh_toan, b.ghi_chu, b.booking_at) "
			+ "FROM Booking b JOIN Tour t ON b.tour_id = t.id WHERE b.tour_id = :tour_id")
	Page<BookingDTO> findBookingByTourId(@Param("tour_id") Long tour_id, Pageable pageable);

	@Query(value = "SELECT new com.dulich.dto.BookingDTO(b.id, b.user_id, b.tour_id, t.ten_tour, b.so_luong_nguoi_lon, b.so_luong_tre_em_13, b.so_luong_tre_em_46, b.ngay_khoi_hanh,b.tong_tien, b.trang_thai, b.pt_thanh_toan, b.ghi_chu, b.booking_at) "
			+ "FROM Booking b JOIN Tour t ON b.tour_id = t.id WHERE b.id = :id")
	BookingDTO findBookingById(@Param("id") Long id);

	@Query(value = "SELECT new com.dulich.dto.BookingDetailDTO(b.id, b.user_id, u.ho_ten, u.sdt, b.tour_id, t.ten_tour, b.so_luong_nguoi_lon, b.so_luong_tre_em_13, b.so_luong_tre_em_46, b.ngay_khoi_hanh, b.tong_tien, b.trang_thai, b.pt_thanh_toan, b.ghi_chu, b.booking_at) "
			+ "FROM Booking b JOIN Tour t ON b.tour_id = t.id JOIN User u ON b.user_id = u.id WHERE b.id = :id")
	BookingDetailDTO findBookingDetailById(@Param("id") Long id);
}

