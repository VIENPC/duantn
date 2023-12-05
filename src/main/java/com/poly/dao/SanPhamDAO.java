package com.poly.dao;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poly.entities.SanPham;

public interface SanPhamDAO extends JpaRepository<SanPham, Integer> {

        @Query("SELECT o FROM SanPham o JOIN NhomSanPham n on o.nhomsanpham.manhom = n.manhom WHERE o.trangthaisp = true and n.manhom =?1")
        List<SanPham> loadspdm(Integer manhom);

        @Query("SELECT o FROM SanPham o WHERE o.trangthaisp = true")
        List<SanPham> findAlltt();

        @Query("SELECT o FROM SanPham o JOIN NhomSanPham n on o.nhomsanpham.manhom = n.manhom WHERE o.trangthaisp = true and n.manhom =?1")
        List<SanPham> findSPDanhMuc(Integer manhom);

        // Lọc sản phẩm theo thuong hiệu
        @Query("SELECT o FROM SanPham o JOIN ThuongHieu t on o.thuonghieu.id = t.id WHERE o.trangthaisp = true and t.id =?1")
        List<SanPham> findSPThuonghieu(Integer id);

        // lọc sản phẩm theo dang muc
        @Query("SELECT o FROM SanPham o JOIN NhomSanPham n on o.nhomsanpham.manhom = n.manhom WHERE o.trangthaisp = true and n.manhom in :manhom")
        List<SanPham> findSPDanhMucLoc(List<Integer> manhom);

        // loc sản phẩm kết hợp danh muc va thương hiệu
        @Query("SELECT o FROM SanPham o JOIN NhomSanPham n ON o.nhomsanpham.manhom = n.manhom JOIN ThuongHieu t ON t.id = o.thuonghieu.id WHERE o.trangthaisp = true AND n.manhom IN :manhom AND t.id = :id")
        List<SanPham> findSPDanhMucAndThuongHieuLoc(@Param("manhom") List<Integer> manhom, @Param("id") Integer id);

        // loc sản phẩm kết hợp danh muc va thương hiệu giá sản phẩm
        @Query("SELECT o FROM SanPham o " +
                        "JOIN NhomSanPham n ON o.nhomsanpham.manhom = n.manhom " +
                        "JOIN ThuongHieu t ON t.id = o.thuonghieu.id " +
                        "WHERE o.trangthaisp = true " +
                        "AND n.manhom IN :manhom " +
                        "AND t.id = :id " +
                        "AND o.giasp BETWEEN :minPrice AND :maxPrice")
        List<SanPham> findSPByDanhMucAndThuongHieuAndGia(@Param("manhom") List<Integer> manhom,
                        @Param("id") Integer id,
                        @Param("minPrice") Integer minPrice,
                        @Param("maxPrice") Integer maxPrice);

        // lọc sản phẩm theo danh mục và giá
        @Query("SELECT o FROM SanPham o " +
                        "JOIN NhomSanPham n ON o.nhomsanpham.manhom = n.manhom " +
                        "WHERE o.trangthaisp = true " +
                        "AND n.manhom IN :manhom " +
                        "AND o.giasp BETWEEN :minPrice AND :maxPrice")
        List<SanPham> findSPByDanhMucAndGia(@Param("manhom") List<Integer> manhom,
                        @Param("minPrice") Integer minPrice,
                        @Param("maxPrice") Integer maxPrice);

        // lọc theo thuong hiệu và giá
        @Query("SELECT o FROM SanPham o " +
                        "JOIN ThuongHieu t ON t.id = o.thuonghieu.id " +
                        "WHERE o.trangthaisp = true " +
                        "AND t.id = :id " +
                        "AND o.giasp BETWEEN :minPrice AND :maxPrice")
        List<SanPham> findSPByThuongHieuAndGia(@Param("id") Integer id,
                        @Param("minPrice") Integer minPrice,
                        @Param("maxPrice") Integer maxPrice);

        List<SanPham> findByGiaspBetweenAndTrangthaispIsTrue(Integer minPrice, Integer maxPrice);

        List<SanPham> findByGiaspGreaterThanEqualAndTrangthaispIsTrue(Integer minPrice);

       List<SanPham> findByGiaspGreaterThanAndTrangthaispIsTrue(Integer maxPrice);

//        lọc sản phẩm theo danh mục theo thương hiệu và theo giá lớn hơn 20tr
         @Query("SELECT o FROM SanPham o " +
                        "JOIN NhomSanPham n ON o.nhomsanpham.manhom = n.manhom " +
                           "JOIN ThuongHieu t ON t.id = o.thuonghieu.id " +
                        "WHERE o.trangthaisp = true " +
                        "AND n.manhom IN :manhom " +
                       "AND t.id = :id " +
                        "AND o.giasp >= :maxPrice")
        List<SanPham> findSPByDanhMucthgiamax(@Param("manhom") List<Integer> manhom,
                        @Param("id") Integer id,
                        @Param("maxPrice") Integer maxPrice);


