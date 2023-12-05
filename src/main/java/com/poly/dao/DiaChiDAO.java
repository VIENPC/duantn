package com.poly.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poly.entities.DiaChi;


public interface DiaChiDAO extends JpaRepository<DiaChi, Integer> {
    @Query("SELECT o FROM DiaChi o where o.khachhang.usernamekh =?1")
    List<DiaChi> loadDCbyUS(String us);

    @Query("SELECT o FROM DiaChi o where o.khachhang.usernamekh =?1 and o.trangthai=true ")
    DiaChi loadDCmd(String us);

   
    
}
