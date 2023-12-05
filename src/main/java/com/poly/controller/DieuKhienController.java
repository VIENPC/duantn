package com.poly.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import com.poly.entities.HoaDon;

import com.poly.dao.HoaDonDAO;
import com.poly.dao.KhachHangDAO;
import com.poly.dao.SanPhamDAO;

@Controller
@RequestMapping("admin")
public class DieuKhienController {
    @Autowired
    KhachHangDAO khdao;
    @Autowired
    SanPhamDAO spdao;
    @Autowired
    HoaDonDAO hddao;

    @RequestMapping("/home")
    public String indexdasboad(Model model) {
        model.addAttribute("slkh", khdao.count());
        model.addAttribute("slsp", spdao.count());
        model.addAttribute("slhd", hddao.count());
        model.addAttribute("slspht", spdao.countByTrangthaisp(false));
        model.addAttribute("listhdtt", hddao.findHdTt(1));

        Date currentDate = new Date();

        // Lấy ngày cách đây 5 ngày
        long fiveDaysInMillis = 5 * 24 * 60 * 60 * 1000;
        Date fiveDaysAgo = new Date(currentDate.getTime() - fiveDaysInMillis);
        model.addAttribute("khm", khdao.findNewCustomers(fiveDaysAgo));

        List<Object[]> revenueData = hddao.calculateRevenueByMonth();
        model.addAttribute("revenueData", revenueData);


           model.addAttribute("souser", khdao.calculateUserRegistrationByMonth());

        model.addAttribute("soOrder", khdao.calculateOrdersByMonth());

        return "admin/view/dashboard";
    }

    // @ModelAttribute
    // public void setthoigianhd() {
    // // Date currentDate = new Date();
    // // Date dateToCompare = new Date(this.mua.getTime() + 3 * 24 * 60 * 60 *
    // 1000); // Ngày đặt + 3 ngày
    // // List<HoaDon> listhd = hddao.findAll();
    // // for (HoaDon hoaDon : listhd) {
    // // if()
    // // }
    // }
}
