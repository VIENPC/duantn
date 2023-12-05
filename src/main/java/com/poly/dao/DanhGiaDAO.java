package com.poly.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poly.entities.DanhGia;

public interface DanhGiaDAO extends JpaRepository<DanhGia, Integer> {

    @Query("SELECT COUNT(d) FROM DanhGia d GROUP BY d.sanpham.masp")
    List<Integer> countDanhGiaBySanPhamMasp();

    @Query("SELECT o FROM DanhGia o WHERE o.sanpham.masp = ?1")
    List<DanhGia> loaddgByMasp(Integer masp);

    @Query("SELECT o FROM DanhGia o JOIN KhachHang kh ON o.khachhang.usernamekh = kh.usernamekh WHERE o.sanpham.masp = ?1 AND kh.usernamekh = ?2")
    DanhGia ktkhsp(Integer masp, String us);

    @Query("SELECT COUNT(dg) FROM DanhGia dg WHERE dg.sanpham.masp = :masp")
    Integer countDanhGiaBySanPhamMasp(@Param("masp") Integer masp);

    @Query("SELECT sum(dg.sodanhgia) FROM DanhGia dg WHERE dg.sanpham.masp = :masp")
    Integer sumsosao(@Param("masp") Integer masp);

    @Query("SELECT COUNT(CASE WHEN dg.sodanhgia = 5 THEN 1 END), "
            + "COUNT(CASE WHEN dg.sodanhgia = 4 THEN 1 END), "
            + "COUNT(CASE WHEN dg.sodanhgia = 3 THEN 1 END), "
            + "COUNT(CASE WHEN dg.sodanhgia = 2 THEN 1 END), "
            + "COUNT(CASE WHEN dg.sodanhgia = 1 THEN 1 END) "
            + "FROM DanhGia dg WHERE dg.sanpham.masp = :masp")
    List<Object[]> countDanhGiaBySao(@Param("masp") Integer masp);

    @Query("SELECT  p.masp, p.tensp, p.hinhanh, " +
            "COUNT(d) AS totalReviews, " +
            "SUM(CASE WHEN d.sodanhgia = 5 THEN 1 ELSE 0 END) AS stars5, " +
            "SUM(CASE WHEN d.sodanhgia = 4 THEN 1 ELSE 0 END) AS stars4, " +
            "SUM(CASE WHEN d.sodanhgia = 3 THEN 1 ELSE 0 END) AS stars3, " +
            "SUM(CASE WHEN d.sodanhgia = 2 THEN 1 ELSE 0 END) AS stars2, " +
            "SUM(CASE WHEN d.sodanhgia = 1 THEN 1 ELSE 0 END) AS stars1 " +
            "FROM DanhGia d JOIN SanPham p ON d.sanpham.masp = p.masp " +
            "GROUP BY p.masp,p.tensp, p.hinhanh")
    List<Object[]> countReviewsAndStars();

}
