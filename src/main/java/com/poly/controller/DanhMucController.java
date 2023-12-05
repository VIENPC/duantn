package com.poly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.dao.DanhMucDAO;
import com.poly.entities.NhomSanPham;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("admin")
public class DanhMucController {

    @Autowired
    DanhMucDAO dMucDAO;

    @RequestMapping("/qldanhmuc")
    public String qldanhmuc(Model model) {
        NhomSanPham danhmuc = new NhomSanPham();
        model.addAttribute("dm", danhmuc);
        model.addAttribute("listdanhmuc", dMucDAO.findAll());
        return "admin/view/qldanhmuc";
    }

    @RequestMapping("/qldanhmuc/themmuc")
    public String addmuc(@ModelAttribute("dm") NhomSanPham dm) {
        List<NhomSanPham> listdm = dMucDAO.findAll();
        for(NhomSanPham dms : listdm){
            if (dm.getTennhom().equalsIgnoreCase(dms.getTennhom())) {
					return "redirect:/admin/qlhieu?fail=adddmtt";
				}
        }
        dMucDAO.save(dm);

        return "redirect:/admin/qldanhmuc?success=addmuc";
    }

    @RequestMapping("/qldanhmuc/updatedm")
    public String updatedm(HttpServletRequest request) {
        String madm = request.getParameter("manhom");
        NhomSanPham danhmuc = dMucDAO.findById(Integer.parseInt(madm)).get();

        danhmuc.setTennhom(request.getParameter("tendanhmuc"));

        String trangthai = request.getParameter("tinhtrang");
        Boolean tt = Boolean.parseBoolean(trangthai);
        

        danhmuc.setTrangthai(tt);
        dMucDAO.save(danhmuc);

        return "redirect:/admin/qldanhmuc?success=updatedm";
    }

    @RequestMapping("/qldanhmuc/deletedm/{madm}")
    public String deletesp(@PathVariable("madm") Integer madm) {
        try {
            // Gọi service hoặc repository để xóa sản phẩm dựa vào id
            dMucDAO.deleteById(madm);

            return "redirect:/admin/qldanhmuc?success=deldm";
        } catch (Exception e) {
            // Chuyển hướng tới trang hiển thị thông báo xóa không thành công

            return "redirect:/admin/qldanhmuc?fail=deldm";
        }

    }
}
