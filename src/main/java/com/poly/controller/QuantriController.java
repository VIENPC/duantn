package com.poly.controller;

import java.io.File;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.dao.DanhMucDAO;
import com.poly.dao.HoaDonDAO;
import com.poly.dao.KhachHangDAO;
import com.poly.dao.SanPhamDAO;
import com.poly.dao.ThuongHieuDAO;
import com.poly.entities.GiamGia;
import com.poly.entities.NhomSanPham;
import com.poly.entities.SanPham;
import com.poly.entities.ThuongHieu;

@Controller
@RequestMapping("admin")
public class QuantriController {
	@Autowired
	KhachHangDAO khdao;
	@Autowired
	SanPhamDAO spdao;
	@Autowired
	HoaDonDAO hddao;

	@Autowired
	ServletContext app;

	@Autowired
	ThuongHieuDAO hieuDAO;
	@Autowired
	DanhMucDAO dmdao;

	@RequestMapping("/qlhieu")
	public String qlhieu(Model model) {

		model.addAttribute("listhieu", hieuDAO.findAll());
		// List<ThuongHieu> listhieu = hieuDAO.findAll();
		// for (ThuongHieu list : listhieu) {
		// System.out.println(list.getHinhanh());
		// }
		return "admin/view/qlhieu";
	}

	@RequestMapping("/qlhieu/addhieu")
	public String addqlhieu(HttpServletRequest request, @RequestParam("hinhthuonghieu") MultipartFile img) {
		try {
			ThuongHieu th = new ThuongHieu();
			String tenth = request.getParameter("tenthuonghieu");
			List<ThuongHieu> listhieu = hieuDAO.findAll();
			for (ThuongHieu existingBrand : listhieu) {
				if (tenth.equalsIgnoreCase(existingBrand.getTenthuonghieu())) {
					return "redirect:/admin/qlhieu?fail=addhieutt";
				}
			}
			th.setTenthuonghieu(tenth);
			String filename = img.getOriginalFilename();
			File file = new File(app.getRealPath("/images/brand/" + filename));
			if (!file.exists()) {

				img.transferTo(file);

			}

			th.setHinhanh(filename);
			hieuDAO.save(th);
			// th.setHinhanh(request.getParameter("hinhanh"));

			return "redirect:/admin/qlhieu?success=addhieu";
		} catch (Exception e) {
			System.out.println(e);
			return "redirect:/admin/qlhieu?fail=addhieu";
		}

	}

	@RequestMapping("/qlhieu/upth")
	public String updatehieu(HttpServletRequest request, @RequestParam("hinhthuonghieu") MultipartFile img) {
		try {
			Integer idhieu = Integer.parseInt(request.getParameter("mahieu"));
			System.out.println(idhieu);
			ThuongHieu th = hieuDAO.findById(idhieu).get();
			String tenth = request.getParameter("tenthuonghieu");
			// System.out.println(idhieu);

			th.setTenthuonghieu(tenth);
			String filename = img.getOriginalFilename();
			File file = new File(app.getRealPath("/images/brand/" + filename));
			if (!file.exists()) {

				img.transferTo(file);

			}
			th.setHinhanh(filename);
			hieuDAO.save(th);

			return "redirect:/admin/qlhieu?success=uphieu";
		} catch (Exception e) {
			// TODO: handle exception
			return "redirect:/admin/qlhieu?fail=uphieufail";
		}

	}

	@RequestMapping("qlhieu/deleteth/{id}")
	public String deleteth(@PathVariable("id") Integer id) {

		try {
			hieuDAO.deleteById(id);

			return "redirect:/admin/qlhieu?success=delth";
		} catch (Exception e) {
			return "redirect:/admin/qlhieu?fail=delth";
		}
	}

	@GetMapping("/fineOne")
	@ResponseBody
	public ThuongHieu findOne(Integer id) {

		return hieuDAO.findById(id).get();

	}

	

	@RequestMapping("/baocaochung")
	public String baocaochung(Model model) {
		model.addAttribute("listhh", hieuDAO.findAll());
		model.addAttribute("listdm", dmdao.findAll());
		model.addAttribute("slsp", spdao.count());
		model.addAttribute("slhd", hddao.count());
		model.addAttribute("slspht", spdao.countByTrangthaisp(false));
		model.addAttribute("tkhk", khdao.countKhachhangByTrangthaikh2(2));
		model.addAttribute("sldhh", hddao.countDontt(5));
		Long tongtien = hddao.getTongTienTatCaHoaDon();
		if (tongtien == null) {
			model.addAttribute("tthoadon", 0);
		} else {
			model.addAttribute("tthoadon", tongtien);
		}
		// model.addAttribute("listspbc", spdao.findSanPhamBanChay());
		model.addAttribute("listspht", spdao.findByTtsp(false));
		model.addAttribute("listspbte", spdao.findProductsNotInAnyOrder(true));
		return "admin/view/baocaochung";
	}

	@RequestMapping("spbanchamdm")
	public String spbanchamdm(Model model, @RequestParam("categoryid") int categoryid) {
		model.addAttribute("listhh", hieuDAO.findAll());
		model.addAttribute("listdm", dmdao.findAll());
		List<Object[]> list = spdao.findProductsNotInAnyOrderForCategory(categoryid);
		NhomSanPham nsp = dmdao.findById(categoryid).get();
		String ten = "loại:" + nsp.getTennhom();
		model.addAttribute("ten", ten);
		model.addAttribute("listspbte", list);
		model.addAttribute("listhh", hieuDAO.findAll());
		model.addAttribute("listdm", dmdao.findAll());
		model.addAttribute("slsp", spdao.count());
		model.addAttribute("slhd", hddao.count());
		model.addAttribute("slspht", spdao.countByTrangthaisp(false));
		model.addAttribute("tkhk", khdao.countKhachhangByTrangthaikh2(2));
		model.addAttribute("sldhh", hddao.countDontt(5));
		Long tongtien = hddao.getTongTienTatCaHoaDon();
		if (tongtien == null) {
			model.addAttribute("tthoadon", 0);
		} else {
			model.addAttribute("tthoadon", tongtien);
		}

		model.addAttribute("listspht", spdao.findByTtsp(false));
		return "admin/view/baocaochung";
	}

	@RequestMapping("spbanchamth")
	public String spbanchamth(Model model, @RequestParam("thid") int thid) {
		model.addAttribute("listhh", hieuDAO.findAll());
		model.addAttribute("listdm", dmdao.findAll());
		List<Object[]> list = spdao.findProductsNotInAnyOrderForBrand(thid);
		ThuongHieu nsp = hieuDAO.findById(thid).get();
		String ten = "thương hiệu:" + nsp.getTenthuonghieu();
		model.addAttribute("ten", ten);
		model.addAttribute("listspbte", list);
		model.addAttribute("listhh", hieuDAO.findAll());
		model.addAttribute("listdm", dmdao.findAll());
		model.addAttribute("slsp", spdao.count());
		model.addAttribute("slhd", hddao.count());
		model.addAttribute("slspht", spdao.countByTrangthaisp(false));
		model.addAttribute("tkhk", khdao.countKhachhangByTrangthaikh2(2));
		model.addAttribute("sldhh", hddao.countDontt(5));
		Long tongtien = hddao.getTongTienTatCaHoaDon();
		if (tongtien == null) {
			model.addAttribute("tthoadon", 0);
		} else {
			model.addAttribute("tthoadon", tongtien);
		}

		model.addAttribute("listspht", spdao.findByTtsp(false));
		return "admin/view/baocaochung";
	}
}
