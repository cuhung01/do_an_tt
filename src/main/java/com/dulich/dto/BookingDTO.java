package com.dulich.dto;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookingDTO {

    private Long id;
    private Long user_id;
    private Long tour_id;
    private String ten_tour;
    private Integer so_luong_nguoi_lon;
    private Integer so_luong_tre_em_13;
    private Integer so_luong_tre_em_46;
    private Date ngay_khoi_hanh;
    private Long tong_tien;
    private Integer trang_thai;
    private Integer pt_thanh_toan;
    private String ghi_chu;
    private Date booking_at;
    private Long totalPeople;   // Trường tính tổng số người
    private Long totalAmount;      // Trường tính tổng tiền
    private Long completedAmount;
    private  Long tiencoc;

    // Constructor cập nhật để truyền vào tất cả các tham số từ JPQL
    public BookingDTO(Long id, Long user_id, Long tour_id, String ten_tour,
                      Integer so_luong_nguoi_lon, Integer so_luong_tre_em_13, Integer so_luong_tre_em_46,
                       Date ngay_khoi_hanh,Long tong_tien, Integer trang_thai, Integer pt_thanh_toan,
                      String ghi_chu, Date booking_at) {
        this.id = id;
        this.user_id = user_id;
        this.tour_id = tour_id;
        this.ten_tour = ten_tour;
        this.so_luong_nguoi_lon = so_luong_nguoi_lon;
        this.so_luong_tre_em_13 = so_luong_tre_em_13;
        this.so_luong_tre_em_46 = so_luong_tre_em_46;
        this.ngay_khoi_hanh = ngay_khoi_hanh;
        this.tong_tien = tong_tien;
        this.trang_thai = trang_thai;
        this.pt_thanh_toan = pt_thanh_toan;
        this.ghi_chu = ghi_chu;
        this.booking_at = booking_at;

    }
    public BookingDTO(Long id, Long user_id, Long tour_id, String ten_tour, Long totalPeople,
                      Integer trang_thai, Date booking_at, Long totalAmount , Long tiencoc) {
        this.id = id;
        this.user_id = user_id;
        this.tour_id = tour_id;
        this.ten_tour = ten_tour;
        this.totalPeople = totalPeople;
        this.trang_thai = trang_thai;
        this.booking_at = booking_at;
        this.totalAmount = totalAmount;
        this.tiencoc = tiencoc;

    }
    public BookingDTO (Integer trang_thai ,Long completedAmount){
        this.trang_thai = trang_thai;
        this.completedAmount = completedAmount;
    }

}