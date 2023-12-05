package com.poly.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poly.entities.GiamGia;

public interface KhuyenMaiDAO extends  JpaRepository<GiamGia, Integer>{
    
    @Query("SELECT o FROM GiamGia o WHERE o.soluong > 0")
    List<GiamGia> loadGiamGia();
}
