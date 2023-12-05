package com.poly.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.dao.AuthorityDAO;
import com.poly.dao.GioHangDAO;
import com.poly.dao.KhachHangDAO;

import com.poly.dao.VaiTroDAO;
import com.poly.entities.GioHang;
import com.poly.entities.KhachHang;

import com.poly.entities.Authority;
import com.poly.service.CustomerService;
import com.poly.service.MailerServiceImpl;
import com.poly.service.SessionService;

import net.bytebuddy.utility.RandomString;

@Controller
public class TaiKhoanController {

	@Autowired
	CustomerService customerServive;
	@Autowired
	KhachHangDAO khdao;

	@Autowired
	GioHangDAO ghdao;

	@Autowired
	SessionService service;

	@Autowired
	HttpSession session;
	@Autowired
	MailerServiceImpl mailer;

	@Autowired
	AuthorityDAO audao;

	@Autowired
	VaiTroDAO rdao;

	@Autowired
	BCryptPasswordEncoder pe;

	double mxn = Math.round(Math.random() * 999999) + 111122;

	@RequestMapping("/auth/login/form")
	public String form(Model model) {
		KhachHang kh = new KhachHang();
		model.addAttribute("kh", kh);
		return "banhang/view/login";
	}

	@GetMapping("/auth/forgot-password")
	public String forgotPasswordForm(Model model) {
		return "banhang/view/forgot-password";
	}

	@PostMapping("/auth/forgot-password")
	public String processForgotPassword(@RequestParam("email") String email, HttpServletRequest request, Model model)
			throws Exception {

		List<KhachHang> acc = customerServive.findAll();
		boolean found = false; // Biến để kiểm tra xem có khách hàng nào có email tương ứng không

		for (KhachHang khachHang : acc) {
			if (khachHang.getEmail() != null && khachHang.getEmail().equals(email)) {
				try {
					String token = RandomString.make(50);
					customerServive.updateToken(token, email);
					String resetLink = getSiteURL(request) + "/auth/reset-password?token=" + token;
					mailer.sendEmail(email, resetLink);
					model.addAttribute("message", "Chúng tôi đã gửi một liên kết đặt lại mật khẩu đến email của bạn "
							+ "Nếu không tìm thấy email, bạn vui lòng kiểm tra mục thư rác.");
					found = true; // Đánh dấu rằng đã tìm thấy email khớp
					break;
				} catch (MessagingException e) {
					e.printStackTrace();
					model.addAttribute("error", "Nếu bạn không thấy email, hãy kiểm tra thư mục thư rác của bạn");
				}
			}
		}

		if (!found) {
			model.addAttribute("message", "Không tìm thấy bất kỳ tài khoản nào với Email này");
		}

		return "banhang/view/forgot-password";
	}

	@GetMapping("/auth/reset-password")
	public String resetPasswordForm(@Param(value = "token") String token, Model model) {

		KhachHang account = customerServive.getByToken(token);

		model.addAttribute("token", token);
		if (account == null) {
			model.addAttribute("message", "Mã không hợp lệ!");
			return "redirect:/auth/login/form";
		}
		return "banhang/view/reset-password";
	}

	@PostMapping("/auth/reset-password")
	public String processResetPassword(@RequestParam("token") String code, @RequestParam("password") String password,
			HttpServletResponse response, Model model) {
		boolean hasUppercase = !password.equals(password.toLowerCase());
		KhachHang token = customerServive.getByToken(code);
		if (token == null) {
			model.addAttribute("message", "Mã không hợp lệ!");
		} else if (password.length() < 6) {
			model.addAttribute("message", "Mật khẩu phải có ít nhất 6 ký tự");
		} else if (!hasUppercase) {
			model.addAttribute("message", "Mật khẩu phải chứa ít nhất 1 chữ cái viết hoa");
		} else {
			customerServive.updatePassword(token, password);
			model.addAttribute("message", "Bạn đã thay đổi mật khẩu thành công!");
			response.addHeader("refresh", "2;url=/auth/login/form");
		}
		return "banhang/view/reset-password";
	}

