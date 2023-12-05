package com.poly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.dao.DanhGiaDAO;


@Controller
@RequestMapping("admin")
public class DanhGiaController {

    @Autowired
    DanhGiaDAO dgdao; 
    
    @RequestMapping("/qldanhgia")
	public String qldanhgia(Model model) {
      
        model.addAttribute("results", dgdao.countReviewsAndStars());

		return "admin/view/danhgia";
	}
     @RequestMapping("/xemctdanhgia/{masp}")
	public String xemdanhgia(Model model, @PathVariable("masp") Integer masp) {
      
        model.addAttribute("listdg", dgdao.loaddgByMasp(masp));

		return "admin/view/chitietdg";
	}   
  @RequestMapping("/qldanhgia/deletedg/{masp}")
	public String deletedg(Model model, @PathVariable("masp") Integer masp) {
      
      dgdao.deleteById(masp);

		return "redirect:/admin/qldanhgia?success=deldg";
	}   
}  
 