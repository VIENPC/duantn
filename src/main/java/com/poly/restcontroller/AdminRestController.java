package com.poly.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.poly.dao.DanhMucDAO;
import com.poly.dao.HoaDonDAO;
import com.poly.dao.KhachHangDAO;
import com.poly.dao.KhuyenMaiDAO;
import com.poly.dao.SanPhamDAO;
import com.poly.dao.TinTucDAO;
import com.poly.entities.GiamGia;
import com.poly.entities.HoaDon;
import com.poly.entities.NhomSanPham;
import com.poly.entities.TinTuc;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@CrossOrigin(origins = "*")
public class AdminRestController {

    @Autowired
    KhuyenMaiDAO kmdao;

    @Autowired
    DanhMucDAO dmdao;
    @Autowired
    SanPhamDAO spdao;

    @Autowired
    TinTucDAO tdao;
    @Autowired
    HoaDonDAO hddao;

    @GetMapping(value = "/rest/khuyenmai/{idgiam}")
    public ResponseEntity<GiamGia> findOne(@PathVariable("idgiam") Integer id) {
        try {
            return new ResponseEntity<GiamGia>(kmdao.findById(id).get(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<GiamGia>(HttpStatus.BAD_REQUEST);
        }
    }
 
    @GetMapping(value = "/rest/danhmuc/{manhom}")
    public ResponseEntity<NhomSanPham> findNhomsanoham(@PathVariable("manhom") Integer id) {
        try {
            return new ResponseEntity<NhomSanPham>(dmdao.findById(id).get(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<NhomSanPham>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("loadtensp")
    public List<String> getProductNames() {
        // Replace this with your actual data retrieval logic
        List<String> productNames = spdao.loadtensp();
        return productNames;
    }

    @GetMapping("/rest/edittin/{id}")
    public ResponseEntity<TinTuc> getTintuc(@PathVariable("id") Integer id) {
        try {
            return new ResponseEntity<TinTuc>(tdao.findById(id).get(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<TinTuc>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/rest/hoadontt/{id}")
    public ResponseEntity<HoaDon> getHoadon(@PathVariable("id") Integer id) {
        try {
            return new ResponseEntity<HoaDon>(hddao.findById(id).get(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<HoaDon>(HttpStatus.BAD_REQUEST);
        }

    }
}
