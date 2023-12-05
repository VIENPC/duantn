package com.poly.restcontroller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poly.dao.DanhMucDAO;
import com.poly.entities.NhomSanPham;


@RestController
@CrossOrigin(origins = "*")
public class DanhMucRestController {

	@Autowired
	DanhMucDAO dmdao;

	

	@GetMapping("/rest/danhmuc")
	public ResponseEntity<List<NhomSanPham>> findAll() {

		return ResponseEntity.ok(dmdao.findAlltt());
	}

	// @GetMapping("/rest/danhmuc/{manhom}")
	// public ResponseEntity<NhomSanPham> findByManhom(@PathVariable("manhom")
	// Integer manhom) {

	// return ResponseEntity.ok(dmdao.findById(manhom).get());
	// }


}
