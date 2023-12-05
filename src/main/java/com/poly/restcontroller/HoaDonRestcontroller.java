package com.poly.restcontroller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.poly.dao.ChiTietGHDAO;
import com.poly.dao.DiaChiDAO;
import com.poly.dao.GioHangDAO;
import com.poly.dao.HoaDonCTDAO;
import com.poly.dao.HoaDonDAO;
import com.poly.dao.KhachHangDAO;
import com.poly.dao.KhuyenMaiDAO;
import com.poly.dao.MailerService;
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
import com.poly.service.OrderService;
import javax.mail.MessagingException;
import java.text.NumberFormat;
import java.util.Locale;

@CrossOrigin(origins = "*")
@RestController
public class HoaDonRestcontroller {

	@Autowired
	HoaDonDAO hddao;
	@Autowired
	SanPhamDAO spdao;
	@Autowired
	HoaDonCTDAO hdctdao;
	@Autowired
	OrderService orderService;

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
	public HoaDonRestcontroller(MailerServiceImpl mailerService) {
		this.mailerService = mailerService;
	}

	@GetMapping("/rest/magiamgia")
	public ResponseEntity<List<GiamGia>> getgiamgia() {

		return ResponseEntity.ok(khmdao.loadGiamGia());
	}

	@GetMapping("/rest/magiamgia/{id}")
	public ResponseEntity<GiamGia> getgiamgiaid(@PathVariable("id") Integer id) {
		return ResponseEntity.ok(khmdao.findById(id).get());
	}

	@PostMapping("/rest/hoadon")
	public HoaDon create(@RequestBody JsonNode orderData) {
		return orderService.create(orderData);
	}

	@PutMapping("/rest/hoadonhuy")
	public ResponseEntity<HoaDon> huydon(
			@RequestParam("mahd") Integer mahd, @RequestBody HoaDon hd) {
		HoaDon hdexist = hddao.findById(mahd).get();

		if (hdexist == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {

			Integer idgiamgia = hdexist.getGiamgia() != null ? hdexist.getGiamgia().getIdgiam() : null;
			if (idgiamgia != null) {
				GiamGia khm = khmdao.findById(idgiamgia).get();

				khm.setSoluong(khm.getSoluong() + 1);
				khmdao.save(khm);
			}

			List<HoaDonCT> hdct = hdctdao.findChiTietHoaDonByMaHoaDon(hdexist.getMahd());
			for (HoaDonCT detail : hdct) {
				Integer masp = detail.getSanpham().getMasp();
				int quantity = detail.getSoluong();
				SanPham product = spdao.findByMasp(masp);
				if (product != null) {
					int currentQuantity = product.getSoluong();
					int newQuantity = currentQuantity + quantity;
					product.setSoluong(newQuantity);
					spdao.save(product);
				}
			}
			hdexist.setTrangthaihd(hd.getTrangthaihd());

		}

		return new ResponseEntity<>(hddao.save(hdexist), HttpStatus.OK);

	}

	@GetMapping("/rest/hoadontt")
	public ResponseEntity<List<HoaDon>> getHoaDonByTTHD(
			@RequestParam("tthd") Integer tthd) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		KhachHang kh = khdao.findById(authentication.getName()).get();
		return ResponseEntity.ok(hddao.findHd(tthd, kh.getUsernamekh()));
	}

	@GetMapping("/rest/hoadonct")
	public ResponseEntity<List<HoaDonCT>> getHoaDonByHDCT(
			@RequestParam("mahd") Integer mahd) {

		return ResponseEntity.ok(hdctdao.findChiTietHoaDonByMaHoaDon(mahd));
	}

	@PostMapping("/rest/addhd")
	public Map<String, Object> taohoadon(@RequestBody Map<String, Object> checkoutData, HttpSession httpSession) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		KhachHang kh = khdao.findById(authentication.getName()).get();
		GioHang gh = ghdao.findByMaKH(kh.getUsernamekh());
		HoaDon hd = new HoaDon();
		int idkhm = (Integer) checkoutData.get("idgiam");
		System.out.println("MÃ giảm giá" + idkhm);
		List<Integer> productIds = (List<Integer>) checkoutData.get("sanphamcheck");
		Integer tienship = (Integer) checkoutData.get("tienvc");
		Long tienvc = tienship != null ? tienship.longValue() : null;

