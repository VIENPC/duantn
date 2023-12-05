package com.poly.restcontroller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poly.dao.HinhAnhDAO;
import com.poly.dao.SanPhamDAO;
import com.poly.entities.HinhSP;
import com.poly.entities.SanPham;
import com.poly.entities.ThuongHieu;

@RestController
@CrossOrigin(origins = "*")
public class SanPhamRestcontroller {
	@Autowired
	SanPhamDAO spdao; 

	@Autowired
	HinhAnhDAO hdao;

	@GetMapping("/rest/sanpham")
	public ResponseEntity<List<SanPham>> findAll() {
		return ResponseEntity.ok(spdao.findAlltt());
	}

	@GetMapping("/rest/sanpham-by-nhomsanpham/{manhom}")
	public ResponseEntity<List<SanPham>> findDanhMuc(@PathVariable("manhom") Integer manhom) {
		return ResponseEntity.ok(spdao.findSPDanhMuc(manhom));
	}
	// @GetMapping("/rest/sanpham")
	// public ResponseEntity<List<SanPham>> findAllNew7() {
	// LocalDateTime localDate = LocalDateTime.now();
	// LocalDateTime fiveDaysAgo = localDate.minusDays(7);
	// return
	// ResponseEntity.ok(spdao.findTop7ByNgaynhapAfterAndTrangthaispOrderByNgaynhapDesc(fiveDaysAgo,
	// true));
	// }

	@GetMapping("/rest/sanphamnew")
	public ResponseEntity<List<SanPham>> findAllNew7() {
		LocalDateTime localDate = LocalDateTime.now();
		LocalDateTime fiveDaysAgo = localDate.minusDays(7);
		return ResponseEntity
				.ok(spdao.findTop8ByNgaycapnhatAfterAndTrangthaispOrderByNgaycapnhatDesc(fiveDaysAgo, true));
	}
	// @GetMapping("/rest/sanphamnewall")
	// public ResponseEntity<List<SanPham>> findAllNew() {
	// LocalDateTime localDate = LocalDateTime.now();
	// LocalDateTime fiveDaysAgo = localDate.minusDays(7);
	// return ResponseEntity.ok(spdao.findNewProduct(fiveDaysAgo,true));
	// }

