package com.poly.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poly.entities.ChiTietGH;

public interface ChiTietGHDAO extends JpaRepository<ChiTietGH, Integer> {

    @Query("SELECT o FROM ChiTietGH o where o.sanpham.masp =?1 and o.giohang.magh = ?2")
    List<ChiTietGH> findByMasp(Integer masp, Integer magh);

    @Query("SELECT o FROM ChiTietGH o where o.giohang.magh = ?1")
    List<ChiTietGH> findByMagh(Integer magh);

    @Query("SELECT o FROM ChiTietGH o where o.sanpham.masp = ?1")
    List<ChiTietGH> findByMasp(Integer magh);
    // @Query("SELECT o FROM ChiTietGH o where o.hoadon.mahd = ?1")
    // List<ChiTietGH> findByMahd(Integer mahd);

    @Query("SELECT o FROM ChiTietGH o where o.sanpham.masp =?1 and o.giohang.magh = ?2")
    ChiTietGH findByMaSPMaGH(Integer masp, Integer magh);
}
