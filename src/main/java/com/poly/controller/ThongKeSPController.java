package com.poly.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.dao.DanhMucDAO;
import com.poly.dao.SanPhamDAO;
import com.poly.dao.ThuongHieuDAO;

@Controller
@RequestMapping("admin")
public class ThongKeSPController {

    @Autowired
    SanPhamDAO spdao;

    @Autowired
    ThuongHieuDAO thdao;

    @Autowired
    DanhMucDAO dmdao;

    @RequestMapping("/baocaosanpham")
    public String baocaosanpham(Model model) {
        int totalTongGiaHang = 0;
        model.addAttribute("tongcong", totalTongGiaHang);
        model.addAttribute("listhh", thdao.findAll());
        model.addAttribute("listdm", dmdao.findAll());
        return "admin/view/baocaosp";
    }

    @PostMapping("/thongke")
    public String thongkehang(@RequestParam("mahang") Integer mahang, Model model) {

        // model.addAttribute("listhxs", hsxdao.findAll());
        List<Object[]> listsp = spdao.thongkeSanPhamTheoBrand(mahang);
        int totalTongGiaHang = 0;
        for (Object[] itemsp : listsp) {
            totalTongGiaHang += Integer.parseInt(itemsp[3].toString()) * Integer.parseInt(itemsp[2].toString());
        }
        model.addAttribute("itemtksp", listsp);
        model.addAttribute("tongcong", totalTongGiaHang);

        model.addAttribute("listhh", thdao.findAll());
        model.addAttribute("listdm", dmdao.findAll());
        return "admin/view/baocaosp";
    }

    @RequestMapping("/thongkesptg")
    public String thongketg(Model model,
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            RedirectAttributes redirectAttributes) {

        model.addAttribute("listhh", thdao.findAll());
        model.addAttribute("listdm", dmdao.findAll());
        List<Object[]> listsp = spdao.thongkesptg(startDate, endDate);
        int totalTongGiaHang = 0;
        for (Object[] itemsp : listsp) {
            totalTongGiaHang += Integer.parseInt(itemsp[3].toString()) *
                    Integer.parseInt(itemsp[2].toString());
        }
        model.addAttribute("itemtksp", listsp);
        model.addAttribute("tongcong", totalTongGiaHang);
        return "admin/view/baocaosp";
    }

    @PostMapping("/thongkedm")
    public String thongkedm(@RequestParam("madm") Integer madm, Model model) {

        model.addAttribute("listhh", thdao.findAll());
        model.addAttribute("listdm", dmdao.findAll());
        System.out.println(madm);
        int totalTongGiaHang = 0;
        List<Object[]> listtk = spdao.thongkeSanPhamTheoMuc(madm);
        for (Object[] objects : listtk) {
            totalTongGiaHang += Double.parseDouble(objects[4].toString());

        }
        model.addAttribute("itemtksp", listtk);
        model.addAttribute("tongcong", totalTongGiaHang);
        return "admin/view/baocaosp";
    }
}