        SanPham findByMasp(Integer masp);

        long countByTrangthaisp(Boolean tt);

        List<SanPham> findTop8ByNgaycapnhatAfterAndTrangthaispOrderByNgaycapnhatDesc(LocalDateTime newdata,
                        Boolean trangThai);

        @Query("SELECT p.masp, p.tensp, od.soluong AS quantity, p.giasp AS unitPrice, (od.soluong * p.giasp) AS totalPrice, p.thuonghieu.tenthuonghieu AS brandName "
                        +
                        "FROM HoaDonCT od " +
                        "JOIN SanPham p ON od.sanpham.masp = p.masp " +
                        "JOIN ThuongHieu b ON p.thuonghieu.id = b.id " +
                        "JOIN HoaDon o ON od.hoadon.mahd = o.mahd " +
                        "WHERE o.trangthaihd = 4 AND b.id = ?1")
        List<Object[]> thongkeSanPhamTheoBrand(int brandId);

        @Query("SELECT p.masp, p.tensp, sum(o.soluong), p.giasp, (sum(o.soluong) * p.giasp), b.tenthuonghieu FROM SanPham p JOIN NhomSanPham c ON c.manhom =p.nhomsanpham.manhom JOIN HoaDonCT o ON o.sanpham.masp = p.masp JOIN HoaDon od ON od.mahd = o.hoadon.mahd JOIN ThuongHieu b ON b.id = p.thuonghieu.id Where od.trangthaihd = 4 AND c.manhom =?1 GROUP BY p.masp, p.tensp, p.giasp, b.tenthuonghieu")
        List<Object[]> thongkeSanPhamTheoMuc(int muc);

