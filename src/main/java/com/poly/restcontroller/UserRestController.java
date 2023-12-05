package com.poly.restcontroller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poly.dao.DiaChiDAO;
import com.poly.dao.KhachHangDAO;
import com.poly.entities.DiaChi;
import com.poly.entities.KhachHang;
import com.poly.entities.SanPham;
import com.poly.service.CustomerService;

@CrossOrigin("*")
@RestController
public class UserRestController {
    @Autowired
    CustomerService customerServive;

    @Autowired
    KhachHangDAO khdao;
    @Autowired
    DiaChiDAO dcdao;

    @GetMapping("/rest/account")
    public ResponseEntity<Optional<KhachHang>> getAccount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<KhachHang> account = customerServive.getAccount(username);
        if (account != null) {
            return ResponseEntity.ok(account);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/rest/accountUser")
    public ResponseEntity<KhachHang> getKhachhangUser(
            @RequestParam("usernamekh") String usernamekh) {
        return ResponseEntity.ok(khdao.findById(usernamekh).get());
    }

    @PutMapping("/rest/updatepass")
    public ResponseEntity<KhachHang> updatepass(
            @RequestParam("usernamekh") String usernamekh, @RequestBody KhachHang khachhang) {

        KhachHang existingkh = khdao.findById(usernamekh).get();
        if (existingkh == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        existingkh.setPasswordkh(khachhang.getPasswordkh());

        return new ResponseEntity<>(khdao.save(existingkh), HttpStatus.OK);
    }

    @PutMapping("/rest/account/{usernamekh}")
    public ResponseEntity<KhachHang> updateKhachHang(@PathVariable("usernamekh") String usernamekh,
            @RequestBody KhachHang khachhang) {

        KhachHang existingkh = khdao.findById(usernamekh).get();
        if (existingkh == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        existingkh.setHotenkh(khachhang.getHotenkh());
        existingkh.setEmail(khachhang.getEmail());
        existingkh.setDienthoai(khachhang.getDienthoai());
        existingkh.setGioitinh(khachhang.getGioitinh());
        existingkh.setDiachi(khachhang.getDiachi());
        existingkh.setTrangthaikh(1);

        return new ResponseEntity<>(khdao.save(existingkh), HttpStatus.OK);
    }
    // @GetMapping("/{username}")
    // public KhachHang findByUsername(@PathVariable String username) {
    // return customerServive.findByUsername(username);
    // }

    @PutMapping("/rest/accountup/{usernamekh}")
    public KhachHang put(@PathVariable("usernamekh") String username, @RequestBody KhachHang account) {
        return customerServive.update(account);
    }

    @GetMapping("/rest/diachi")
    public List<DiaChi> loadiachi() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        KhachHang kh = khdao.findById(authentication.getName()).get();
        return dcdao.loadDCbyUS(kh.getUsernamekh());
    }

    @GetMapping("/rest/diachimd")
    public DiaChi loadiachimd() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        KhachHang kh = khdao.findById(authentication.getName()).get();
        return dcdao.loadDCmd(kh.getUsernamekh());
    }

    @PostMapping("/rest/address")
    public ResponseEntity<DiaChi> themdiachi(@RequestBody DiaChi diachi) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        KhachHang kh = khdao.findById(username).get();
        List<DiaChi> listdc = dcdao.loadDCbyUS(kh.getUsernamekh());
        DiaChi dc = new DiaChi();
        if (listdc.isEmpty()) {
            dc.setMaxa(diachi.getMaxa());
            dc.setMahuyen(diachi.getMahuyen());
            dc.setMatinh(diachi.getMatinh());
            dc.setSdtnn(diachi.getSdtnn());
            dc.setDiachi(diachi.getDiachi());
            dc.setKhachhang(kh);
            dc.setTennn(diachi.getTennn());
            dc.setTrangthai(true);

            return ResponseEntity.ok(dcdao.save(dc));
        } else {
            dc.setMaxa(diachi.getMaxa());
            dc.setMahuyen(diachi.getMahuyen());
            dc.setMatinh(diachi.getMatinh());
            dc.setSdtnn(diachi.getSdtnn());
            dc.setDiachi(diachi.getDiachi());
            dc.setKhachhang(kh);
            dc.setTennn(diachi.getTennn());
            dc.setTrangthai(false);
            dcdao.save(dc);
            return ResponseEntity.ok(dcdao.save(dc));
        }

    }

    @GetMapping("/rest/getdc/{id}")
    public ResponseEntity<Optional<DiaChi>> getdc(@PathVariable("id") Integer id) {

        return ResponseEntity.ok(dcdao.findById(id));
    }

    @PutMapping("/rest/updatedc/{id}")
    public ResponseEntity<DiaChi> updatedc(@PathVariable("id") Integer id, @RequestBody DiaChi diachi) {
        DiaChi dc = dcdao.findById(id).get();
        dc.setTennn(diachi.getTennn());
        dc.setSdtnn(diachi.getSdtnn());
        dc.setDiachi(diachi.getDiachi());

        dc.setMatinh(diachi.getMatinh());
        dc.setMahuyen(diachi.getMahuyen());
        dc.setMaxa(diachi.getMaxa());

        return ResponseEntity.ok(dcdao.save(dc));
    }

    @DeleteMapping("/rest/remove-adress/{id}")
    public ResponseEntity<String> deletediachi(@PathVariable("id") Integer id) {
        dcdao.deleteById(id);
        return ResponseEntity.ok().body("{\"message\": \"Thành công!\"}");
    }

    @PutMapping("rest/updatett/{id}")
    public ResponseEntity<String> updatett(@PathVariable("id") Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        KhachHang kh = khdao.findById(authentication.getName()).get();
        List<DiaChi> dcexist = dcdao.loadDCbyUS(kh.getUsernamekh());
        for (DiaChi diaChi : dcexist) {
            diaChi.setTrangthai(false);
            dcdao.save(diaChi);
        }

        DiaChi dc = dcdao.findById(id).get();
        dc.setTrangthai(true);
        dcdao.save(dc);
        return ResponseEntity.ok().body("{\"message\": \"Thành công!\"}");
    }

    @GetMapping("/rest/diachimdm/{id}")
    public ResponseEntity<Optional<DiaChi>> diachimdm(@PathVariable("id") Integer id) {

        return ResponseEntity.ok(dcdao.findById(id));
    }
}