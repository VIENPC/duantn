package com.poly.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poly.dao.HoaDonDAO;
import com.poly.dao.SanPhamDAO;
import com.poly.dao.HoaDonCTDAO;
import com.poly.entities.HoaDon;
import com.poly.entities.HoaDonCT;
import com.poly.entities.SanPham;

@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	HoaDonDAO dao;

	@Autowired
	SanPhamDAO spdao;

	@Autowired
	HoaDonCTDAO ddao;

	@Override
	public HoaDon create(JsonNode orderData) {
		ObjectMapper mapper = new ObjectMapper();

		HoaDon order = mapper.convertValue(orderData, HoaDon.class);
		dao.save(order);

		TypeReference<List<HoaDonCT>> type = new TypeReference<List<HoaDonCT>>() {
		};
		List<HoaDonCT> details = mapper.convertValue(orderData.get("hoadonct"), type).stream()
				.peek(d -> d.setHoadon(order)).collect(Collectors.toList());
		for (HoaDonCT detail : details) {
			
			// Lấy mã sản phẩm từ hóa đơn chi tiết
			Integer masp = detail.getSanpham().getMasp();

			// Lấy số lượng sản phẩm từ hóa đơn chi tiết
			int quantity = detail.getSoluong();

			// Tìm sản phẩm trong cơ sở dữ liệu bằng mã sản phẩm
			//SanPham product = spdao.findByMasp(masp); 

			// Cập nhật số lượng mới vào sản phẩm trong cơ sở dữ liệu
			// if (product != null) {
			// 	// Lấy số lượng hiện có của sản phẩm trong cơ sở dữ liệu
			// 	int currentQuantity = product.getSoluong();
				
			// 	// Trừ số lượng sản phẩm đã thêm vào hóa đơn chi tiết từ số lượng hiện có
			// 	int newQuantity = currentQuantity - quantity;

			// 	// Cập nhật số lượng mới vào sản phẩm trong cơ sở dữ liệu
			// 	product.setSoluong(newQuantity);
			// 	// Lưu sản phẩm sau khi đã cập nhật số lượng mới
			// 	spdao.save(product);
			// } else {
			// 	  throw new IllegalArgumentException("Không tìm thấy sản phẩm");
			// }
		}

		ddao.saveAll(details);

		return order;

	}

	@Override
	public HoaDon findById(Integer mahd) {
		return dao.findById(mahd).get();
	}

	@Override
	public List<HoaDon> findByUsername(String username) {
		return dao.findByUsername(username);
	}
}
