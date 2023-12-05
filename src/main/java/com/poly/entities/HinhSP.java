package com.poly.entities;

import javax.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "Hinhanhsanpham")
public class HinhSP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "masp")
    SanPham sanpham;

    String hinh;

    
}