	@GetMapping("/auth/change-password")
	public String changePasswordForm(Model model) {
		return "banhang/view/change-password";
	}

	@PostMapping("/auth/change-password")
	public String processChangePassword(Model model, @RequestParam("username") String username,
			@RequestParam("password") String Password, @RequestParam("Newpassword") String NewPassword) {

		// if (NewPassword.equals("")) {
		// model.addAttribute("message", "Vui lòng nhập mật khẩu!");
		// } else if (Password.equals("")) {
		// model.addAttribute("message", "Vui lòng nhập lại mật khẩu!");
		//
		// } else {
		//
		//
		// customerServive.changePassword(account, Password);
		// model.addAttribute("message", "Đổi mật khẩu thành công!");
		// return "banhang/view/login";
		// }
		//
		// return "banhang/view/change-password";

		// Kiểm tra nếu username thay đổi
		boolean hasUppercase = !Password.equals(Password.toLowerCase());
		if (!username.equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
			model.addAttribute("message", "Không được thay đổi username!");
			return "banhang/view/change-password";
		}

		if (NewPassword.equals("")) {
			model.addAttribute("message", "Vui lòng nhập mật khẩu!");
		} else if (Password.equals("")) {
			model.addAttribute("message", "Vui lòng nhập lại mật khẩu!");
		} else if (Password.length() < 6) {
			model.addAttribute("message", "Mật khẩu phải có ít nhất 6 ký tự");
		} else if (!hasUppercase) {
			model.addAttribute("message", "Mật khẩu phải chứa ít nhất 1 chữ cái viết hoa");
		} else {
			KhachHang account = customerServive.findById(username);
			customerServive.changePassword(account, Password);
			model.addAttribute("message", "Đổi mật khẩu thành công!");
			return "banhang/view/login";
		}

		return "banhang/view/change-password";
	}

	@RequestMapping("/auth/signup/form")
	public String index(Model model) {
		model.addAttribute("account", new KhachHang());
		return "banhang/view/register";
	}

	@RequestMapping("create")
	public String create(KhachHang account, @RequestParam("email") String email, Model model,
			@RequestParam("passwordkh") String password, @RequestParam("confirm") String confirm,
			@RequestParam("usernamekh") String username) throws IllegalStateException, IOException, MessagingException {
		boolean hasUppercase = !password.equals(password.toLowerCase());
		List<KhachHang> all = khdao.findAll();
		for (KhachHang account2 : all) {
			if (account2.getUsernamekh() != null && account2.getUsernamekh().equals(username)) {
				model.addAttribute("account", account);
				model.addAttribute("message", "Tên đăng nhập đã được sử dụng ");
				return "banhang/view/register";
			} else if (account2.getEmail() != null && account2.getEmail().equals(email)) {
				model.addAttribute("account", account);
				model.addAttribute("message", "Email đã được sử dụng ");
				return "banhang/view/register";
			} else if (email == null || email.isEmpty()) {
				model.addAttribute("account", account);
				model.addAttribute("message", "Email không được bỏ trống");
				return "banhang/view/register";
			} else if (username == null || username.isEmpty()) {
				model.addAttribute("account", account);
				model.addAttribute("message", "Tên đăng nhập không được bỏ trống");
				return "banhang/view/register";
			} else if (password.length() < 6) {
				model.addAttribute("account", account);
				model.addAttribute("message", "Mật khẩu phải có ít nhất 6 ký tự");
				return "banhang/view/register";
			} else if (!hasUppercase) {
				model.addAttribute("account", account);
				model.addAttribute("message", "Mật khẩu phải chứa ít nhất 1 chữ cái viết hoa");
				return "banhang/view/register";
			}
		}

		Integer ma = (int) mxn;

		String thongBao = "" + "<div style='font-family:Roboto,sans-serif;width:50%;margin:0 auto;text-align:center'>"
				+ "\"<div style='font-size:3em;padding:0.5em 1em'><b>Mã xác nhận tài khoản</b></div>\""
				+ "<div style='background-color:#f0f8ff;font-size:16px;padding:1em 3em'>" + "\r\n"
				+ "Dưới đây là mã xác nhận code của quý khách:\r\n" + "\r\n <br>	" + ma + "</br>" + "\r\n"
				+ "\r\n <br>	"
				+ "Vui lòng sử dụng mã xác nhận này để tiếp tục các hoạt động và giao dịch trên tài khoản của quý khách. Chúng tôi khuyến nghị quý khách không tiết lộ mã xác nhận  này cho bất kỳ ai khác và không gửi mã này qua email hay tin nhắn điện thoại.\r\n <br>	"
				+ "\r\n"
				+ "Nếu quý khách không yêu cầu hoặc không nhớ có bất kỳ hoạt động liên quan đến mã xác nhận này, vui lòng liên hệ với bộ phận hỗ trợ khách hàng của chúng tôi ngay để được hỗ trợ và đảm bảo an toàn cho tài khoản của quý khách.\r\n <br>	"
				+ "\r\n" + "Xin chân thành cảm ơn quý khách hàng đã sử dụng dịch vụ của chúng tôi.\r\n" + "\r\n <br>"
				+ "Trân trọng,\r\n" + "NVA3-Watches";

		if (confirm.equals(password)) {
			mailer.send(email, "YÊU CẦU MÃ XÁC NHẬN TỪ NGƯỜI DÙNG!", thongBao);
			service.set("mxn", ma);

			service.set("account", account);

			return "banhang/view/confirm";
		} else {
			model.addAttribute("account", account);
			model.addAttribute("message", "Xác nhận mật khẩu không chính xác");
			return "banhang/view/register";
		}

	}

