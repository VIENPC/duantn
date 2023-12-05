package com.poly.restcontroller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
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

import com.poly.ZaloPayConfig;
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
import com.poly.utils.HMACUtil;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/rest/zalopay")
public class ZaloPayPaymentController {
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
	
	Integer mahd1; 
	private final MailerServiceImpl mailerService;
@Autowired
	public ZaloPayPaymentController(MailerServiceImpl mailerService) {
		this.mailerService = mailerService;
	}
	public static String getCurrentTimeString(String format) {
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
		SimpleDateFormat fmt = new SimpleDateFormat(format);
		fmt.setCalendar(cal);
		return fmt.format(cal.getTimeInMillis());
	}

	@PostMapping(value = "/create-order")
	public Map<String, Object> createPayment(HttpServletRequest request, @RequestBody Map<String, Object> checkoutData)
			throws Exception {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		KhachHang kh = khdao.findById(authentication.getName()).get();
		int id = (Integer) checkoutData.get("diachi");
		Integer integerValue = (Integer) checkoutData.get("totalAmount");

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
		mahd1 = hd.getMahd();
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

		Map<String, Object> zalopay_Params = new HashMap<>();
		zalopay_Params.put("appid", ZaloPayConfig.APP_ID);
		zalopay_Params.put("apptransid", getCurrentTimeString("yyMMdd") + "_" + new Date().getTime());
		zalopay_Params.put("apptime", System.currentTimeMillis());
		zalopay_Params.put("appuser", "a");
		zalopay_Params.put("amount", tongtien);
		zalopay_Params.put("description", "Thanh toan don hang #" + hd.getMahd());
		zalopay_Params.put("bankcode", "");
		String item = "[{\"itemid\":\"knb\",\"itemname\":\"kim nguyen bao\",\"itemprice\":198400,\"itemquantity\":1}]";
		zalopay_Params.put("item", item);

		// embeddata
		// Trong trường hợp Merchant muốn trang cổng thanh toán chỉ hiện thị danh sách
		// các ngân hàng ATM,
		// thì Merchant để bankcode="" và thêm bankgroup = ATM vào embeddata như ví dụ
		// bên dưới
		// embeddata={"bankgroup": "ATM"}
		// bankcode=""
		Map<String, String> embeddata = new HashMap<>();
		embeddata.put("merchantinfo", "eshop123");
		embeddata.put("promotioninfo", "");
		embeddata.put("redirecturl", ZaloPayConfig.REDIRECT_URL);

		Map<String, String> columninfo = new HashMap<String, String>();
		columninfo.put("store_name", "NVA3-Shop");
		embeddata.put("columninfo", new JSONObject(columninfo).toString());
		zalopay_Params.put("embeddata", new JSONObject(embeddata).toString());

		String data = zalopay_Params.get("appid") + "|" + zalopay_Params.get("apptransid") + "|"
				+ zalopay_Params.get("appuser") + "|" + zalopay_Params.get("amount") + "|"
				+ zalopay_Params.get("apptime") + "|" + zalopay_Params.get("embeddata") + "|"
				+ zalopay_Params.get("item");
		zalopay_Params.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZaloPayConfig.KEY1, data));
		// zalopay_Params.put("phone", order.getPhone());
		// zalopay_Params.put("email", order.getEmail());
		// zalopay_Params.put("address", order.getAddress());
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(ZaloPayConfig.CREATE_ORDER_URL);

		List<NameValuePair> params = new ArrayList<>();
		for (Map.Entry<String, Object> e : zalopay_Params.entrySet()) {
			params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
		}
		post.setEntity(new UrlEncodedFormEntity(params));
		CloseableHttpResponse res = client.execute(post);
		BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
		StringBuilder resultJsonStr = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			resultJsonStr.append(line);
		}
		JSONObject result = new JSONObject(resultJsonStr.toString());
		Map<String, Object> kq = new HashMap<String, Object>();
		kq.put("returnmessage", result.get("returnmessage"));
		kq.put("orderurl", result.get("orderurl"));
		kq.put("returncode", result.get("returncode"));
		kq.put("zptranstoken", result.get("zptranstoken"));
		return kq;
	}

	@GetMapping(value = "/getstatusbyapptransid")
	public Map<String, Object> getStatusByApptransid(HttpServletRequest request,
			@RequestParam(name = "apptransid") String apptransid) throws Exception {
		String appid = ZaloPayConfig.APP_ID;
		String key1 = ZaloPayConfig.KEY1;
		String data = appid + "|" + apptransid + "|" + key1; // appid|apptransid|key1
		String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, key1, data);

		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("appid", appid));
		params.add(new BasicNameValuePair("apptransid", apptransid));
		params.add(new BasicNameValuePair("mac", mac));

		URIBuilder uri = new URIBuilder("https://sandbox.zalopay.com.vn/v001/tpe/getstatusbyapptransid");
		uri.addParameters(params);

		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet(uri.build());

		CloseableHttpResponse res = client.execute(get);
		BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
		StringBuilder resultJsonStr = new StringBuilder();
		String line;

		while ((line = rd.readLine()) != null) {
			resultJsonStr.append(line);
		}

		JSONObject result = new JSONObject(resultJsonStr.toString());
		Map<String, Object> kq = new HashMap<String, Object>();
		kq.put("returncode", result.get("returncode"));
		kq.put("returnmessage", result.get("returnmessage"));
		kq.put("isprocessing", result.get("isprocessing"));
		kq.put("amount", result.get("amount"));
		
		kq.put("discountamount", result.get("discountamount"));
		kq.put("zptransid", result.get("zptransid"));
		return kq;
	}

	@GetMapping("/success/payment")
	public ResponseEntity<String> handlePaymentSuccess(
			@RequestParam String amount,
			@RequestParam String appid,
			@RequestParam String apptransid,
			@RequestParam String bankcode,
			@RequestParam String checksum,
			@RequestParam String discountamount,
			@RequestParam String pmcid,
			@RequestParam String status) {
		if(status.equalsIgnoreCase("1") ){
			HoaDon hd = hddao.findById(mahd1).orElse(null);
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
					List<HoaDonCT> listhdct = hdctdao.findChiTietHoaDonByMaHoaDon(mahd1);
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
					recipientName, recipientAddress, paymentAmount, deliveryNote, orderDate, productNames, quantities,
					price, String.valueOf(mahd1));

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
	return ResponseEntity.status(HttpStatus.FOUND).header("Location",
				"/error").build();
	}
	
}


