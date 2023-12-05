package com.poly.entities;

import java.time.LocalDateTime;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@Entity
@Table(name = "Tintuc")
public class TinTuc {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  int id;

  @NotNull(message = "Chọn sản phẩm")
  @ManyToOne
  @JoinColumn(name = "Masp")
  SanPham sanpham;

  @ManyToOne
  @JoinColumn(name = "Nguoitao")
  KhachHang khachhang;
  @NotBlank(message = "Nhập tiêu đề tin ")
  String tieude;
  @NotBlank(message = "Nhập nội dung tin tức")
  String noidung;
  String hinhanh;
  @Column(name = "Ngaydang")
  LocalDateTime ngaydang;

}
