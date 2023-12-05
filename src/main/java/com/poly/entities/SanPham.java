package com.poly.entities;

import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

import lombok.Data;

@Data
@Entity
@Table(name = "Sanpham")
public class SanPham {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int masp;
	
	@NotNull(message = "Chọn danh mục sản phẩm")
	@ManyToOne
	@JoinColumn(name = "Manhom")
	NhomSanPham nhomsanpham;

	@NotNull(message = "Chọn thương hiệu")
	@ManyToOne
	@JoinColumn(name = "Idthuonghieu")
	ThuongHieu thuonghieu;
	@NotBlank(message = "Nhập tên sản phẩm")
	String tensp;

	@NotBlank(message = "Nhập mô tả thông tin sản phẩm")
	String thongtin;
	@NotBlank(message = "Nhập thông số kỹ thuật sản phẩm")
	String thongsokythuat;
	@NotNull(message = "Nhập số lượng")
	@Min(value = 0, message = "Số lượng không được bé hơn 0")
	int soluong;
	@NotNull(message = "Nhập giá sản phẩm")
	@Min(value = 0, message = "Giá sản phẩm không được âm")
	int giasp;
	String hinhanh;
	@Column(name = "Ngaynhap")
	LocalDateTime ngaynhap;
	@Column(name = "Ngaycapnhat")
	LocalDateTime ngaycapnhat;
	@NotNull(message = "Chọn trạng thái!")
	Boolean trangthaisp;

	@JsonIgnore
	@OneToMany(mappedBy = "sanpham")
	List<HinhSP> hinhsp;

	@JsonIgnore
	@OneToMany(mappedBy = "sanpham")
	List<HoaDonCT> hoadonct;

	@JsonIgnore
	@OneToMany(mappedBy = "sanpham")
	List<ChiTietGH> chitietgh;
	@JsonIgnore
	@OneToMany(mappedBy = "sanpham")
	List<DanhGia> danhgia;

	@JsonIgnore
	@OneToMany(mappedBy = "sanpham")
	List<TinTuc> tintuc;

}
