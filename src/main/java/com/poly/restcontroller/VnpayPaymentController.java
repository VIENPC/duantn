package com.poly.restcontroller;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.poly.VnpayConfig;
import com.poly.dao.ChiTietGHDAO;
import com.poly.dao.DiaChiDAO;
import com.poly.dao.GioHangDAO;
import com.poly.dao.HoaDonCTDAO;
import com.poly.dao.HoaDonDAO;
import com.poly.dao.KhachHangDAO;
import com.poly.dao.KhuyenMaiDAO;
import com.poly.dao.SanPhamDAO;
import com.poly.entities.ChiTietGH;
import com.poly.entities.DiaChi;
import com.poly.entities.EmailContentBuilder;
import com.poly.entities.GiamGia;
import com.poly.entities.GioHang;
import com.poly.entities.HoaDon;
import com.poly.entities.HoaDonCT;
import com.poly.entities.KhachHang;
import com.poly.entities.SanPham;
import com.poly.service.MailerServiceImpl;


@RestController
public class VnpayPaymentController {

	@Autowired
	HoaDonDAO hddao;
	@Autowired
	SanPhamDAO spdao;
	@Autowired
	HoaDonCTDAO hdctdao;
	

	@Autowired
	KhachHangDAO khdao;

	@Autowired
	DiaChiDAO dcdao;

	@Autowired
	ChiTietGHDAO ctghdao;
	@Autowired
	GioHangDAO ghdao;

	@Autowired
	KhuyenMaiDAO khmdao;

	private final MailerServiceImpl mailerService;

	@Autowired
	public VnpayPaymentController(MailerServiceImpl mailerService) {
		this.mailerService = mailerService;
	}
	// @GetMapping
	// public ModelAndView payVNP() {
	// ModelAndView modelAndView = new ModelAndView("index");
	// modelAndView.addObject("pay", new PaymentQR());
	// return modelAndView;
	// }
	// @GetMapping("/pay")
	// public ModelAndView payQR(){
	// ModelAndView modelAndView = new ModelAndView("vnpay_pay");
	// modelAndView.addObject("payment",new PaymentQR());
	// return modelAndView;
	// }

