package com.poly.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poly.entities.HoaDon;
import com.poly.entities.NhomSanPham;
import com.poly.entities.SanPham;

public  interface DanhMucDAO  extends JpaRepository<NhomSanPham, Integer> {
    @Query("SELECT o FROM NhomSanPham o WHERE o.trangthai = true")
    List<NhomSanPham> findAlltt();


}
