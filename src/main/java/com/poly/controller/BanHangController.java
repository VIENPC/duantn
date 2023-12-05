package com.poly.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BanHangController {
    @GetMapping("")
    public String index() {

        return "banhang/view/index";
    }

    @RequestMapping("/index")
    public String index2() {

        return "banhang/view/index";
    }

    @RequestMapping("/cart")
    public String cart() {

        return "banhang/view/cart";
    }
    
    @RequestMapping("/account")
	public String edit(Model model) {
		return "banhang/view/account";
	}
    

    @RequestMapping("/contact")
    public String contact() {

        return "banhang/view/contact";
    }

    @RequestMapping("/shop")
    public String category() {

        return "banhang/view/category";
    }

    @RequestMapping("/checkout")
    public String checkout() {

        return "banhang/view/checkout";
    }

    @RequestMapping("/product-details/{masp}")
    public String prodct_details() {

        return "banhang/view/single-product";
    }
     
    @RequestMapping("/confirmation")
    public String confirmation() {

        return "banhang/view/confirmation";
    }

    @RequestMapping("/login")
    public String login() {

        return "banhang/view/login";
    }
    

    @RequestMapping("/tracking")
    public String tracking() {

        return "banhang/view/tracking";
    }

    @RequestMapping("/blog")
    public String blog() {

        return "banhang/view/blog";
    }
    @RequestMapping("/success")
    public String success() {

        return "banhang/view/sucessful";
    }
    @RequestMapping("/blog-signle/{id}")
    public String singleblog() {

        return "banhang/view/single-blog";
    }
}
