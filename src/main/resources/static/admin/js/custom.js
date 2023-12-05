$('#sampleTable').DataTable({
});
$(document).ready(function () {
	// Hàm xử lý sự kiện khi chọn sản phẩm
	$('select[name="productId"]').change(function () {
		// Lấy giá trị của sản phẩm đã chọn
		var selectedProductId = $(this).val();

		// Gán giá trị vào trường ẩn
		$('#selectedProductId').val(selectedProductId);
	});
	$('input[name="files"]').change(function () {
		// Hiển thị hình ảnh ngay tại client-side
		previewImages(this);
	});

	// Hàm hiển thị hình ảnh
	function previewImages(input) {
		var previewDiv = $('#image-preview');
		previewDiv.empty();

		// Lặp qua tất cả các file được chọn 
		for (var i = 0; i < input.files.length; i++) {
			var reader = new FileReader();
			reader.onload = function (e) {
				// Hiển thị hình ảnh
				$('<img width="300px">').attr('src', e.target.result).appendTo(previewDiv);
			};
			reader.readAsDataURL(input.files[i]);
		}
	}


});
$(document).ready(function () {
	$('.js-example-basic-single').select2({

	});
	// Xử lý sự kiện khi nhấn vào button
	$('#sidebarCollapse').click(function () {
		// Thêm hoặc xóa lớp CSS 'active' của thẻ nav
		$('#sidebar').toggleClass('active');
	});

	$('#checkboxall').click(function () {
		if ($(this).is(":checked")) {
			$('.chkboxId').prop('checked', true);
		} else {
			$('.chkboxId').prop('checked', false);
		}

	});
	$('.edit').on('click', function (event) {
		event.preventDefault();
		var href = $(this).attr('href');
		$.get(href, function (thuonghieu, status) {
			$('#tenhieuedit').val(thuonghieu.tenthuonghieu);
			$('#mahieu').val(thuonghieu.id);
		});
		$("#editthuonghieu").modal("show");
	});


	$("#trangthai").change(function () {
		updateSoluongInput();
	});
	function updateSoluongInput() {
		if ($("#trangthai").val() === "false") {
			// If "Hết hàng" is selected, set the "Số lượng" input value to 0
			$("#soluong").val(0);
		} else {
			$("#soluong").val(1);
		}
	}

	$('#spbanchay').DataTable({
		"order": []  // Đặt thứ tự sắp xếp ban đầu là mảng rỗng
	});


});

// xử lí phần thống kê theo hãng
function submitForm() {
	document.getElementById("formhang").submit();
}
function submitFormdm() {
	document.getElementById("formdm").submit();
}



