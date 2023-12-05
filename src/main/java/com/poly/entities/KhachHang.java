package com.poly.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@Table(name = "Khachhang")
public class KhachHang {
	@Id
	String usernamekh;
	String hotenkh;
	String passwordkh;
	String diachi;
	String email;
	String dienthoai;

	@Temporal(TemporalType.DATE)
	@Column(name = "Ngaydk")
	Date ngaydk = new Date();
	Boolean gioitinh;
	String token;
	Integer trangthaikh;

	@JsonIgnore
	@OneToMany(mappedBy = "khachhang")
	List<DanhGia> danhgia;

	@JsonIgnore
	@OneToMany(mappedBy = "khachhang")
	List<TinTuc> tintuc;
	@JsonIgnore
	@OneToMany(mappedBy = "khachhang")
	List<GioHang> giohang;
	@JsonIgnore
	@OneToMany(mappedBy = "khachhang")
	List<HoaDon> hoadon;
	@JsonIgnore
	@OneToMany(mappedBy = "khachhang")
	List<DiaChi> diachict;

	@JsonIgnore
	@OneToMany(mappedBy = "khachhang", fetch = FetchType.EAGER)
	List<Authority> authorities;

}
