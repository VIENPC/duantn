package com.poly.entities;

import java.io.Serializable;


import javax.persistence.*;
import lombok.Data;
@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "Hoadonct")
public class HoaDonCT implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    
    @ManyToOne
    @JoinColumn(name = "Mahd")
    HoaDon hoadon;
    
    @ManyToOne
    @JoinColumn(name = "Masp")
    SanPham sanpham;
  
    int soluong;
  
    

}