	@PostMapping("/rest/addhdonline")
	public ResponseEntity<Map<String, String>> receiveTotalAmount(@RequestBody Map<String, Object> checkoutData,
			HttpSession httpSession) throws IOException {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		KhachHang kh = khdao.findById(authentication.getName()).get();
		int id = (Integer) checkoutData.get("diachi");
		Integer integerValue = (Integer) checkoutData.get("totalAmount");
		Long longValue = integerValue.longValue() * 100;
		Long tongtien = integerValue.longValue();
		String gc = (String) checkoutData.get("ghichu");
		
		System.out.println(checkoutData.get("totalAmount"));
		System.out.println(checkoutData.get("diachi"));
		System.out.println(checkoutData.get("ghichu"));
		int idkhm = (Integer) checkoutData.get("idgiam");
		Integer tienship = (Integer) checkoutData.get("tienvc");
		Long tienvc = tienship != null ? tienship.longValue() : null;
		System.out.println("MÃ giảm giá" + idkhm);
		// add hóa dơn trước
		DiaChi dc = dcdao.findById(id).get();
		HoaDon hd = new HoaDon();
		if (idkhm != 0) {
			GiamGia khm = khmdao.findById(idkhm).get();
			hd.setGiamgia(khm);
			khm.setSoluong(khm.getSoluong() - 1);
			khmdao.save(khm);

		}
		hd.setKhachhang(kh);
		hd.setTrangthaihd(1);
		hd.setTennguoinhan(dc.getTennn());
		hd.setDiachinn(dc.getDiachi());
		hd.setTienvc(tienvc);
		hd.setDienthoainn(dc.getSdtnn());
		hd.setTrangthaitt(false);
		hd.setTongtien(tongtien);
		hd.setGhichu(gc);
		hddao.save(hd);
GioHang gh = ghdao.findByMaKH(kh.getUsernamekh());
		List<Integer> productIds = (List<Integer>) checkoutData.get("sanphamcheck");
		for (Integer listmasp : productIds) {
			System.out.println("Danh sách mã sp" + listmasp);
			List<ChiTietGH> listctgh = ctghdao.findByMasp(listmasp,gh.getMagh());
			for (ChiTietGH chiTietGH : listctgh) {
				int quantity = chiTietGH.getSoluong();
				SanPham sp = spdao.findById(listmasp).get();
				if (sp != null) {
					int slsp = sp.getSoluong();
					int slm = slsp - quantity;
					sp.setSoluong(slm);
					spdao.save(sp);
				}
				HoaDonCT hdct = new HoaDonCT();
				hdct.setHoadon(hd);
				hdct.setSanpham(chiTietGH.getSanpham());
				hdct.setSoluong(chiTietGH.getSoluong());
				hdctdao.save(hdct);
				ctghdao.deleteById(chiTietGH.getId());
			}
		}

		String vnp_Version = "2.1.0";
		String vnp_Command = "pay";
		String orderType = "other";
		String vnp_TxnRef = String.valueOf(hd.getMahd());
		String vnp_IpAddr = "127.0.0.1";
		String vnp_TmnCode = VnpayConfig.vnp_TmnCode;
		Map<String, String> vnp_Params = new HashMap<>();
		vnp_Params.put("vnp_Version", vnp_Version);
		vnp_Params.put("vnp_Command", vnp_Command);
		vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
		vnp_Params.put("vnp_Amount", String.valueOf(longValue));
		vnp_Params.put("vnp_CurrCode", "VND");
		vnp_Params.put("vnp_BankCode", "NCB");
		vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
		vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
		vnp_Params.put("vnp_OrderType", orderType);
		vnp_Params.put("vnp_Locale", "vn");
		vnp_Params.put("vnp_ReturnUrl", VnpayConfig.vnp_Returnurl);
		vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

		Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String vnp_CreateDate = formatter.format(cld.getTime());
		vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

		cld.add(Calendar.MINUTE, 15);
		List fieldNames = new ArrayList(vnp_Params.keySet());
		Collections.sort(fieldNames);
		StringBuilder hashData = new StringBuilder();
		StringBuilder query = new StringBuilder();
		Iterator itr = fieldNames.iterator();
		while (itr.hasNext()) {
			String fieldName = (String) itr.next();
			String fieldValue = (String) vnp_Params.get(fieldName);
			if ((fieldValue != null) && (fieldValue.length() > 0)) {
				// Build hash data
				hashData.append(fieldName);
				hashData.append('=');
				hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
				// Build query
				query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
				query.append('=');
				query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
				if (itr.hasNext()) {
					query.append('&');
					hashData.append('&');
				}
			}
		}
		String queryUrl = query.toString();
		String vnp_SecureHash = VnpayConfig.hmacSHA512(VnpayConfig.vnp_HashSecret, hashData.toString());
		queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
		String paymentUrl = VnpayConfig.vnp_PayUrl + "?" + queryUrl;
		System.out.println("diachiurlthanhtoan " + paymentUrl);

		// Tạo một đối tượng JSON để trả về paymentUrl
		Map<String, String> response = new HashMap<>();
		response.put("paymentUrl", paymentUrl);
		return ResponseEntity.ok(response);
	}

	
	@GetMapping("/vnpay_jsp")
	public ResponseEntity<String> laythongtin(@RequestParam("vnp_TransactionStatus") String trangthaitt,
			@RequestParam("vnp_TxnRef") String mahd) {

		if (trangthaitt.equals("00")) {
			HoaDon hd = hddao.findById(Integer.parseInt(mahd)).orElse(null);
			if (hd != null) {
				hd.setTrangthaitt(true);
				hd.setTrangthaihd(2);
				hddao.save(hd);
				try {

					// Gửi mail hóa đơn cho khách hàng
					KhachHang kh = khdao.findById(hd.getKhachhang().getUsernamekh()).get();
					String to = kh.getEmail();
					String senderPhone = "0766949***";
					String senderFax = "Shop đồng hồ NVA3";
					String recipientCode = hd.getDienthoainn();
					String recipientName = hd.getTennguoinhan();
					String recipientAddress = hd.getDiachinn();
					String paymentAmount = "ĐÃ THANH TOÁN";
					String deliveryNote = "Cho xem hàng, không cho thử";
					List<String> productNamesList = new ArrayList<>();
					List<Integer> quantitiesList = new ArrayList<>();
					List<Integer> pricelist = new ArrayList<>();
					List<HoaDonCT> listhdct = hdctdao.findChiTietHoaDonByMaHoaDon(Integer.parseInt(mahd));
					for (HoaDonCT hdct : listhdct) {
						String productName = hdct.getSanpham().getTensp();
						int quantity2 = hdct.getSoluong();
						int price2 = hdct.getSanpham().getGiasp();
						productNamesList.add(productName);
						quantitiesList.add(quantity2);
						pricelist.add(price2);
					}
					Date orderDate = new Date(); // Thời gian đặt hàng
					// Chuyển đổi mảng thành mảng cơ bản (nếu cần)
					String[] productNames = productNamesList.toArray(new String[0]);
					int[] quantities = quantitiesList.stream().mapToInt(Integer::intValue).toArray();
					int[] price = pricelist.stream().mapToInt(Integer::intValue).toArray();
					// Tạo nội dung email
					String emailContent = EmailContentBuilder.buildInvoiceContent(senderPhone, senderFax, recipientCode,
							recipientName, recipientAddress, paymentAmount, deliveryNote, orderDate, productNames,
							quantities,
							price, mahd);

					// Gửi email
					mailerService.sendInvoiceEmail(to, "Đơn hàng đồng hồ của bạn", emailContent);
				} catch (MessagingException | UnsupportedEncodingException e) {
					e.printStackTrace();

				}
				// Chuyển hướng đến trang thông báo thành công (ví dụ: /success)
				return ResponseEntity.status(HttpStatus.FOUND).header("Location",
						"/success").build();
			}

		}

		// Nếu không thành công hoặc không tìm thấy đơn hàng, có thể chuyển hướng đến
		// trang lỗi hoặc trang khác.
		return ResponseEntity.status(HttpStatus.FOUND).header("Location",
				"/error").build();
	}

}
