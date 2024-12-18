package com.dulich.dto;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookingDetailDTO {

    private Long id;
    private Long user_id;
    private String ho_ten;
    private String sdt;
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

    // Constructor đầy đủ tham số
    public BookingDetailDTO(Long id, Long user_id, String ho_ten, String sdt, Long tour_id, String ten_tour,
                            Integer so_luong_nguoi_lon, Integer so_luong_tre_em_13, Integer so_luong_tre_em_46,
                            Date ngay_khoi_hanh, Long tong_tien, Integer trang_thai, Integer pt_thanh_toan,
                            String ghi_chu, Date booking_at) {
        this.id = id;
        this.user_id = user_id;
        this.ho_ten = ho_ten;
        this.sdt = sdt;
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
}
