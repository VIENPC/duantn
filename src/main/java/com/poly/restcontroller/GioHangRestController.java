package com.poly.restcontroller;

import java.util.List;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.poly.dao.ChiTietGHDAO;
import com.poly.dao.GioHangDAO;
import com.poly.dao.KhachHangDAO;
import com.poly.dao.SanPhamDAO;
import com.poly.entities.ChiTietGH;
import com.poly.entities.GioHang;
import com.poly.entities.HoaDon;
import com.poly.entities.KhachHang;
import com.poly.entities.SanPham;

@RestController
@CrossOrigin(origins = "*")
public class GioHangRestController {

    @Autowired
    KhachHangDAO khdao;

    @Autowired
    GioHangDAO ghdao;

    @Autowired
    SanPhamDAO spdao;

    @Autowired
    ChiTietGHDAO ctghdao;

    @PostMapping("/rest/addcart")
    public ResponseEntity<List<ChiTietGH>> chitet(@RequestBody SanPham sanpham) {
        // System.out.println(sanpham.getMasp());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        KhachHang kh = khdao.findById(authentication.getName()).get();
        // System.out.println(kh.getUsernamekh());
        GioHang gh = ghdao.findByMaKH(kh.getUsernamekh());
        SanPham sp = spdao.findById(sanpham.getMasp()).get();
        List<ChiTietGH> chiTietGH = ctghdao.findByMasp(sanpham.getMasp(), gh.getMagh());
        // ChiTietGH chiTietGH = new ChiTietGH();
        if (chiTietGH.isEmpty()) {
            ChiTietGH chiTiet = new ChiTietGH();
            chiTiet.setSanpham(sp);
            chiTiet.setGiohang(gh);
            chiTiet.setSoluong(1);
            chiTiet.setGiasp(sp.getGiasp());
            ctghdao.save(chiTiet);
            return ResponseEntity.ok(ctghdao.findByMagh(gh.getMagh()));
        } else {
            ChiTietGH ctgh = chiTietGH.get(0);
            ctgh.setSoluong(ctgh.getSoluong() + 1);
            ctghdao.save(ctgh);
            return ResponseEntity.ok(ctghdao.findByMagh(gh.getMagh()));
        }

    }

    @PostMapping("/rest/addcartsl")
    public ResponseEntity<ChiTietGH> chitet2(@RequestBody SanPham sanpham) {
        // System.out.println(sanpham.getMasp());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        KhachHang kh = khdao.findById(authentication.getName()).get();
        // System.out.println(kh.getUsernamekh());
        GioHang gh = ghdao.findByMaKH(kh.getUsernamekh());
        SanPham sp = spdao.findById(sanpham.getMasp()).get();
        List<ChiTietGH> chiTietGH = ctghdao.findByMasp(sanpham.getMasp(), gh.getMagh());
        // ChiTietGH chiTietGH = new ChiTietGH();
        if (chiTietGH.isEmpty()) {
            ChiTietGH chiTiet = new ChiTietGH();
            chiTiet.setSanpham(sp);
            chiTiet.setGiohang(gh);
            chiTiet.setSoluong(sanpham.getSoluong());
            chiTiet.setGiasp(sp.getGiasp());

            return ResponseEntity.ok(ctghdao.save(chiTiet));
        } else {
            ChiTietGH ctgh = chiTietGH.get(0);
            ctgh.setSoluong(ctgh.getSoluong() + sanpham.getSoluong());
            return ResponseEntity.ok(ctghdao.save(ctgh));
        }

    }

    @GetMapping("/rest/cart")
    ResponseEntity<List<ChiTietGH>> cart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        KhachHang kh = khdao.findById(authentication.getName()).get();
        GioHang gh = ghdao.findByMaKH(kh.getUsernamekh());
        return ResponseEntity.ok(ctghdao.findByMagh(gh.getMagh()));
    }

    @PutMapping("/rest/update-quantity/{masp}/{soluong}")
    public ResponseEntity<String> updateCartItemQuantity(@PathVariable("masp") Integer masp,
            @PathVariable("soluong") int soluong) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        KhachHang kh = khdao.findById(authentication.getName()).get();
        SanPham sp = spdao.findById(masp).get();

        if (sp == null) {
            return ResponseEntity.badRequest().body("Item not found!");
        }

        // Tìm giỏ hàng chưa hoàn tất của người dùng

        GioHang gh = ghdao.findByMaKH(kh.getUsernamekh());
        if (gh != null) {
            // Xác định chi tiết đơn hàng tương ứng với sản phẩm và cập nhật số lượng
            ChiTietGH ctgh = ctghdao.findByMaSPMaGH(masp, gh.getMagh());
            if (ctgh != null) {

                ctgh.setSoluong(soluong);
                ctghdao.save(ctgh);
                return ResponseEntity.ok().body("{\"message\": \"Đã cập nhật số lượng!\"}");
            }
        }

        return ResponseEntity.ok().body("{\"message\": \"Đã cập nhật số lượng!\"}");

    }

    @DeleteMapping("rest/remove-from-cart/{masp}")
    public ResponseEntity<String> removeFromCart(@PathVariable("masp") Integer masp) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        KhachHang kh = khdao.findById(authentication.getName()).get();
        GioHang gh = ghdao.findByMaKH(kh.getUsernamekh());
        ChiTietGH ctgh = ctghdao.findByMaSPMaGH(masp, gh.getMagh());
        System.out.println(ctgh.getId());
        ctghdao.deleteById(ctgh.getId());
        return ResponseEntity.ok().body("{\"message\": \"Item removed from cart!\"}");
    }

    @DeleteMapping("/rest/removecart")
    public ResponseEntity<List<ChiTietGH>> removeFromCart2(@RequestBody List<Integer> maspList) {
        // Xử lý xóa danh sách sản phẩm từ giỏ hàng
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        KhachHang kh = khdao.findById(authentication.getName()).get();
        GioHang gh = ghdao.findByMaKH(kh.getUsernamekh());

        for (Integer masp : maspList) {
            ChiTietGH ctgh = ctghdao.findByMaSPMaGH(masp, gh.getMagh());
            if (ctgh != null) {
                ctghdao.deleteById(ctgh.getId());
            }
        }

        return ResponseEntity.ok(ctghdao.findByMagh(gh.getMagh()));
    }

}
