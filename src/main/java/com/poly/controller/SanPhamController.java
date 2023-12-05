package com.poly.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

import org.springframework.validation.Errors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import com.poly.dao.DanhMucDAO;
import com.poly.dao.HinhAnhDAO;
import com.poly.dao.HoaDonCTDAO;
import com.poly.dao.HoaDonDAO;
import com.poly.dao.SanPhamDAO;
import com.poly.dao.ThuongHieuDAO;
import com.poly.entities.HinhSP;
import com.poly.entities.NhomSanPham;
import com.poly.entities.SanPham;
import com.poly.entities.ThuongHieu;

@Controller
@RequestMapping("admin")
public class SanPhamController {
	@Autowired
	ServletContext app;

	@Autowired
	ThuongHieuDAO thdao;

	@Autowired
	DanhMucDAO dmdao;

	@Autowired
	SanPhamDAO spdao;

	@Autowired
	HoaDonDAO hddao;

	@Autowired
	HoaDonCTDAO hdctdao;

	@Autowired
	HinhAnhDAO hadao;

	@RequestMapping("/qlsanpham")
	public String qlsanpham(Model model) {

		model.addAttribute("listsp", spdao.findAll());

		return "admin/view/product";

	}

	@RequestMapping("/addsanpham")
	public String addsanpham(Model model) {
		SanPham sp = new SanPham();
		model.addAttribute("sp", sp);
		model.addAttribute("listth", thdao.findAll());
		model.addAttribute("listdm", dmdao.findAll());
		model.addAttribute("listsp", spdao.findAll());
		return "admin/view/add_product";
	}

	@RequestMapping("/addsanpham/{masp}")
	public String editmasp(Model model, @PathVariable("masp") Integer masp) {
		SanPham sp = spdao.findById(masp).get();
		// System.out.println(sp.getHinhanh());
		model.addAttribute("listth", thdao.findAll());
		model.addAttribute("listdm", dmdao.findAll());
		model.addAttribute("listsp", spdao.findAll());

		model.addAttribute("sp", sp);
		return "admin/view/updatesp";
	}

	// @Transactional
	@RequestMapping("/addsanpham/save")
	public String add_sp(Model model, @Validated @ModelAttribute("sp") SanPham sp, Errors errors,
			@RequestParam("hinh_sp") MultipartFile img) {
		try {
			if (errors.hasErrors() || img.isEmpty()) {
				if (img.isEmpty()) {
					model.addAttribute("message_img", "Vui lòng chọn hình ảnh");
				}

				model.addAttribute("sp", sp);
				model.addAttribute("listth", thdao.findAll());
				model.addAttribute("listdm", dmdao.findAll());
				return "admin/view/add_product";

			} else {
				LocalDateTime now = LocalDateTime.now();
				String filename = img.getOriginalFilename();
				File file = new File(app.getRealPath("/images/sanpham/" + filename));
				if (!file.exists()) {
					img.transferTo(file);

				}

				sp.setHinhanh(filename);

				sp.setNgaynhap(now);
				sp.setNgaycapnhat(now);

				spdao.save(sp);
				return "redirect:/admin/addsanpham?success=add";
			 }

		} catch (Exception e) {
			System.out.println(e);
			return "redirect:/admin/addsanpham?fail=add";
		}

	}

	@RequestMapping("/qlsanpham/updatesp")
	public String updatesp(HttpServletRequest request) {
		try {
			Integer masp = Integer.parseInt(request.getParameter("masp_hidden"));

			Integer sl = Integer.parseInt(request.getParameter("soluong"));
			Integer giasp = Integer.parseInt(request.getParameter("giasp"));

			Boolean tt = Boolean.parseBoolean(request.getParameter("trangthai"));
			LocalDateTime now = LocalDateTime.now();
			SanPham sp = spdao.findById(masp).get();
			sp.setMasp(sp.getMasp());
			sp.setTensp(request.getParameter("tensp"));
			sp.setSoluong(sl);
			sp.setTrangthaisp(tt);
			sp.setNgaycapnhat(now);
			sp.setGiasp(giasp);
			spdao.save(sp);

			return "redirect:/admin/qlsanpham?success=updatesp";
		} catch (Exception e) {

			return "redirect:/admin/qlsanpham?fail=updatesp";
		}

	}