function removeSuccessParamFromURL() {
	if (window.history && window.history.replaceState) {
		var url = window.location.href;
		var urlParams = new URLSearchParams(window.location.search);
		urlParams.delete('success');
		var newURL = url.split('?')[0] + urlParams.toString();
		window.history.replaceState({}, '', newURL);
	}
}
function removeFailParamFromURL() {
	if (window.history && window.history.replaceState) {
		var url = window.location.href;
		var urlParams = new URLSearchParams(window.location.search);
		urlParams.delete('fail');
		var newURL = url.split('?')[0] + urlParams.toString();
		window.history.replaceState({}, '', newURL);
	}
}
// Kiểm tra query parameter và hiển thị thông báo thành công
window.onload = function () {
	var urlParams = new URLSearchParams(window.location.search);
	var successParam = urlParams.get('success');
	var failParam = urlParams.get('fail');
	//thành công 
	if (successParam == "add") {
		showSuccessMessage("Thêm sản phẩm thành công");
		removeSuccessParamFromURL();
	}
	if (successParam == "addkm") {
		showSuccessMessage("Thêm khuyến mãi thành công");
		removeSuccessParamFromURL();
	}
	if (successParam == "addmuc") {
		showSuccessMessage("Thêm danh mục thành công");
		removeSuccessParamFromURL();
	}
	if (successParam == "addhieu") {
		showSuccessMessage("Thêm thương hiệu thành công");
		removeSuccessParamFromURL();
	}
	if (successParam == "addtin") {
		showSuccessMessage("Thêm tin tức thành công");
		removeSuccessParamFromURL();
	}
	if (successParam == "updatedm") {
		showSuccessMessage("Cập nhật danh mục thành công");
		removeSuccessParamFromURL();
	}
	if (successParam == "uppkm") {
		showSuccessMessage("Cập nhật khuyến mãi thành công");
		removeSuccessParamFromURL();
	}
	if (successParam == "uphieu") {
		showSuccessMessage("Cập nhật thương hiệu thành công");
		removeSuccessParamFromURL();
	}
	if (successParam == "upad") {
		showSuccessMessage("Cập nhật thông tin thành công");
		removeSuccessParamFromURL();
	}
	if (successParam == "uptin") {
		showSuccessMessage("Cập nhật tin tức thành công");
		removeSuccessParamFromURL();
	}
	if (successParam == "uphinh") {
		showSuccessMessage("Thêm hình thành công");
		removeSuccessParamFromURL();
	}
	if (successParam === 'delete') {
		showSuccessMessage("Xoá sản phẩm thành công");
		removeSuccessParamFromURL();
	}
	if (successParam === 'deldm') {
		showSuccessMessage("Xoá danh mục thành công");
		removeSuccessParamFromURL();
	}
	if (successParam === 'delth') {
		showSuccessMessage("Xóa thương hiệu thành công");
		removeSuccessParamFromURL();
	}
	if (successParam === 'deltin') {
		showSuccessMessage("Xóa tin tức thành công");
		removeSuccessParamFromURL();
	}

	if (successParam === 'deldg') {
		showSuccessMessage("Xóa đánh giá thành công");
		removeSuccessParamFromURL();
	}
	if (successParam === 'updatesp') {
		showSuccessMessage("Cập nhật thành công");
		removeSuccessParamFromURL();
	}





	//thất bại
	if (failParam == 'delete') {
		showFailMessage("Xoá sản phẩm thất bại");
		removeFailParamFromURL();
	}
	if (failParam == 'addkm') {
		showFailMessage("Thêm khuyến mãi thất bại");
		removeFailParamFromURL();
	}
	if (failParam == 'deldm') {
		showFailMessage("Xoá danh mục thất bại");
		removeFailParamFromURL();
	}
	if (failParam == 'delth') {
		showFailMessage("Xoá thương hiệu thất bại");
		removeFailParamFromURL();
	}

	if (failParam == 'updatesp') {
		showFailMessage("Cập nhật sản phẩm thất bại");
		removeFailParamFromURL();
	}
	if (failParam == 'add') {
		showFailMessage("Thêm sản phẩm thất bại trùng tên");
		removeFailParamFromURL();
	}
	if (failParam == 'addhieu') {
		showFailMessage("Thêm thương hiệu thất bại");
		removeFailParamFromURL();
	}
	if (failParam == 'addhieutt') {
		showFailMessage("Thêm thương hiệu thất bại trùng tên");
		removeFailParamFromURL();
	}if (failParam == 'adddmtt') {
		showFailMessage("Thêm danh mục thất bại trùng tên");
		removeFailParamFromURL();
	}
	if (failParam == 'uphieufail') {
		showFailMessage("Cập nhật thương hiệu thất bại");
		removeFailParamFromURL();
	}
}
function showFailMessage(message) {
	swal("Thông báo", message, "error");
}
function showSuccessMessage(message) {
	swal("Thông báo", message, "success");
}
// xóa nhiều sản phẩm cùng lúc
// $('.xoansp').click(function(){
// 	swal({
// 		title: "Cảnh báo",
// 		text: "Bạn có chắc chắn muốn xóa sản phẩm này?",
// 		buttons: ["Hủy bỏ", "Đồng ý"],
// 	}).then((willDelete) => {
// 		window.location.href = `/qlsanpham/delnhieusp`;
// 	})
// });  

// $(document).on('click', '.settt', function () {
// 	const mahd = $(this).data("mahd");
// 	const user = $(this).data("user");
// 	swal({
// 		title: "Cảnh báo",
// 		text: "Bạn có chắc chắn muốn chỉnh sửa trạng thái này?",
// 		buttons: ["Hủy bỏ", "Đồng ý"],
// 		icon: "warning"
// 	}).then((willEdit) => {
// 		if (willEdit) {
// 			if (mahd != null) {
// 				window.location.href = `/admin/qldonhang/suatthd/${mahd}`;
// 			}
// 			if (user != null) {

// 				window.location.href = `/admin/qlkhachhang/editt/${user}`;
// 			}

// 		}
// 	})

// });

