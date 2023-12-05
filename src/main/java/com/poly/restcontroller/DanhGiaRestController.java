package com.poly.restcontroller;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poly.dao.DanhGiaDAO;
import com.poly.dao.HoaDonCTDAO;
import com.poly.dao.KhachHangDAO;
import com.poly.dao.SanPhamDAO;
import com.poly.entities.DanhGia;
import com.poly.entities.HoaDonCT;
import com.poly.entities.KhachHang;
import com.poly.entities.SanPham;

@RestController
@CrossOrigin(origins = "*")
public class DanhGiaRestController {
    @Autowired
    DanhGiaDAO dgdao;

    @Autowired
    KhachHangDAO khdao;

    @Autowired
    HoaDonCTDAO hdctdao;

    @Autowired
    SanPhamDAO spdao;

    // load đánh giá
    @GetMapping("/rest/loaddg/{masp}")
    public ResponseEntity<List<DanhGia>> loaddanhgia(@PathVariable("masp") Integer masp) {
        System.out.println(masp);

        return ResponseEntity.ok(dgdao.loaddgByMasp(masp));
    }

    @GetMapping("/rest/countdg/{masp}")
    public ResponseEntity<Integer> tongsao(@PathVariable("masp") Integer masp) {
        return ResponseEntity.ok(dgdao.countDanhGiaBySanPhamMasp(masp));
    }

    @GetMapping("/rest/sumsao/{masp}")
    public ResponseEntity<Integer> countdg(@PathVariable("masp") Integer masp) {
        return ResponseEntity.ok(dgdao.sumsosao(masp));
    }

    @GetMapping("/rest/danhgia")
    public ResponseEntity<List<DanhGia>> danhgia(@RequestParam(value = "masp", required = false) Integer masp,
            @RequestParam(value = "sodanhgia", required = false) Integer so,
            @RequestParam(value = "noidungdg", required = false) String noidung) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        KhachHang kh = khdao.findById(username).get();
        SanPham sp = spdao.findById(masp).get();
        List<HoaDonCT> hdct = hdctdao.findkhhdct(username, sp.getMasp());
        DanhGia dGia  = dgdao.ktkhsp(masp, username);
        DanhGia dgsp = new DanhGia(); 
        LocalDateTime now = LocalDateTime.now();
        if (!hdct.isEmpty() && dGia == null) {
            dgsp.setSanpham(sp);
            dgsp.setKhachhang(kh); 
            dgsp.setSodanhgia(so);
            dgsp.setNoidungdg(noidung);
            dgsp.setNgaydanhgia(now);
            dgdao.save(dgsp);
            return new ResponseEntity<>(dgdao.loaddgByMasp(masp), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/rest/count-by-sao/{masp}")
    public ResponseEntity<Map<String, Integer>> countDanhGiaBySao(@PathVariable Integer masp) {
        List<Object[]> resultList = dgdao.countDanhGiaBySao(masp);

        if (resultList.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyMap());
        }

        Object[] result = resultList.get(0);

        Map<String, Integer> resultAsMap = new LinkedHashMap<>();
        resultAsMap.put("sao5", ((Number) result[0]).intValue());
        resultAsMap.put("sao4", ((Number) result[1]).intValue());
        resultAsMap.put("sao3", ((Number) result[2]).intValue());
        resultAsMap.put("sao2", ((Number) result[3]).intValue());
        resultAsMap.put("sao1", ((Number) result[4]).intValue());

        return ResponseEntity.ok(resultAsMap);
    }


    @DeleteMapping("/rest/remove-dg")
    public ResponseEntity<List<DanhGia>> deletedg(@RequestParam(value = "masp", required = false) Integer masp,@RequestParam(value = "id", required = false) Integer id){
        dgdao.deleteById(id);
        return ResponseEntity.ok(dgdao.loaddgByMasp(masp));
    }
}
