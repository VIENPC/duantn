package com.poly.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poly.entities.TinTuc;

public interface TinTucDAO extends JpaRepository<TinTuc, Integer> {

    @Query("SELECT t FROM TinTuc t JOIN SanPham sp ON t.sanpham.masp = sp.masp WHERE sp.tensp LIKE %?1% OR t.tieude LIKE %?1%")
    List<TinTuc> searchByProductNameOrTitle(String keyword);

    @Query("SELECT ns.tennhom, COUNT(t) " +
            "FROM TinTuc t " +
            "JOIN SanPham sp ON t.sanpham.masp = sp.masp " +
            "JOIN NhomSanPham ns ON ns.manhom = sp.nhomsanpham.manhom " +
            "GROUP BY ns.tennhom")
    List<Object[]> countNewsByCategory();
}
