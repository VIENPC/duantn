package com.poly.controller;

import java.io.File;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.poly.dao.KhachHangDAO;
import com.poly.dao.SanPhamDAO;
import com.poly.dao.TinTucDAO;
import com.poly.entities.KhachHang;
import com.poly.entities.SanPham;
import com.poly.entities.TinTuc;

@Controller
@RequestMapping("admin")
public class TinTucController {

    @Autowired
    KhachHangDAO khdao;

    @Autowired
    TinTucDAO tindao;

    @Autowired
    SanPhamDAO spdao;

    @Autowired
    ServletContext app;

    @RequestMapping("/qltintuc")
    public String qltintuc(Model model) {
        model.addAttribute("listsp", spdao.findAll());
        model.addAttribute("listtin", tindao.findAll());
        return "admin/view/qltintuc";
    }

    @RequestMapping("/addtintuc")
    public String addtintuc(Model model) {
        TinTuc tintuc = new TinTuc();
        model.addAttribute("tt", tintuc);

        model.addAttribute("listsp", spdao.findAll());
        return "admin/view/add_Tintuc";

    }

    @RequestMapping("/qltintuc/save")
    public String savetin(Model model, @Validated @ModelAttribute("tt") TinTuc tin, Errors errors,
            @RequestParam("hinh") MultipartFile img) {
        if (errors.hasErrors() || img.isEmpty()) {
            // TinTuc tintuc = new TinTuc();
            // model.addAttribute("tt", tintuc);
            if (img.isEmpty()) {
                model.addAttribute("message_img", "Chọn hình ảnh!");
            }
            model.addAttribute("listsp", spdao.findAll());
            return "admin/view/add_Tintuc";
        } else {
            try {
                String filename = img.getOriginalFilename();
                File file = new File(app.getRealPath("/images/blog/" + filename));
                if (!file.exists()) {
                    img.transferTo(file);

                }

                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                KhachHang kh = khdao.findById(username).get();
                LocalDateTime now = LocalDateTime.now();
                tin.setKhachhang(kh);
                tin.setNgaydang(now);
                tin.setHinhanh(filename);
                tindao.save(tin);
            } catch (Exception e) {
                System.out.println(e);
            }

        }
        return "redirect:/admin/qltintuc?success=addtin";
    }

    @RequestMapping("/updatetin")
    public String updatetin(HttpServletRequest request) {
        Integer masp = Integer.parseInt(request.getParameter("masp"));
        Integer id = Integer.parseInt(request.getParameter("id"));
        SanPham sp = spdao.findById(masp).get();
        TinTuc tin = tindao.findById(id).get();
        tin.setTieude(request.getParameter("tieude"));
        tin.setSanpham(sp);
        tin.setNoidung(request.getParameter("noidung"));
        tindao.save(tin);
        System.out.println(masp);
        return "redirect:/admin/qltintuc?success=uptin";
    }

    @RequestMapping("qltintuc/deletetin/{idtin}")
    public String xoatin(@PathVariable("idtin") Integer idtin) {
        tindao.deleteById(idtin);
        return "redirect:/admin/qltintuc?success=deltin";
    }
}
