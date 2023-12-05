package com.poly.entities;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "Danhgia")
public class DanhGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "Masp")
    SanPham sanpham;

    @ManyToOne
    @JoinColumn(name = "Makh")
    KhachHang khachhang;

    int sodanhgia;

    String noidungdg;
    @Column(name = "Ngaydangbai")
    LocalDateTime ngaydanhgia;

}
