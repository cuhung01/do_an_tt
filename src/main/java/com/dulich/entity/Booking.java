package com.dulich.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long user_id;

    private Long tour_id;

    // Ba thuộc tính riêng biệt cho người lớn, trẻ em từ 1 đến 3 tuổi và từ 4 đến 6 tuổi
    private Integer so_luong_nguoi_lon;  // Người lớn
    private Integer so_luong_tre_em_13;   // Trẻ em từ 1 đến 3 tuổi
    private Integer so_luong_tre_em_46;   // Trẻ em từ 4 đến 6 tuổi

    private Date ngay_khoi_hanh;

    private Long tong_tien;

    private Integer trang_thai;

    private Date booking_at;

    private Integer pt_thanh_toan;

    private String ghi_chu;

    @PrePersist
    public void onCreate() {
        this.booking_at = new Date();
    }
}