        @Query("SELECT p.masp AS masp, p.tensp AS tensp, SUM(od.soluong) AS soluong, p.giasp AS giasp, " +
                        "SUM(od.soluong * p.giasp) AS totalPrice, p.thuonghieu.tenthuonghieu AS tenhang " +
                        "FROM HoaDonCT od " +
                        "INNER JOIN od.sanpham p " +
                        "WHERE od.hoadon.ngaymua BETWEEN :startDate AND :endDate " +
                        "AND od.hoadon.trangthaihd = 4 " +
                        "GROUP BY p.masp, p.tensp, p.giasp, p.thuonghieu.tenthuonghieu")
        List<Object[]> thongkesptg(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

        // // @Query("SELECT sp FROM SanPham sp WHERE sp.ngaynhap >= ?1 AND
        // sp.trangthaisp =?2 ORDER BY sp.ngaynhap DESC LIMIT 7 ")
        // // List<SanPham> findNewProduct7(LocalDateTime newdata, Boolean tt);

        // @Query("SELECT sp FROM SanPham sp WHERE sp.ngaynhap >= ?1 AND sp.trangthaisp
        // =?2 ORDER BY sp.ngaynhap DESC ")
        // List<SanPham> findNewProduct(LocalDateTime newdata, Boolean tt);

        @Query("SELECT o FROM SanPham o WHERE o.trangthaisp =?1")
        List<SanPham> findByTtsp(Boolean ttsp);

        @Query("SELECT o.tensp FROM SanPham o")
        List<String> loadtensp();
        // long countByTrangthaisp(Boolean tt);

        // @Query("SELECT sp.masp, sp.tensp,sp.giasp, SUM(hdct.soluong),
        // sp.hangsanxuat.tenhang FROM SanPham sp JOIN HoaDonCT hdct ON sp.masp =
        // hdct.sanpham.masp JOIN HoaDon hd ON hd.mahd = hdct.hoadon.mahd JOIN
        // HangSanXuat hsx on hsx.mahang = sp.hangsanxuat.mahang GROUP BY sp.masp,
        // sp.tensp, sp.giasp, sp.hangsanxuat.tenhang HAVING SUM(hdct.soluong) >= 5
        // ORDER BY SUM(hdct.soluong) DESC")
        // List<Object[]> findSanPhamBanChay();

        // @Query("SELECT sp.masp, sp.tensp,SUM(hdct.soluong),
        // sp.giasp,sp.hangsanxuat.tenhang FROM HoaDon hd JOIN HoaDonCT hdct ON
        // hdct.hoadon.mahd =hd.mahd JOIN SanPham sp ON sp.masp = hdct.sanpham.masp JOIN
        // HangSanXuat hsx on hsx.mahang = sp.hangsanxuat.mahang WHERE hsx.mahang=?1 AND
        // hd.trangthaihd = 4 GROUP BY sp.masp, sp.tensp, sp.giasp,
        // sp.hangsanxuat.tenhang ")
        // List<Object[]> thongkesph(int mahang);

        // sản phẩm bán chạy theo tháng
        @Query("SELECT p.masp, p.tensp, SUM(od.soluong) AS totalQuantity, p.giasp AS unitPrice, SUM(od.soluong * p.giasp) AS totalSales, b.tenthuonghieu, o.ngaymua, p.hinhanh "
                        +
                        "FROM HoaDonCT od " +
                        "INNER JOIN od.sanpham p " +
                        "INNER JOIN p.thuonghieu b " +
                        "INNER JOIN od.hoadon o " +
                        "WHERE o.trangthaihd = 4 " +
                        "AND o.trangthaitt = true " +
                        "AND MONTH(o.ngaymua) = :month " +
                        "GROUP BY p.masp, p.tensp, p.giasp, b.tenthuonghieu, o.ngaymua, p.hinhanh " +
                        "ORDER BY totalQuantity DESC")
        List<Object[]> findBestSellingProductsMonth(Integer month);

        // sản phẩm bán chạy theo loại
       @Query("SELECT p.masp, p.tensp, p.giasp, SUM(od.soluong) AS totalSold, p.thuonghieu.tenthuonghieu, p.hinhanh " +
       "FROM HoaDonCT od " +
       "JOIN HoaDon hd ON hd.mahd = od.hoadon.mahd " +
       "INNER JOIN od.sanpham p " +
       "WHERE p.nhomsanpham.manhom = :manhom AND hd.trangthaihd = 4 " +
       "GROUP BY p.masp, p.tensp, p.giasp, p.thuonghieu.tenthuonghieu, p.hinhanh " +
       "ORDER BY totalSold DESC")
List<Object[]> findBestSellingProductsByCategory(@Param("manhom") int categoryId);

        // sản phẩm bán chạy theo hãng
       @Query("SELECT p.masp, p.tensp, p.giasp, SUM(od.soluong) AS totalSold, p.thuonghieu.tenthuonghieu, p.hinhanh " +
       "FROM HoaDonCT od " +
       "INNER JOIN od.sanpham p " +
       "INNER JOIN od.hoadon hd " +
       "WHERE p.thuonghieu.id = :id AND hd.trangthaihd = 4 " +
       "GROUP BY p.masp, p.tensp, p.giasp, p.thuonghieu.tenthuonghieu, p.hinhanh " +
       "ORDER BY totalSold DESC")
List<Object[]> findBestSellingProductsByBrand(@Param("id") int id);


        // Sản phẩm bán chạy
        @Query("SELECT p.masp, p.tensp, p.giasp, SUM(od.soluong) AS totalSold, p.thuonghieu.tenthuonghieu, p.hinhanh "
                        +
                        "FROM HoaDonCT od " +
                        "INNER JOIN od.sanpham p " +
                           "INNER JOIN od.hoadon hd " +
                           "WHERE hd.trangthaihd = 4 " +
                        "GROUP BY p.masp, p.tensp, p.giasp, p.thuonghieu.tenthuonghieu, p.hinhanh " +
                        "ORDER BY totalSold DESC")
        List<Object[]> findBestSellingProducts();

        // Tất cả sản phẩm bán chậm
        @Query("SELECT p.masp, p.tensp, p.hinhanh, p.soluong, p.giasp, p.thuonghieu.tenthuonghieu FROM SanPham p " +
                        "WHERE p.trangthaisp = ?1 AND NOT EXISTS (" +
                        "   SELECT 1 FROM HoaDonCT od " +
                        "   JOIN od.hoadon o " +
                        "   WHERE od.sanpham.masp = p.masp" +
                        ")")
        List<Object[]> findProductsNotInAnyOrder(boolean trangthaisp);

        // Danh sách sản phẩm bán chậm theo loại
        @Query("SELECT p.masp, p.tensp, p.hinhanh, p.soluong, p.giasp, p.thuonghieu.tenthuonghieu FROM SanPham p " +
                        "WHERE p.nhomsanpham.manhom = :category_id and p.trangthaisp =true" +
                        "  AND NOT EXISTS (" +
                        "      SELECT 1 FROM HoaDonCT od " +
                        "      JOIN od.hoadon o " +
                        "      WHERE od.sanpham.masp = p.masp " +
                        ")")
        List<Object[]> findProductsNotInAnyOrderForCategory(@Param("category_id") int categoryId);

        // Danh sách sản phẩm bán chậm theo thương hiệu
        @Query("SELECT p.masp, p.tensp, p.hinhanh, p.soluong, p.giasp, p.thuonghieu.tenthuonghieu FROM SanPham p " +
                        "WHERE p.thuonghieu.id = :id and p.trangthaisp =true" +
                        "  AND NOT EXISTS (" +
                        "      SELECT 1 FROM HoaDonCT od " +
                        "      JOIN od.hoadon o " +
                        "      WHERE od.sanpham.masp = p.masp" +
                        ")")
        List<Object[]> findProductsNotInAnyOrderForBrand(@Param("id") int id);

            List<SanPham> findByTenspContainingAndTrangthaispIsTrue(String tensp);
}
