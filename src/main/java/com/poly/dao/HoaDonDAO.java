package com.poly.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poly.entities.HoaDon;

public interface HoaDonDAO extends JpaRepository<HoaDon, Integer> {
        @Query("SELECT o FROM HoaDon o where o.trangthaihd= ?1")
        List<HoaDon> findHdTt(Integer tthd);

        @Query("SELECT o FROM HoaDon o JOIN KhachHang kh ON kh.usernamekh = o.khachhang.usernamekh where o.mahd = ?1 AND kh.email =?2")
        HoaDon findhdem(Integer mahd, String email);

        @Query("SELECT o FROM HoaDon o JOIN KhachHang kh ON kh.usernamekh = o.khachhang.usernamekh where o.trangthaihd= ?1 AND kh.usernamekh =?2")
        List<HoaDon> findHd(Integer tthd, String username);

        // @Query("SELECT NEW com.poly.entities.HoaDonTT(o.mahd,sp.tensp, sp.hinhanh,
        // ct.soluong, sp.giasp, o.tongtien) FROM HoaDon o JOIN KhachHang kh ON
        // kh.usernamekh = o.khachhang.usernamekh JOIN HoaDonCT ct ON
        // ct.hoadon.mahd=o.mahd JOIN SanPham sp ON sp.masp = ct.sanpham.masp where
        // o.trangthaihd= ?1 AND kh.usernamekh= ?2 AND o.mahd= ?3")
        // List<HoaDonTT> findHdAndct(Integer tthd, String user, int mahd);

        @Query("SELECT COUNT(o) FROM HoaDon o where o.trangthaihd= ?1")
        Integer countDontt(Integer tthd);

        @Query("SELECT SUM(hd.tongtien) FROM HoaDon hd where hd.trangthaihd = 4")
        Long getTongTienTatCaHoaDon();

        // @Query("SELECT hd.mahd,hd.tennguoinhan,
        // sp.tensp,SUM(hdct.soluong),hd.tongtien FROM HoaDon hd JOIN HoaDonCT hdct ON
        // hdct.hoadon.mahd =hd.mahd JOIN SanPham sp ON sp.masp = hdct.sanpham.masp JOIN
        // HangSanXuat hsx on hsx.mahang = sp.hangsanxuat.mahang WHERE hd.ngaymua
        // BETWEEN ?1 AND ?2 GROUP BY hd.mahd, hd.tennguoinhan,
        // sp.tensp,SUM(hdct.soluong),hd.tongtien")
        // List<Object[]> thongkehdtg(Date startDate,Date endDate);

        // @Query("SELECT hd.mahd, hd.tennguoinhan,sp.tensp,SUM(hdct.soluong), sp.giasp
        // FROM HoaDon hd JOIN HoaDonCT hdct ON hdct.hoadon.mahd =hd.mahd JOIN SanPham
        // sp ON sp.masp = hdct.sanpham.masp JOIN HangSanXuat hsx on hsx.mahang =
        // sp.hangsanxuat.mahang WHERE hd.ngaymua BETWEEN ?1 AND ?2 GROUP BY hd.mahd,
        // hd.tennguoinhan,sp.tensp, sp.giasp ")
        // List<Object[]> thongkesptg(Date startDate,Date endDate);

        @Query("SELECT o FROM HoaDon o WHERE o.khachhang.usernamekh=?1")
        List<HoaDon> findByUsername(String username);

        // Thống kê biểu đồ
        @Query(nativeQuery = true, value = "SELECT t.thang, COALESCE(SUM(o.tongtien), 0) AS doanh_thu_thang " +
                        "FROM (SELECT 1 AS thang " +
                        "      UNION SELECT 2 " +
                        "      UNION SELECT 3 " +
                        "      UNION SELECT 4 " +
                        "      UNION SELECT 5 " +
                        "      UNION SELECT 6 " +
                        "      UNION SELECT 7 " +
                        "      UNION SELECT 8 " +
                        "      UNION SELECT 9 " +
                        "      UNION SELECT 10 " +
                        "      UNION SELECT 11 " +
                        "      UNION SELECT 12) t " +
                        "LEFT JOIN Hoadon o ON MONTH(o.ngaymua) = t.thang " +
                        "                  AND o.trangthaihd = 4 " +
                        "                  AND o.trangthaitt = 1 " +
                        "                  AND o.ngaymua >= DATEADD(MONTH, -12, GETDATE()) " +
                        "GROUP BY t.thang " +
                        "ORDER BY t.thang")
        List<Object[]> calculateRevenueByMonth();

        @Query("SELECT o.mahd AS orderId, o.tennguoinhan AS customerName, p.tensp AS productName, od.soluong AS quantity,p.giasp, (p.giasp * od.soluong) AS totalPrice "
                        +
                        "FROM HoaDon o " +
                        "INNER JOIN o.hoadonct od " +
                        "INNER JOIN od.sanpham p " +
                        "WHERE o.ngaymua BETWEEN :startDate AND :endDate " +
                        "AND o.trangthaihd = 4")
        List<Object[]> thongKeDonHang(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
