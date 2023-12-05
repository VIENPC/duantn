package com.poly.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poly.entities.KhachHang;

public interface KhachHangDAO extends JpaRepository<KhachHang, String> {
	@Query("SELECT kh FROM KhachHang kh WHERE kh.ngaydk >= :fiveDaysAgo")
	List<KhachHang> findNewCustomers(Date fiveDaysAgo);

	@Query("SELECT COUNT(kh) FROM KhachHang kh WHERE kh.trangthaikh = ?1")
	long countKhachhangByTrangthaikh2(Integer ttkh);

	// Phuc vu viec gui mail
	@Query("SELECT a FROM KhachHang a WHERE a.email=?1")
	public KhachHang findByEmail(String email);

	@Query("SELECT a FROM KhachHang a WHERE a.token=?1")
	public KhachHang findByToken(String token);

	// đếm sô user đằng kí
	@Query(value = "SELECT t.thang, COALESCE(COUNT(u.usernamekh), 0) AS doanh_thu_thang "
			+ "FROM (SELECT 1 AS thang UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 "
			+ "UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 "
			+ "UNION SELECT 10 UNION SELECT 11 UNION SELECT 12) t "
			+ "LEFT JOIN KhachHang u ON MONTH(u.ngaydk) = t.thang "
			+ "AND u.ngaydk >= DATEADD(MONTH, -12, GETDATE()) "
			+ "GROUP BY t.thang ORDER BY t.thang", nativeQuery = true)
	List<Object[]> calculateUserRegistrationByMonth();

	// đếm số đơn hàng theo tháng
	@Query(value = "SELECT t.thang, COALESCE(COUNT(o.mahd), 0) AS doanh_thu_thang "
			+ "FROM (SELECT 1 AS thang UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 "
			+ "UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 "
			+ "UNION SELECT 10 UNION SELECT 11 UNION SELECT 12) t "
			+ "LEFT JOIN Hoadon o ON MONTH(o.ngaymua) = t.thang "
			+ "AND o.ngaymua >= DATEADD(MONTH, -12, GETDATE()) "
			+ "AND o.trangthaihd = 4 "
			+ "GROUP BY t.thang ORDER BY t.thang", nativeQuery = true)
	List<Object[]> calculateOrdersByMonth();


}
