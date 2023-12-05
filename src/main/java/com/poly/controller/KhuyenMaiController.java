package com.poly.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.dao.KhuyenMaiDAO;
import com.poly.entities.GiamGia;
import com.poly.entities.SanPham;

@Controller
@RequestMapping("admin")
public class KhuyenMaiController {

    @Autowired
    KhuyenMaiDAO khmdao;

    @RequestMapping("/sale")
    public String sale(Model model) {
        GiamGia km = new GiamGia();
        model.addAttribute("km", km);
        model.addAttribute("khuyenmai", khmdao.findAll());
        return "admin/view/sale";
    }

    @PostMapping("addkhm")
    public String addkhm(@ModelAttribute("km") GiamGia km, HttpServletRequest request) {

        // System.out.println(request.getParameter("loai"));
        Integer loai = Integer.parseInt(request.getParameter("loai"));

        km.setLoai(loai);
        khmdao.save(km);

        return "redirect:/admin/sale?success=addkm";

    }

    @PostMapping("capnhatkm")
    public String updatekhm(@ModelAttribute("km") GiamGia km, HttpServletRequest request) {
        Integer idgiam = Integer.parseInt(request.getParameter("idgiam"));
        GiamGia giamGia = khmdao.findById(idgiam).get();
        Integer loai = Integer.parseInt(request.getParameter("loai"));
        giamGia.setSoluong(km.getSoluong());
        giamGia.setTiengiam(km.getTiengiam());
        giamGia.setLoai(loai);
        giamGia.setMagiam(km.getMagiam());
        giamGia.setGiamin(km.getGiamin());
        
        giamGia.setGiamax(km.getGiamax());
        khmdao.save(giamGia);

        return "redirect:/admin/sale?success=uppkm";

    }

}