$('.unclock').click(function () {
	const user = $(this).data("user");
	swal({
		title: "Cảnh báo",
		text: "Bạn có chắc chắn muốn chỉnh sửa trạng thái này?",
		buttons: ["Hủy bỏ", "Đồng ý"],
	}).then((willEdit) => {
		if (willEdit) {

			if (user != null) {

				window.location.href = `/admin/qlkhachhang/unclock/${user}`;
			}

		}
	})
})

$(document).on('click', '.trash', function () {
	const masp = $(this).data("masp");
	const madm = $(this).data("madm");
	const id = $(this).data("id");
	const idtin = $(this).data("idtin");
	const iddg = $(this).data("iddg");
	swal({
		title: "Cảnh báo",
		text: "Bạn có chắc chắn muốn xóa?",
		buttons: ["Hủy bỏ", "Đồng ý"],
		icon: "warning"
	}).then((willDelete) => {
		if (willDelete) {
			if (masp != null) {
				window.location.href = `/admin/qlsanpham/deletesp/${masp}`;
			} else if (madm != null) {
				window.location.href = `/admin/qldanhmuc/deletedm/${madm}`;
			} else if (id != null) {
				window.location.href = `/admin/qlhieu/deleteth/${id}`;
			} else if (idtin != null) {
				window.location.href = `/admin/qltintuc/deletetin/${idtin}`;
			} else if (iddg != null) {
				window.location.href = `/admin/qldanhgia/deletedg/${iddg}`;

			}

			// Chuyển hướng tới endpoint xử lý xóa sản phẩm khi người dùng nhấn "Đồng ý"
			// Thay 123 bằng id của sản phẩm cần xóa


		}

	});
});
function validateForm() {
	var loai = document.getElementById("loai").value;
	var tenkm = document.getElementById("tenkm").value;
	var tien = document.getElementById("tien").value;
	var soluong = document.getElementById("soluong").value;

	if (loai === "" || tenkm.trim() === "" || tien === "" || soluong === "") {
		alert("Vui lòng điền đầy đủ thông tin.");
		return false;
	}

	return true; // Allow form submission
}
function validateDate() {
	var startDate = new Date(document.getElementById("startDate").value);
	var endDate = new Date(document.getElementById("endDate").value);
	var errorMessage = document.getElementById("error-message");

	if (endDate < startDate) {
		errorMessage.textContent = "Ngày kết thúc không thể trước ngày bắt đầu.";
	} else {
		errorMessage.textContent = "";
	}
}

function validateForm() {
	var startDate = new Date(document.getElementById("startDate").value);
	var endDate = new Date(document.getElementById("endDate").value);
	var errorMessage = document.getElementById("error-message");

	if (endDate < startDate) {
		errorMessage.textContent = "Ngày kết thúc không thể trước ngày bắt đầu.";
		return false; // Prevent form submission
	} else {
		errorMessage.textContent = "";
		return true; // Allow form submission
	}
}

$('.export').click(function () {
	var table2excel = new Table2Excel();
    var tableElement = document.getElementById('table');

    // Kiểm tra xem bảng có đang ẩn không
    var isTableHidden = $(tableElement).is(":hidden");

    // Nếu bảng đang ẩn, hiển thị tạm thời để xuất Excel
    if (isTableHidden) {
        $(tableElement).show();
    }

    table2excel.export(tableElement);

    // Nếu bảng ban đầu đang ẩn, ẩn nó lại sau khi xuất Excel
    if (isTableHidden) {
        $(tableElement).hide();
    }
});
$('.pdf-file').click(function () {
    var element = document.getElementById("table");

    // Kiểm tra xem bảng có đang ẩn không
    var isTableHidden = $(element).is(":hidden");

    // Nếu bảng đang ẩn, hiển thị tạm thời để render
    if (isTableHidden) {
        $(element).show();
    }

    var html = "<html>";
    html += '<head>';
    html += ' <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">';
    html += '</head><body>';
    html += element.outerHTML;
    html += '</body></html>';

    // Nếu bảng ban đầu đang ẩn, ẩn nó lại sau khi render
    if (isTableHidden) {
        $(element).hide();
    }

    // console.log("Giá trị của bảng", html);

    var opt = {
        margin: 0.5,
        filename: 'myfilepdf.pdf',
        image: { type: 'png', quality: 0.98 },
        html2canvas: { scale: 2 },
        jsPDF: { unit: 'in', format: 'letter', orientation: 'portrait' }
    };

    html2pdf(html, opt);
});