	@RequestMapping("/updatesp/save")
	public String update(Model model, @Validated @ModelAttribute("sp") SanPham sp, Errors errors,
			@RequestParam("hinh_sp") MultipartFile img)
			throws IllegalStateException, IOException {
		if (errors.hasErrors()) {

			model.addAttribute("listth", thdao.findAll());
			model.addAttribute("listdm", dmdao.findAll());
			return "admin/view/updatesp";
		} else {
			LocalDateTime now = LocalDateTime.now();
			String filename = img.getOriginalFilename();
			SanPham sp2 = spdao.findById(sp.getMasp()).get();
			File file = new File(app.getRealPath("/images/sanpham/" + filename));
			if (!file.exists()) {
				img.transferTo(file);

			}
			System.out.println(img.getOriginalFilename());

			if (filename != null && !filename.isEmpty()) {
				sp2.setHinhanh(filename);
			}

			// System.out.println(sp.getThuonghieu());

			sp2.setTensp(sp.getTensp());
			sp2.setSoluong(sp.getSoluong());
			sp2.setGiasp(sp.getGiasp());
			sp2.setTrangthaisp(sp.getTrangthaisp());
			sp2.setThongtin(sp.getThongtin());
			sp2.setThongsokythuat(sp.getThongsokythuat());
			sp2.setNhomsanpham(sp.getNhomsanpham());
			sp2.setThuonghieu(sp.getThuonghieu());
			sp2.setNgaycapnhat(now);
			spdao.save(sp2);
			return "redirect:/admin/qlsanpham?success=updatesp";
		}

	}

	@RequestMapping("/qlsanpham/deletesp/{masp}")
	public String deletesp(@PathVariable("masp") Integer masp, RedirectAttributes redirectAttributes) {
		try {
			// Gọi service hoặc repository để xóa sản phẩm dựa vào id
			spdao.deleteById(masp);

			// Chuyển hướng tới trang hiển thị thông báo xóa thành công
			// redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sản phẩm thành
			// công!");
			return "redirect:/admin/qlsanpham?success=delete";
		} catch (Exception e) {
			// Chuyển hướng tới trang hiển thị thông báo xóa không thành công
			// redirectAttributes.addFlashAttribute("errorMessage", "Xóa sản phẩm không
			// thành công: " + e.getMessage());
			return "redirect:/admin/qlsanpham?fail=delete";
		}

	}

	// upload hinh ảnh
	@PostMapping("/upload")
	public String handleFileUpload(@RequestPart("files") MultipartFile[] files,
			@RequestParam("productId") Integer productId) {

		try {

			SanPham sp = spdao.findById(productId).get();
			for (MultipartFile img : files) {
				String filename = img.getOriginalFilename();
				File file = new File(app.getRealPath("/images/sanpham/" + filename));
				if (!file.exists()) {
					img.transferTo(file);

				}
				HinhSP hinh = new HinhSP();
				hinh.setHinh(filename);
				hinh.setSanpham(sp);
				hadao.save(hinh);
				System.out.println("thành công add hình");
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		// System.out.println();
		return "redirect:/admin/addsanpham?success=uphinh";
	}

	@ModelAttribute
	public void globalModelAttributes(Model model) {

		List<SanPham> sp = spdao.findAll();
		for (SanPham sanPham : sp) {
			if (sanPham.getSoluong() == 0) {
				sanPham.setTrangthaisp(false);
				spdao.save(sanPham);
			} else {
				sanPham.setTrangthaisp(true);
				spdao.save(sanPham);
			}
		}
	}
}
