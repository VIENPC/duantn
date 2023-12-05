package com.poly.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poly.entities.GioHang;
import com.poly.entities.HoaDon;

public interface GioHangDAO extends JpaRepository<GioHang, Integer> {
    
    @Query("SELECT o FROM GioHang o where o.khachhang.usernamekh = ?1")
    GioHang findByMaKH(String makh);
}
