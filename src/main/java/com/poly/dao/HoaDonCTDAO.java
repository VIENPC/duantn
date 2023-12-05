package com.poly.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poly.entities.ChiTietGH;
import com.poly.entities.HoaDonCT;

public interface HoaDonCTDAO extends JpaRepository<HoaDonCT, Integer> {
     @Query("SELECT o FROM HoaDonCT o where o.hoadon.mahd =?1")
    List<HoaDonCT> findChiTietHoaDonByMaHoaDon(Integer maHoaDon);



  @Query("SELECT hdct FROM HoaDonCT hdct JOIN SanPham p ON p.masp = hdct.sanpham.masp JOIN HoaDon hd ON hd.mahd = hdct.hoadon.mahd JOIN KhachHang kh ON kh.usernamekh = hd.khachhang.usernamekh WHERE hd.trangthaihd = 4 AND kh.usernamekh =?1 AND p.masp =?2  ")
  List<HoaDonCT> findkhhdct(String us, Integer masp);

}
