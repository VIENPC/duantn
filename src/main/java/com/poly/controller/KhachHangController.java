package com.poly.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.dao.KhachHangDAO;
import com.poly.entities.KhachHang;

@Controller
@RequestMapping("admin")
public class KhachHangController {
    @Autowired
    KhachHangDAO khdao;

    @RequestMapping("/qlkhachhang")
    public String qlkhachhang(Model model) {

        List<KhachHang> khlist = khdao.findAll();

        model.addAttribute("items", khlist);

        return "admin/view/qlcustomer";
    }

    

    // @RequestMapping("/qlkhachhang/editt/{user}")
    // public String edittkh(@PathVariable("user") String user) {
    // KhachHang kh = khdao.findById(user).get();
    // kh.setTrangthaikh(2);
    // khdao.save(kh);
    // return "redirect:/admin/qlkhachhang?success=updatesp";
    // }

    // @RequestMapping("/qlkhachhang/unclock/{user}")
    // public String unclockh(@PathVariable("user") String user) {
    //     KhachHang kh = khdao.findById(user).get();
    //     kh.setTrangthaikh(1);
    //     khdao.save(kh);
    //     return "redirect:/admin/qlkhachhang?success=updatesp";
    // }
}