		if (idkhm != 0) {
			GiamGia khm = khmdao.findById(idkhm).get();
			hd.setGiamgia(khm);
			khm.setSoluong(khm.getSoluong() - 1);
			khmdao.save(khm);
		}
		System.out.println(idkhm);
		int id = (Integer) checkoutData.get("diachi");
		Integer integerValue = (Integer) checkoutData.get("totalAmount");
		Long longValue = integerValue.longValue();
		// long tongtien = (Long)checkoutData.get("totalAmount");
		String gc = (String) checkoutData.get("ghichu");

		System.out.println(id);
		System.out.println(gc);
		System.out.println(longValue);
		DiaChi dc = dcdao.findById(id).get();

		hd.setKhachhang(kh);
		hd.setTrangthaihd(1);
		hd.setTennguoinhan(dc.getTennn());
		hd.setDiachinn(dc.getDiachi());
		hd.setDienthoainn(dc.getSdtnn());
		hd.setTienvc(tienvc);
		hd.setTrangthaitt(false);
		hd.setTongtien(longValue);
		hd.setGhichu(gc);
		hddao.save(hd);
		List<String> productNamesList = new ArrayList<>();
		List<Integer> quantitiesList = new ArrayList<>();
		List<Integer> pricelist = new ArrayList<>();
		for (Integer listmasp : productIds) {

			List<ChiTietGH> listctgh = ctghdao.findByMasp(listmasp, gh.getMagh());
			for (ChiTietGH chiTietGH : listctgh) {
				System.out.println("Danh sách mã sp" + chiTietGH.getSanpham().getMasp());
				String productName = chiTietGH.getSanpham().getTensp();
				int quantity = chiTietGH.getSoluong();
				int price2 = chiTietGH.getSanpham().getGiasp();
				productNamesList.add(productName);
				quantitiesList.add(quantity);
				pricelist.add(price2);

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

		try {
			String formattedAmount = formatCurrency(longValue, "vi", "VN");
			// Gửi mail hóa đơn cho khách hàng
			String to = kh.getEmail();
			String senderPhone = "0766949***";
			String senderFax = "Shop đồng hồ NVA3";
			String recipientCode = dc.getSdtnn();
			String recipientName = dc.getTennn();
			String recipientAddress = dc.getDiachi();
			String paymentAmount = formattedAmount;
			String deliveryNote = "Cho xem hàng, không cho thử";
			String mahd = String.valueOf(hd.getMahd());
			Date orderDate = new Date(); // Thời gian đặt hàng
			// Chuyển đổi mảng thành mảng cơ bản (nếu cần)
			String[] productNames = productNamesList.toArray(new String[0]);
			int[] quantities = quantitiesList.stream().mapToInt(Integer::intValue).toArray();
			int[] price = pricelist.stream().mapToInt(Integer::intValue).toArray();
			// Tạo nội dung email
			String emailContent = EmailContentBuilder.buildInvoiceContent(senderPhone, senderFax, recipientCode,
					recipientName, recipientAddress, paymentAmount, deliveryNote, orderDate, productNames, quantities,
					price, mahd);

			// Gửi email
			mailerService.sendInvoiceEmail(to, "Đơn hàng đồng hồ của bạn", emailContent);
		} catch (MessagingException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return checkoutData;
		}
		return checkoutData;
	}

	private static String formatCurrency(Long amount, String language, String country) {

		Locale locale = new Locale(language, country);
		NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

		return currencyFormatter.format(amount);
	}

	@GetMapping("/rest/hdem")
	public ResponseEntity<HoaDon> trackinghd(@RequestParam("mahd") Integer mahd, @RequestParam("email") String email) {

		return ResponseEntity.ok(hddao.findhdem(mahd, email));
	}

}
