package com.poly.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "Chitietgh")
public class ChiTietGH {
    

     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    
    @ManyToOne
    @JoinColumn(name = "Magh")
    GioHang giohang;
    
    @ManyToOne
    @JoinColumn(name = "Masp")
    SanPham sanpham;
    int soluong;
    int giasp;
}
