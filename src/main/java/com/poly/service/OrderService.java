package com.poly.service;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.poly.entities.HoaDon;

public interface OrderService {
	public HoaDon create(JsonNode orderData);

	public HoaDon findById(Integer mahd);

	public List<HoaDon> findByUsername(String username);
}
