package com.poly.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poly.entities.HinhSP;

public interface HinhAnhDAO  extends JpaRepository<HinhSP, Integer>{
    @Query("SELECT o FROM HinhSP o where o.sanpham.masp =?1")
    List<HinhSP> findbyMasp(Integer masp);
    
}