	@RequestMapping("confirm")
	public String Confirm(Model model, @RequestParam("confirm") Integer confirm) {
		Integer ma = service.get("mxn");
		if (confirm == null) {
			model.addAttribute("error", "Mã Xác Nhận Không Chính Xác!");
			return "banhang/view/confirm";
		} else {
			if (!confirm.equals(ma)) {
				model.addAttribute("error", "Mã Xác Nhận Không Chính Xác!");
				return "banhang/view/confirm";
			} else {
				KhachHang item = service.get("account");
				// item.setCreatedate(new Date());
				model.addAttribute("item", item);
				item.setTrangthaikh(1);
				item.setGioitinh(true);
				item.setPasswordkh(pe.encode(item.getPasswordkh()));
				khdao.save(item);
				Authority au = new Authority();
				au.setKhachhang(item);
				au.setRole(rdao.findById("USER").get());
				audao.save(au);
			}
		}
		return "banhang/view/login";
	}

	@RequestMapping("/auth/login/success")
	public String success(Model model) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		System.out.println(authentication);
		KhachHang kha = khdao.findById(username).get();
		if (kha.getDiachi() == null || kha.getDienthoai() == null || kha.getHotenkh() == null
				|| kha.getGioitinh() == null) {
			model.addAttribute("message", "Vui lòng cập nhật đầy đủ thông tin tài khoản!");
			// System.out.println(authentication.getName());
			GioHang gh = null;

			KhachHang kh = khdao.findById(authentication.getName()).get();
			gh = ghdao.findByMaKH(kh.getUsernamekh());
			if (gh == null) {
				// Giỏ hàng không tồn tại, tạo giỏ hàng mới
				gh = new GioHang();
				gh.setKhachhang(kh);
				ghdao.save(gh);
			}
			return "banhang/view/account";
		} else {

			List<String> authList = new ArrayList<>();

			// Check if the user is authenticated
			if (authentication != null && authentication.isAuthenticated()) {
				List<String> roleNames = customerServive.getRolesByUsername(authentication.getName());

				for (String roleName : roleNames) {
					authList.add("ROLE_" + roleName);
				}
			}

			if (authList.contains("ROLE_ADMIN")) {
				System.out.println(authentication.getName());
				GioHang gh = null;

				KhachHang kh = khdao.findById(authentication.getName()).get();
				gh = ghdao.findByMaKH(kh.getUsernamekh());
				if (gh == null) {
					// Giỏ hàng không tồn tại, tạo giỏ hàng mới
					gh = new GioHang();
					gh.setKhachhang(kh);
					ghdao.save(gh);
				}
				return "redirect:/admin/home";
			} else {
				System.out.println(authentication.getName());
				GioHang gh = null;

				KhachHang kh = khdao.findById(authentication.getName()).get();
				gh = ghdao.findByMaKH(kh.getUsernamekh());
				if (gh == null) {
					// Giỏ hàng không tồn tại, tạo giỏ hàng mới
					gh = new GioHang();
					gh.setKhachhang(kh);
					ghdao.save(gh);
				}
				return "redirect:/index";
			}
		}
	}

	@RequestMapping("/auth/access/denied")
	public String denied(Model model) {

		return "error404";
	}

	@RequestMapping("/auth/login/error")
	public String error(Model model) {
		model.addAttribute("message", "Sai thông tin đăng nhập!");
		return "forward:/auth/login/form";
	}

	@RequestMapping("/auth/logoff/success")
	public String logoff(Model model) {

		return "forward:/auth/login/form";
	}

	/*
	 * @RequestMapping("/auth/sign-up") public String signup(Model
	 * model, @ModelAttribute("kh") KhachHang kh, HttpServletResponse response,
	 * RedirectAttributes redirectAttributes) { try { List<KhachHang> khh =
	 * khdao.findAll(); for (KhachHang itkh : khh) { if
	 * (itkh.getUsernamekh().equals(kh.getUsernamekh())) {
	 * 
	 * return "redirect:/auth/login/form?fail=dk"; } } String url =
	 * "http://localhost:8080/home#!/upaccount";
	 * 
	 * String body = "<html>" +
	 * "<body style='font-family: Arial, sans-serif; line-height: 1.6em;'>" +
	 * "<h2>Xin chào,</h2>" + "<p>Cảm ơn bạn đã đăng ký tài khoản." +
	 * "<p>Vui lòng truy cập đường dẫn này<a href=\"" + url + "\">" + url +
	 * "</a>để cập nhật thông tin hoàn thành đang kí tài khoản.</p>" +
	 * "<p>Trân trọng,<br>Đội ngũ hỗ trợ</p>" + "</body>" + "</html>";
	 * 
	 * mailer.send(kh.getEmail(), "MAIL ĐĂNG KÍ TỪ WEBSITE ĐỒNG HỒ", body);
	 * kh.setTrangthaikh(1); khdao.save(kh); Cookie usernameCookie = new
	 * Cookie("username", kh.getUsernamekh()); usernameCookie.setMaxAge(30 * 24 * 60
	 * * 60); // Thời gian tồn tại của cookie (30 ngày) usernameCookie.setPath("/");
	 * // Đặt path cho cookie, "/" nghĩa là sẽ được áp dụng trên toàn bộ trang web
	 * response.addCookie(usernameCookie); } catch (Exception e) {
	 * System.out.println(e); }
	 * 
	 * return "auth/login"; }
	 */

	/*
	 * OAuth2
	 */

	@RequestMapping("/oauth2/login/success")
	public String success(OAuth2AuthenticationToken oauth2) {

		customerServive.loginFromOAuth2(oauth2);
		return "redirect:/index";
	}

	public String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replace(request.getServletPath(), "");
	}

	@RequestMapping("/admin/thongtin")
	public String getaccountadmin(Model model, @ModelAttribute("ad") KhachHang ad) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		System.out.println(authentication);
		KhachHang kha = khdao.findById(username).get();
		// System.out.println(kha.getHotenkh());
		model.addAttribute("ad", kha); 
		return "admin/view/profile";
	}
 
	@RequestMapping("/admin/upadmin")
	public String UpdateAdmin(HttpServletRequest request) {
		KhachHang kha = khdao.findById(request.getParameter("us")).get();
		kha.setHotenkh(request.getParameter("ht"));
		kha.setDienthoai(request.getParameter("sdt"));
		kha.setEmail(request.getParameter("em"));
		String gioitinhParam = request.getParameter("gt");
		Boolean gioitinh = Boolean.valueOf(gioitinhParam);
		kha.setGioitinh(gioitinh);
		kha.setDiachi(request.getParameter("dc"));
		khdao.save(kha);
		return "redirect:/admin/thongtin?success=upad";
	}
}