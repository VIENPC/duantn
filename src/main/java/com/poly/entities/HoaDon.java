package com.poly.entities;


import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import lombok.Data;
@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "Hoadon")
public class HoaDon {
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int mahd;
	@ManyToOne
	@JoinColumn(name = "Usernamekh")
	KhachHang khachhang;	
	
	@Column(name = "Ngaymua")
	Date ngaymua = new Date();
	int trangthaihd;
	String tennguoinhan;
	String diachinn;
	String dienthoainn;
	Long tienvc;
	Long tongtien;
	Boolean trangthaitt;
	String ghichu;

	@ManyToOne
	@JoinColumn(name ="idgiam")
	GiamGia giamgia;
	
	@JsonIgnore	
	@OneToMany(mappedBy = "hoadon")
	List<HoaDonCT> hoadonct;
}