	@GetMapping("/rest/sanphams/{masp}")
	public ResponseEntity<SanPham> getOne(@PathVariable("masp") Integer masp) {
		if (!spdao.existsById(masp)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(spdao.findById(masp).get());
	}

	@GetMapping("/rest/hinhsanphams/{masp}") 
	public ResponseEntity<List<HinhSP>> gethinhsp(@PathVariable("masp") Integer masp) {
		if (!spdao.existsById(masp)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(hdao.findbyMasp(masp));
	}

	// @GetMapping("/rest/hangsanxuat/{mahang}")
	// public ResponseEntity<List<SanPham>> gethang(@PathVariable("mahang") Integer
	// mahang) {
	// return ResponseEntity.ok(spdao.findByMahang(mahang));
	// }
	@GetMapping("/rest/productsloc")
	public ResponseEntity<List<SanPham>> getFilteredProducts(
			@RequestParam(value = "danhmuc", required = false) String danhmuchon) {
		// Khi bạn gọi API, danhMucIds sẽ chứa danh sách các giá trị được truyền từ
		// AngularJS
		// Thực hiện logic lọc dựa trên danhMucIds ở đây và trả về kết quả

		List<Integer> danhMucIds = new ArrayList<>();
		List<SanPham> filteredProducts = null;
		// Phân tách chuỗi và thêm từng số vào danh sách
		if (danhmuchon != null && !danhmuchon.isEmpty()) {
			String[] danhMucArray = danhmuchon.split(",");
			for (String danhMuc : danhMucArray) {
				danhMucIds.add(Integer.parseInt(danhMuc.trim()));
			}
		}
		if (danhMucIds.isEmpty()) {
			filteredProducts = spdao.findAlltt();
		} else {
			filteredProducts = spdao
					.findSPDanhMucLoc(danhMucIds);
		}

		System.out.println(danhMucIds);

		return ResponseEntity.ok(filteredProducts);

	}

	@GetMapping("/rest/locdmth")
	public ResponseEntity<List<SanPham>> getFilteredProductsTh(
			@RequestParam(value = "danhmuc", required = false) String danhmuchon,
			@RequestParam("thuonghieu") Integer id) {
		// Khi bạn gọi API, danhMucIds sẽ chứa danh sách các giá trị được truyền từ
		// AngularJS
		// Thực hiện logic lọc dựa trên danhMucIds ở đây và trả về kết quả

		List<Integer> danhMucIds = new ArrayList<>();
		List<SanPham> filteredProducts = null;
		// Phân tách chuỗi và thêm từng số vào danh sách
		if (danhmuchon != null && !danhmuchon.isEmpty()) {
			String[] danhMucArray = danhmuchon.split(",");
			for (String danhMuc : danhMucArray) {
				danhMucIds.add(Integer.parseInt(danhMuc.trim()));
			}
		}
		if (danhMucIds.isEmpty()) {
			filteredProducts = spdao.findAlltt();
		} else {
			filteredProducts = spdao
					.findSPDanhMucAndThuongHieuLoc(danhMucIds, id);
		}

		System.out.println(danhMucIds);

		return ResponseEntity.ok(filteredProducts);

	}

	@GetMapping("/rest/locdmthgia")
	public ResponseEntity<List<SanPham>> getFilteredProductsThprice(
			@RequestParam(value = "danhmuc", required = false) String danhmuchon,
			@RequestParam("thuonghieu") Integer id, @RequestParam(name = "minPrice", required = false) Integer minPrice,
			@RequestParam(name = "maxPrice", required = false) Integer maxPrice) {
		// Khi bạn gọi API, danhMucIds sẽ chứa danh sách các giá trị được truyền từ
		// AngularJS
		// Thực hiện logic lọc dựa trên danhMucIds ở đây và trả về kết quả

		List<Integer> danhMucIds = new ArrayList<>();
		List<SanPham> filteredProducts = null;
		// Phân tách chuỗi và thêm từng số vào danh sách
		if (danhmuchon != null && !danhmuchon.isEmpty()) {
			String[] danhMucArray = danhmuchon.split(",");
			for (String danhMuc : danhMucArray) {
				danhMucIds.add(Integer.parseInt(danhMuc.trim()));
			}
		}
		if (danhMucIds.isEmpty()) {
			filteredProducts = spdao.findAlltt();
		} else {
			filteredProducts = spdao
					.findSPByDanhMucAndThuongHieuAndGia(danhMucIds, id, minPrice, maxPrice);

		}

		return ResponseEntity.ok(filteredProducts);

	}

	@GetMapping("/rest/locdmgia")
	public ResponseEntity<List<SanPham>> getFilteredProductsprice(
			@RequestParam(value = "danhmuc", required = false) String danhmuchon,
			@RequestParam(name = "minPrice", required = false) Integer minPrice,
			@RequestParam(name = "maxPrice", required = false) Integer maxPrice) {
		// Khi bạn gọi API, danhMucIds sẽ chứa danh sách các giá trị được truyền từ
		// AngularJS
		// Thực hiện logic lọc dựa trên danhMucIds ở đây và trả về kết quả

		List<Integer> danhMucIds = new ArrayList<>();
		List<SanPham> filteredProducts = null;
		// Phân tách chuỗi và thêm từng số vào danh sách
		if (danhmuchon != null && !danhmuchon.isEmpty()) {
			String[] danhMucArray = danhmuchon.split(",");
			for (String danhMuc : danhMucArray) {
				danhMucIds.add(Integer.parseInt(danhMuc.trim()));
			}
		}

		filteredProducts = spdao
				.findSPByDanhMucAndGia(danhMucIds, minPrice, maxPrice);

		// System.out.println(danhMucIds);

		return ResponseEntity.ok(filteredProducts);

	}

	@GetMapping("/rest/locthprice")
	public ResponseEntity<List<SanPham>> getFilteredthsprice(
			@RequestParam("thuonghieu") Integer id,
			@RequestParam(name = "minPrice", required = false) Integer minPrice,
			@RequestParam(name = "maxPrice", required = false) Integer maxPrice) {
		// Khi bạn gọi API, danhMucIds sẽ chứa danh sách các giá trị được truyền từ
		// AngularJS
		// Thực hiện logic lọc dựa trên danhMucIds ở đây và trả về kết quả

		List<SanPham> filteredProducts = spdao.findSPByThuongHieuAndGia(id, minPrice, maxPrice);

		// System.out.println(danhMucIds);

		return ResponseEntity.ok(filteredProducts);

	}

	@GetMapping("/rest/locprice")
	public ResponseEntity<List<SanPham>> getFilteredProductsprice(
			@RequestParam(name = "minPrice", required = false) Integer minPrice,
			@RequestParam(name = "maxPrice", required = false) Integer maxPrice) {
		List<SanPham> sanPhams;

		if (minPrice != null && maxPrice != null) {
			sanPhams = spdao.findByGiaspBetweenAndTrangthaispIsTrue(minPrice, maxPrice);
		} else if (maxPrice != null) {
			sanPhams = spdao.findByGiaspGreaterThanAndTrangthaispIsTrue(maxPrice);
		} else {
			sanPhams = spdao.findAll();
		}

		return ResponseEntity.ok(sanPhams);
	}


	@GetMapping("/rest/locthmaxrice")
	public ResponseEntity<List<SanPham>> getFilteredProductsmaxprice(@RequestParam(value = "danhmuc", required = false) String danhmuchon,
			@RequestParam("thuonghieu") Integer id, 
			@RequestParam(name = "maxPrice", required = false) Integer maxPrice){
List<Integer> danhMucIds = new ArrayList<>();

		List<SanPham> filteredProducts = null;
		// Phân tách chuỗi và thêm từng số vào danh sách
		if (danhmuchon != null && !danhmuchon.isEmpty()) {
			String[] danhMucArray = danhmuchon.split(",");
			for (String danhMuc : danhMucArray) {
				danhMucIds.add(Integer.parseInt(danhMuc.trim()));
			}
		}
		filteredProducts = spdao
					.findSPByDanhMucthgiamax(danhMucIds, id, maxPrice);
				return ResponseEntity.ok(filteredProducts);
	}
	@GetMapping("rest/thuonghieu/{id}")
	public ResponseEntity<List<SanPham>> loadthid(@PathVariable("id") Integer id) {

		return ResponseEntity.ok(spdao.findSPThuonghieu(id));
	}

	@GetMapping("/rest/product-search")
	public ResponseEntity<List<SanPham>> searchProductsByName(@RequestParam(name = "productName") String tensp) {
		try {
			List<SanPham> products = spdao.findByTenspContainingAndTrangthaispIsTrue(tensp);
			return ResponseEntity.ok(products);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/rest/sanphamdm/{manhom}")
	public ResponseEntity<List<SanPham>> loadspdm(@PathVariable("manhom") Integer manhom) {
		try {
			List<SanPham> products = spdao.loadspdm(manhom);
			return ResponseEntity.ok(products);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
