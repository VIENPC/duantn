package com.poly.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.dao.DanhMucDAO;
import com.poly.dao.SanPhamDAO;
import com.poly.dao.ThuongHieuDAO;
import com.poly.entities.NhomSanPham;
import com.poly.entities.ThuongHieu;

@Controller
@RequestMapping("admin")
public class SPBanChayController {
    @Autowired
    SanPhamDAO spdao;

    @Autowired
    DanhMucDAO cdao;
    @Autowired
    ThuongHieuDAO hieuDAO;

    @RequestMapping("/spbanchay")
    public String spbc(Model model, @RequestParam(name = "thang", required = false) Integer thang) {

        model.addAttribute("listhh", hieuDAO.findAll());
        model.addAttribute("listdanhmuc", cdao.findAll());
        model.addAttribute("listspbc", spdao.findBestSellingProducts());
        if (thang != null) {
            // Đã chọn tháng, thực hiện xử lý
            System.out.println("Tháng: " + thang);
            // Gọi phương thức findBestSellingProducts với tháng thích hợp
            model.addAttribute("listspbctheomonth", spdao.findBestSellingProductsMonth(thang));
            model.addAttribute("thang1", thang);
        } else {
            // Lấy ngày hôm nay
            LocalDate ngayHomNay = LocalDate.now();

            // Lấy tháng của ngày hôm nay
            int thangHomNay = ngayHomNay.getMonthValue();

            System.out.println("Tháng hôm nay: " + thangHomNay);
            model.addAttribute("listspbctheomonth", spdao.findBestSellingProductsMonth(thangHomNay));
            model.addAttribute("thangHomNay", thangHomNay);
            // Hiển thị trang ban đầu (chưa chọn tháng)

        }
        return "admin/view/spbanchay";
    }

    @RequestMapping("/spbanchay/category")
    public String spbcbycategory(Model model,

            @RequestParam("categoryid") int categoryid) {
        model.addAttribute("listhh", hieuDAO.findAll());
        model.addAttribute("listdanhmuc", cdao.findAll());

        model.addAttribute("listspbc", spdao.findBestSellingProductsByCategory(categoryid));
        NhomSanPham nsp = cdao.findById(categoryid).get();
        String ten = "loại:" + nsp.getTennhom();
        model.addAttribute("ten", ten);

        return "admin/view/spbanchay";
    }

    @RequestMapping("/spbanchay/brand")
    public String spbcbybrand(Model model,

            @RequestParam("thid") int id) {
        model.addAttribute("listhh", hieuDAO.findAll());
        model.addAttribute("listdanhmuc", cdao.findAll());

        model.addAttribute("listspbc", spdao.findBestSellingProductsByBrand(id));
        ThuongHieu nsp = hieuDAO.findById(id).get();
        String ten = "thương hiệu:" + nsp.getTenthuonghieu();
        model.addAttribute("ten", ten);

        return "admin/view/spbanchay";
    }

}
