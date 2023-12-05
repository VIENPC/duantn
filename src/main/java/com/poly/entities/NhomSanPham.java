package com.poly.entities;

import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name = "Nhomsanpham")
public class NhomSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	int manhom;
    @NotBlank(message="Vui lòng nhập tên danh mục")
    String tennhom;
    @NotNull(message="Chọn trạng thái!")
    Boolean trangthai;

   
    @JsonIgnore
	@OneToMany(mappedBy = "nhomsanpham")
	List<SanPham> sanpham;
    
}