var myApp = new function () {
	this.printTable = function () {
		var tab = document.getElementById('table');
		
		//var win = window.open('', '', 'height=700,width=700');
		var html = "<html>";
		html +='<head>';
		html +=' <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">'
		html +='</head><body>'
		html +=tab.outerHTML;
		html +='</body><script>window.print();</script></html>'
		var win = window.open('', '', 'height=700,width=700');
		win.document.write(html);
		win.document.close();
		//win.print();
		
		
	}
	 
	
	

}
function editDanhMuc(id) {
	$.ajax({
		type: 'GET',
		url: '/rest/danhmuc/' + id,
		success: function (danhmuc) {
			var trangthaiValue = danhmuc.trangthai ? "true" : "false";
			$('#modaledit #tendanhmuc').val(danhmuc.tennhom);
			$('#modaledit #tinhtrang').val(trangthaiValue);
			$('#modaledit #manhom').val(danhmuc.manhom);
		}
	})

	// Hiển thị modal
	$("#modaledit").modal("show");


}

function edithd(id) {

	$.ajax({
		type: 'GET',
		url: '/rest/hoadontt/' + id,
		success: function (hoadon) {

			$('#edittt #tinhtrang').val(hoadon.trangthaihd);
			$('#edittt #mahd').val(hoadon.mahd);

		}
	})
	$("#edittt").modal("show");
}

function editkm(id) {

	$.ajax({
		type: 'GET',
		url: '/rest/khuyenmai/' + id,
		success: function (giamgia) {

			$('#modaleditkm #tenkm').val(giamgia.magiam);
			$('#modaleditkm #soluong').val(giamgia.soluong);
			$('#modaleditkm #tien').val(giamgia.tiengiam);

			$('#modaleditkm #loai').val(giamgia.loai);
			$('#modaleditkm #giamin').val(giamgia.giamin);
			$('#modaleditkm #giamax').val(giamgia.giamax);
			$('#modaleditkm #idgiam').val(giamgia.idgiam);
		}
	})
	$("#modaleditkm").modal("show");
}
var masp;
function editsp(id) {
	$.ajax({
		type: 'GET',
		url: '/rest/sanphams/' + id,
		success: function (sanpham) {
			var trangthaiValue = sanpham.trangthaisp ? "true" : "false";


			$('#modalsp #masp').val(sanpham.masp);
			$('#modalsp #masp_hidden').val(sanpham.masp);
			$('#modalsp #soluong').val(sanpham.soluong);
			$('#modalsp #tensp').val(sanpham.tensp);
			$('#modalsp #trangthai').val(trangthaiValue);
			$('#modalsp #giasp').val(sanpham.giasp);
			masp = sanpham.masp;
		}
	})
	$("#modalsp").modal("show");
}
function checkmasp() {
	// alert(masp);

	window.location.href = "/admin/addsanpham/" + masp;

}
function edittin(id) {

	$.ajax({
		type: 'GET',
		url: '/rest/edittin/' + id,
		success: function (tintuc) {
			var selectedValue = tintuc.sanpham.masp;
			$('#edittin #id').val(tintuc.id)
			$('#edittin #tensp').val(selectedValue)
			$('#edittin #tieude').val(tintuc.tieude)
			// $('#edittin #noidung').val(tintuc.noidung)
			CKEDITOR.instances['noidung'].setData(tintuc.noidung);
		}
	})

	$('#edittin').modal("show");
}


function submitFormSPBC(selectElement) {
	var selectedValue = selectElement.value;
	// Kiểm tra giá trị đã chọn và thực hiện hành động tương ứng
	if (selectedValue) {
		document.getElementById('formthang').submit();
	}
}
function submitFormLoai() {
	document.getElementById("formloai").submit();
}

// Get references to HTML elements
const imageInput = document.getElementById("validatedCustomFile");
const imageFrame = document.getElementById("imageFrame");
const selectedImage = document.getElementById("selectedImage");

// Add an event listener to the file input
document.addEventListener("DOMContentLoaded", function () {
	var imageInput = document.getElementById("validatedCustomFile");
	if (imageInput) {
		imageInput.addEventListener("change", function () {
			if (imageInput.files.length > 0) {
				const selectedFile = imageInput.files[0];
				const objectURL = URL.createObjectURL(selectedFile);

				// Display the selected image in the frame
				selectedImage.src = objectURL;
				imageFrame.style.display = "block";
			} else {
				// If no file is selected, hide the frame
				imageFrame.style.display = "none";
			}
		});
	} else {
		console.error("imageInput element not found");
	}
});







