package com.poly.entities;

import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name = "Thuonghieu")
public class ThuongHieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
 
    String tenthuonghieu;
     
    String hinhanh;
 
     @JsonIgnore
	@OneToMany(mappedBy = "thuonghieu")
	List<SanPham> sanpham;

}
