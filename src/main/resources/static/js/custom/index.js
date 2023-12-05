
var app = angular.module('myapp');

// let host = "http://localhost:8080/rest";
app.controller('homectrl', function ($scope, $http, $window, $location,checkoutService) {
    $scope.account = [];
    $scope.danhmucs = [];
    $scope.thuonghieus = [];
    $scope.sortOrder = 'asc';
    $scope.carts = []
    // code load danh mục
    $http.get(`/rest/danhmuc`)
        .then(function (resp) {
            $scope.danhmucs = resp.data;
            console.log("Danh mục", resp);
        }).catch(error => {
            console.log("Error", error)
        });
    // Mặc định sắp xếp tăng dần

    $scope.sortProducts = function () {
        // console.log("Before sorting:", $scope.totalItems.map(item => item.giasp));
        console.log("sortOder", $scope.sortOrder)
        $scope.totalItems.sort((a, b) => {
            if ($scope.sortOrder === 'asc') {
                return a.giasp - b.giasp;
            } else {
                return b.giasp - a.giasp;
            }
        });

        // console.log("After sorting:", $scope.totalItems.map(item => item.giasp));

        // Định dạng lại giá trước khi cập nhật trang hiện tại
        $scope.totalItems.forEach(item => {
            item.formattedGiasp = item.giasp.toLocaleString('vi-VN');
        });

        $scope.setPage($scope.pager.currentPage);
    };

    // code phân trang 
    $scope.pager = {};
    $scope.pagingSize = $scope.pagingSize || 6;
    $scope.itemPerPage = $scope.itemPerPage || 6;
    $scope.totalItems = [];

    $scope.load = function () {
        $scope.pagingSize = 5;
        $scope.dataPerPage = 6;
        $http.get(`/rest/sanpham`)
            .then(function (resp) {
                $scope.totalItems = resp.data;

                $scope.setPage(1);
                $scope.sortProducts();
                // console.log("Danh mục", $scope.totalItems.length);
            }).catch(error => {
                console.log("Error", error)
            });
        $scope.displayItems = [];

    }
    $scope.load();
    $scope.timkiems = function () {
        if (!$scope.searchnd) {
            swal("Thông báo", "Nhập tên sản phẩm cần tìm?", "info")
        } else {
            $http.get('/rest/product-search?productName=' + $scope.searchnd)
                .then(function (response) {
                    $scope.sanphamtk = response.data;
                    if ($scope.sanphamtk.length == 0) {
                        swal("Thông báo", "Không tìm thấy ", "info")

                    } else {
                        $('#modaltksp').modal("show")
                    }

                }).catch(function (error) {
                    console.error('Error fetching products:', error);

                });
        }

    }

    $scope.setPager = function (itemCount, currentPage, itemPerPage) {
        currentPage = currentPage || 1;
        var startPage, endPage;

        var totalPages = Math.ceil(itemCount / itemPerPage);
        if (totalPages <= $scope.pagingSize) {
            startPage = 1;
            endPage = totalPages;
        } else {
            if (currentPage + 1 >= totalPages) {
                startPage = totalPages - ($scope.pagingSize - 1);
                endPage = totalPages;
            } else {
                startPage = currentPage - parseInt($scope.pagingSize / 2);
                startPage = startPage <= 0 ? 1 : startPage;
                endPage = (startPage + $scope.pagingSize - 1) <= totalPages ? (startPage + $scope.pagingSize - 1) : totalPages;
                if (totalPages === endPage) {
                    startPage = endPage - $scope.pagingSize + 1;
                }
            }
        }

        var startIndex = (currentPage - 1) * itemPerPage;
        var endIndex = startIndex + itemPerPage - 1;

        var index = startPage;
        var pages = [];
        for (; index < endPage + 1; index++)
            pages.push(index);

        $scope.pager.currentPage = currentPage;
        $scope.pager.totalPages = totalPages;
        $scope.pager.startPage = startPage;
        $scope.pager.endPage = endPage;
        $scope.pager.startIndex = startIndex;
        $scope.pager.endIndex = endIndex;
        $scope.pager.pages = pages;
    }


    $scope.setPage = function (currentPage) {
        if (currentPage < 1 || currentPage > $scope.pager.totalPages)
            return;

        $scope.setPager($scope.totalItems.length, currentPage, $scope.itemPerPage);

        $scope.displayItems = $scope.totalItems.slice($scope.pager.startIndex, $scope.pager.endIndex + 1);
        // console.log("tong trang", $scope.totalItems);
    };
    // kết thúc code phân trang
    $scope.selectedBrandValue = null;
    $scope.locbrand = function (id) {

        $scope.selectedBrandValue = id;
        $scope.updateFilter()
        // console.log("Selected Brand Value: ", $scope.selectedBrandValue);
    };

    $scope.minPrice = null;
    $scope.maxPrice = null;
    $scope.selectedgia = null;

    $scope.locprice = function () {
        // Lấy giá trị min và max từ radio button đã chọn
        $scope.selectedgia = $scope.selectedPrice
        switch ($scope.selectedPrice) {
            case "2000000":
                $scope.minPrice = null;
                $scope.maxPrice = 2000000;
                break;
            case "4000000":
                $scope.minPrice = 2000000;
                $scope.maxPrice = 4000000;
                break;
            case "7000000":
                $scope.minPrice = 4000000;
                $scope.maxPrice = 7000000;
                break;
            case "13000000":
                $scope.minPrice = 7000000;
                $scope.maxPrice = 13000000;
                break;
            case "20000000":
                $scope.minPrice = 13000000;
                $scope.maxPrice = 20000000;
                break;
            case "20":
                $scope.minPrice = null;
                $scope.maxPrice = 20000000;
                break;
            default:
                // Trường hợp "Tất cả" hoặc giá trị không hợp lệ
                $scope.selectedgia = null;
                $scope.minPrice = null;
                $scope.maxPrice = null;
                break;
        }
        $scope.updateFilter();
        // // Gọi API hoặc thực hiện các hành động khác tại đây
        // $http.get('/rest/locprice', { params: { minPrice: $scope.minPrice, maxPrice: $scope.maxPrice } })
        //     .then(function (response) {
        //         $scope.totalItems = response.data;
        //         $scope.setPage(1);
        //         $scope.sortProducts();
        //         // Xử lý dữ liệu trả về
        //         console.log("Sản phẩm đã lọc gia ", response.data);

        //     })
        //     .catch(function (error) {
        //         console.error('Error fetching products:', error);
        //     });
        //     $scope.displayItems = [];
    };



    // loc sản phẩm  
    $scope.filterCriteria = {
        danhmuc: [],  // Danh sách các danh mục được chọn

    };
    $scope.updateFilter = function () {
        // Lọc ra các danh mục được chọn
        $scope.filterCriteria.danhmuc = $scope.danhmucs.filter(function (dm) {
            return dm.selected;
        }).map(function (dm) {
            return dm.manhom;
        });
        var danhMucString = $scope.filterCriteria.danhmuc.join(',');

        if ($scope.filterCriteria.danhmuc.length > 0) {
            if ($scope.selectedBrandValue != null) {
                if ($scope.selectedgia != null) {
                    //   alert("vào đây")
                    $http.get('/rest/locdmthgia', { params: { danhmuc: danhMucString, thuonghieu: $scope.selectedBrandValue, minPrice: $scope.minPrice, maxPrice: $scope.maxPrice } })
                        .then(function (response) {
                            $scope.totalItems = response.data;
                            $scope.setPage(1);
                            $scope.sortProducts();
                            console.log("Sản phẩm đã lọc ", response.data);
                        })
                        .catch(function (error) {
                            console.error('Error fetching products:', error);
                        });
                } else {
                    $http.get('/rest/locdmth', { params: { danhmuc: danhMucString, thuonghieu: $scope.selectedBrandValue } })
                        .then(function (response) {
                            $scope.totalItems = response.data;
                            $scope.setPage(1);
                            $scope.sortProducts();
                            console.log("Sản phẩm đã lọc ", response.data);
                        })
                        .catch(function (error) {
                            console.error('Error fetching products:', error);
                        });
                }

            } else if ($scope.selectedgia != null) {
                $http.get('/rest/locdmgia', { params: { danhmuc: danhMucString, minPrice: $scope.minPrice, maxPrice: $scope.maxPrice } })
                    .then(function (response) {
                        $scope.totalItems = response.data;
                        $scope.setPage(1);
                        $scope.sortProducts();
                        console.log("Sản phẩm đã lọc ", response.data);
                    })
                    .catch(function (error) {
                        console.error('Error fetching products:', error);
                    });
            } else {
                $http.get('/rest/productsloc', { params: { danhmuc: danhMucString } })
                    .then(function (response) {
                        // Xử lý dữ liệu khi yêu cầu thành công
                        $scope.totalItems = response.data;

                        $scope.setPage(1);
                        $scope.sortProducts();
                        console.log("Sản phẩm đã lọc ", response.data);
                    })
                    .catch(function (error) {
                        console.error('Error fetching products:', error);
                    });
            }
        } else if ($scope.selectedBrandValue != null) {
            if ($scope.selectedgia != null) {

                $http.get('/rest/locthprice', { params: { thuonghieu: $scope.selectedBrandValue, minPrice: $scope.minPrice, maxPrice: $scope.maxPrice } })
                    .then(function (response) {
                        $scope.totalItems = response.data;
                        $scope.setPage(1);
                        $scope.sortProducts();
                        console.log("Sản phẩm đã lọc thuong hieu và giá ", response.data);
                    })
                    .catch(function (error) {
                        console.error('Error fetching products:', error);
                    });
            } else {
                $http.get('/rest/thuonghieu/' + $scope.selectedBrandValue)
                    .then(function (response) {
                        $scope.totalItems = response.data;
                        $scope.setPage(1);
                        $scope.sortProducts();
                        console.log("Sản phẩm đã lọc ", response.data);
                    })
                    .catch(function (error) {
                        console.error('Error fetching products:', error);
                    });
            }

        } else if ($scope.selectedgia != null) {
            $http.get('/rest/locprice', { params: { minPrice: $scope.minPrice, maxPrice: $scope.maxPrice } })
                .then(function (response) {
                    $scope.totalItems = response.data;
                    $scope.setPage(1);
                    $scope.sortProducts();
                    console.log("Sản phẩm đã lọc ", response.data);
                })
                .catch(function (error) {
                    console.error('Error fetching products:', error);
                });
        } else if ($scope.selectedBrandValue == null || $scope.selectedgia == null) {
            $scope.load();
        }
        else {
            $scope.load();
        }
        // if($scope.minPrice == null){
        //     $http.get('/rest/locthmaxrice', {params: { danhmuc: danhMucString, thuonghieu: $scope.selectedBrandValue, maxPrice: $scope.maxPrice } })
        //     .then(function (response) {
        //         $scope.totalItems = response.data;
        //         $scope.setPage(1);
        //         $scope.sortProducts();
        //         console.log("Sản phẩm đã lọc thuong hieu và giá ", response.data);
        //     })
        //     .catch(function (error) {
        //         console.error('Error fetching products:', error);
        //     });
        // }

        $scope.displayItems = [];
    };

    // lọc sản phẩm theo hãng

    // kết thúc lọc sản phẩm 

    $http.get('/rest/thuonghieu').then(resp => {
        $scope.thuonghieus = resp.data;
        console.log("Success", resp)
    }).catch(error => {
        console.log("Error", error)
    });
    // load giỏ hàng
    $scope.loadgh = function () {
        $http.get('/rest/cart').then(
            (resp) => {
                $scope.carts = resp.data;
            },
            (error) => {
                // alert("Đặt hàng lỗi!");
                console.log("Error", error);
            }
        );
    }
    // code load ten account
    $http.get(`/rest/account`).then(resp => {
        $scope.account = resp.data;
        console.log("account", resp)
        if ($scope.account != null) {
            $scope.isLoggedIn = true;
            var redirectFrom = sessionStorage.getItem('redirectFrom');

            if (redirectFrom) {
                sessionStorage.removeItem('redirectFrom'); // Xóa đường dẫn đã lưu trong sessionStorage
                window.location.href = redirectFrom; // Chuyển hướng trở lại trang trước đó
            }
            $scope.loadgh();



        } else {
            $scope.isLoggedIn = false;
        }

    }).catch(error => {
        console.log("Error", error)
    });

    // load sản phẩm theo danh mục
    $scope.selectedCategory = null;
    $scope.loaddm = function (selectedItem) {
        if (selectedItem.selected) {
            // Nếu một checkbox được chọn, hủy chọn các checkbox khác
            $scope.danhmucs.forEach(function (item) {
                if (item !== selectedItem) {
                    item.selected = false;
                }
            });
            $scope.selectedCategory = selectedItem.manhom;
            $scope.filterData(selectedItem.manhom);
        } else if (selectedItem.manhom === $scope.selectedCategory) {
            // Nếu checkbox được bỏ chọn và là checkbox hiện đang được chọn, tải lại trang
            location.reload();
        }
    };

    $scope.goToCategory = function (manhom) {
        window.location.replace(`/shop#${manhom}`);
        $(document).ready(function () {
            var targetElement = $(".container");
            if (targetElement.length) {
                $('html, body').animate({
                    scrollTop: targetElement.offset().top
                }, 1000); // Thời gian cuộn (milliseconds)
            }
        });
    };
    $scope.filterData = function (manhom) {
        // Tải sản phẩm dựa trên categoryID
        // alert(manhom)
        $http.get(`/rest/sanpham-by-nhomsanpham/` + manhom)
            .then(function (resp) {
                $scope.totalItems = resp.data;
                $scope.setPage(1);
                console.log("Sản phẩm theo danh mục", $scope.totalItems);
            }).catch(error => {
                console.log("Error", error)
            });

        $scope.displayItems = [];



    };
    window.onhashchange = function () {
        // Lấy fragment từ URL
        var fragment = window.location.hash.substr(3); // Loại bỏ dấu "#"
        console.log('Fragment:', fragment);
        if (fragment) {
            $scope.$apply(function () {
                $scope.filterData(fragment);
            });
            $scope.danhmucs.forEach(function (dm) {
                dm.selected = fragment.includes(dm.manhom);
            });
        } else {
            $scope.$apply(function () {
                $scope.load();
            });
            $scope.danhmucs.forEach(function (dm) {
                dm.selected = false;
            });
        }
        

    };
    window.onload = function () {

        var fragment = window.location.hash.substr(3); // Loại bỏ dấu "#"
        console.log('Fragment:', fragment);
        if (fragment) {
            $scope.$apply(function () {
                $scope.filterData(fragment);
                $scope.danhmucs.forEach(function (dm) {
                    dm.selected = fragment.includes(dm.manhom);
                });
            });

        } else {

            $scope.$apply(function () {
                $scope.danhmucs.forEach(function (dm) {
                    dm.selected = false;
                });
            });
        }
        

    };
    $scope.login = function () {
        sessionStorage.setItem('redirectFrom', $location.absUrl());
        window.location.href = "/auth/login/form";
    }

    // thêm sản phẩm vào giỏ hàng
    $scope.addToCart = function (masp) {
        if ($scope.account == null) {
            swal("Thông báo", "Vui lòng đăng nhập trước!", "warning").then(function () {
                sessionStorage.setItem('redirectFrom', $location.absUrl());
                window.location.href = "/auth/login/form";
            });
        } else {
            var url = `/rest/addcart`;
            var data = { masp: masp };
            $http.post(url, data).then(
                (resp) => {
                    
                  $scope.carts =resp.data;
                    console.log("Giỏ hàng mới", $scope.carts)
                    swal("Thành Công", "Thêm sản phẩm vào giỏ hàng thành công", "success")

                },
                (error) => {
                    alert("Đặt hàng lỗi!");
                    console.log("Error", error);
                }
            );
        }

    }

    $http.get(`/rest/sanphamnew`).then(resp => {
        $scope.sanphamnews = resp.data;
        console.log("Success new", resp)
    }).catch(error => {
        console.log("Error", error)
    });



    $scope.goToSinglePage = function (masp) {
        window.location.href = `/product-details/${masp}`; // Sử dụng chuỗi template (ES6)
    };

    // Hàm cập nhật thông tin người dùngHàm cập nhật thông tin người dùngHàm cập nhật thông tin người dùng Hàm cập nhật thông tin người dùngHàm cập nhật thông tin người dùngHàm cập nhật thông tin người dùng
    $scope.updateAccount = function () {
        var usernamekh = document.getElementById("usernamekh").innerHTML;
        console.log($scope.account);
        // Kiểm tra xem họ và tên có bị bỏ trống hay không
        if (!$scope.account.hotenkh) {
            //swal("Thông báo", "Vui lòng nhập họ tên", "warning")
            alert("Vui lòng nhập họ tên!");
            return; // Dừng việc thực hiện yêu cầu PUT nếu họ và tên bị bỏ trống
        } else if (!$scope.account.dienthoai) {
            //swal("Thông báo", "Vui lòng nhập số điện thoại!", "warning")
            alert("Vui lòng nhập số điện thoại!");
            return; // Dừng việc thực hiện yêu cầu PUT nếu sdt bị bỏ trống
        } else if (!$scope.account.diachi) {
            //swal("Thông báo", "Vui lòng nhập địa chỉ!", "warning")
            alert("Vui lòng nhập địa chỉ!");
            return; // Dừng việc thực hiện yêu cầu PUT nếu địa chỉ bị bỏ trống
        }
        $http.put(`/rest/accountup/${usernamekh}`, $scope.account)
            .then(function (response) {
                // Xử lý phản hồi sau khi cập nhật thành công
                console.log(response.data);

                // Hiển thị thông báo thành công bằng SweetAlert
                swal("Thông báo", "Thông tin đã được cập nhật", "success")
            })
            .catch(function (error) {
                // Xử lý lỗi nếu cập nhật không thành công
                console.log("Error", error);

                // Hiển thị thông báo lỗi bằng SweetAlert
                swal("Thông báo", "Cập nhật thất bại!", "warning")
            });
    };
    $scope.updateSelectAllCheckbox = function () {
        $scope.selectAll = $scope.carts.every(function (gh) {
            return gh.selected;
        });
    };
    $scope.proceedToCheckout = function () {
        var selectedItems = $scope.carts.filter(function (gh) {
            return gh.selected;
        });

        if (selectedItems.length === 0) {
            swal("Thông Báo", "Vui lòng chọn tối thiểu 1 sản phẩm để thành toán", "warning")
            return;
        } else {
           
                checkoutService.setSelectedItems(selectedItems);

                window.location.href = `/checkout`;
            

        }
    };


});
app.service('checkoutService', function () {
    this.setSelectedItems = function (items) {
        localStorage.setItem('selectedItems', JSON.stringify(items));
    };

    this.getSelectedItems = function () {
        var items = localStorage.getItem('selectedItems');
        return items ? JSON.parse(items) : [];
    };
});

app.controller("cartctrl", function ($http, $scope, checkoutService, $location) {
    $scope.carts = []
    $scope.tongtien = 0;
    $scope.selectAll = false;
    $scope.isQuantityExceeded = false;


    // Chọn sản phẩm để thanh toán
    $scope.selectAllItems = function () {
        angular.forEach($scope.carts, function (cart) {
            cart.selected = $scope.selectAll;
        });
    };
    $scope.updateSelectAllCheckbox = function () {
        $scope.selectAll = $scope.carts.every(function (cart) {
            return cart.selected;
        });
    };
    $scope.isAnyItemSelected = function () {
        return $scope.carts.some(function (cart) {
            return cart.selected;
        });
    };
    $scope.calculateTotal = function () {
        // You can add any additional fees or discounts here if needed
        return $scope.calculateSubtotal();
    };
    $scope.calculateSubtotal = function () {
        var subtotal = 0;
        angular.forEach($scope.carts, function (cart) {
            if (cart.selected) {
                subtotal += cart.soluong * cart.giasp;
            }
        });
        return subtotal;
    };
    $scope.isQuantityExceeded = function () {
        var selectedItems = $scope.carts.filter(function (cart) {
            return cart.selected;
        });

        for (var i = 0; i < selectedItems.length; i++) {
            var cart = selectedItems[i];
            if (cart.soluong > cart.sanpham.soluong) {
                return true; // Số lượng sản phẩm vượt quá số lượng trong kho
            }
        }

        return false; // Không có vấn đề với số lượng sản phẩm
    };

    $scope.proceedToCheckout = function () {
        var selectedItems = $scope.carts.filter(function (cart) {
            return cart.selected;
        });

        if (selectedItems.length === 0) {
            swal("Thông Báo", "Vui lòng chọn tối thiểu 1 sản phẩm để thành toán", "warning")
            return;
        } else {
            if ($scope.isQuantityExceeded()) {
                swal("Thông Báo", "Số lượng sản phẩm không đủ trong kho", "warning")

            } else {
                // Sử dụng service để lưu danh sách sản phẩm đã chọn
                checkoutService.setSelectedItems(selectedItems);

                window.location.href = `/checkout`;
            }

        }
    };
    $scope.deleteSelectedItems = function () {
        var selectedItems = $scope.carts.filter(function (cart) {
            return cart.selected;
        });
        if (selectedItems.length === 0) {
            swal("Thông Báo", "Vui lòng chọn sản phẩm để xóa", "warning")
            return;
        } else {
            var maspList = selectedItems.map(function (item) {
                return item.sanpham.masp;
            });

            swal({
                title: "Cảnh báo",
                text: "Bạn có chắc chắn muốn xóa sản phẩm này?",
                buttons: ["Hủy bỏ", "Đồng ý"],
                icon: "warning",
            }).then((willDelete) => {
                if (willDelete) {
                    $http({
                        method: 'DELETE',
                        url: '/rest/removecart',
                        data: maspList,
                        headers: { 'Content-Type': 'application/json' }
                    }).then(
                        function (resp) {
                            // Xử lý kết quả từ server nếu cần
                            // Load lại giỏ hàng sau khi xóa
                            swal("Thành công", "Sản phẩm đã được xóa!", "success")

                            $scope.loadcart();


                            console.log(resp.data.message);
                        },
                        function (error) {
                            console.error('Error', error);
                        }
                    );




                }


            });

        }
    }


    // kết thúc chọn sản phẩm thanh toán


    $scope.goToSinglePage = function (masp) {
        window.location.href = `/product-details/${masp}`; // Sử dụng chuỗi template (ES6)
    };


    // load sản phẩm giỏ hàng 
    $scope.loadcart = function () {

        $http.get('/rest/cart').then(
            (resp) => {
                $scope.carts = resp.data;
                $scope.isCartEmpty = $scope.carts.length === 0;
                console.log("giohang", $scope.carts);



            },
            (error) => {
                // alert("Đặt hàng lỗi!");
                console.log("Error", error);
            }
        );
    }
    $scope.updateQuantity = function (cart, action) {
        if (action === 'increment') {
            cart.soluong++;
        } else if (action === 'decrement' && cart.soluong > 1) {
            cart.soluong--;
        }

        // Update the quantity in the database immediately
        $scope.updateCartItemQuantity(cart);
    };
    $scope.updateCartItemQuantity = function (carts) {
        // accountId được trả về từ server
        var url = `/rest/update-quantity/${carts.sanpham.masp}/${carts.soluong}`;
        $http.put(url)
            .then(response => {
                console.log("giohangmoi", response.data);
                // $scope.checktt = $scope.isAnyQuantityExceeded();
                console.log($scope.checktt)
            })
            .catch(error => {
                console.error(error);
            });


    };



    $scope.removesp = function (masp) {
        swal({
            title: "Cảnh báo",
            text: "Bạn có chắc chắn muốn xóa sản phẩm này?",
            buttons: ["Hủy bỏ", "Đồng ý"],
            icon: "warning",
        }).then((willDelete) => {
            if (willDelete) {
                var url = `/rest/remove-from-cart/` + masp
                $http.delete(url).then(
                    (resp) => {
                        swal("Thành công", "Sản phẩm đã được xóa!", "success")
                        $scope.loadcart();
                    },
                    (error) => {
                        // alert("Đặt hàng lỗi!");
                        console.log("Error", error);
                    }
                );
            }


        });

    }
    $scope.loadcart();


});
app.controller("checkctrl", function ($scope, $http, $location, checkoutService) {
    $scope.diachis = [];
    //load sản phẩm đã check
    $scope.tong = 0;
    $scope.sanphamcheck = []
    $scope.maxa = null;
    $scope.mahuyen = null;
    $scope.matinh = null;
    $scope.loadspcheck = function () {

        $scope.sanphamcheck = checkoutService.getSelectedItems();
        console.log("sản phẩm đã chọn bên cart", checkoutService.getSelectedItems())
        $scope.tongtien = $scope.calculateTotalAmount();
        $scope.tong = $scope.tongtien;
    }
    $scope.calculateTotalAmount = function () {
        var total = 0;
        angular.forEach($scope.sanphamcheck, function (item) {
            total += item.soluong * item.giasp;
        });
        return total;
    };
    $scope.loadspcheck()

    $scope.clear = function () {

        $scope.sanphamcheck = []
        checkoutService.setSelectedItems();
        // Xóa sạch các mặt hàng trong giỏ

    }

    // code test api tỉnh thành viet nam giao hàng nhanh
    var header = {
        "Token": "fed84849-6abb-11ee-b1d4-92b443b7a897",
        "ShopId": "189769"
    };
    $scope.provinces = []; // You should load this array with the available provinces/cities
    $scope.loadProvinces = function () {
        // Construct the API URL for loading provinces
        var apiURL = 'https://dev-online-gateway.ghn.vn/shiip/public-api/master-data/province';

        $http.get(apiURL, { headers: header })
            .then(response => {
                $scope.provinces = response.data.data;
                console.log("tỉnh thanh pho ghn", $scope.provinces);
            })
            .catch(error => {
                console.error(error);
            });
    };
    $scope.loadProvinces();
    $scope.diachi = null;

    $scope.onProvince = function () {
        $scope.districts = []; // Clear the current list of districts

        if ($scope.selectedProvince) {
            // Construct the API URL for the selected province
            var apiURL = 'https://dev-online-gateway.ghn.vn/shiip/public-api/master-data/district?province_id=' + $scope.selectedProvince.ProvinceID;
            $scope.matinh = $scope.selectedProvince.ProvinceID;
            $http.get(apiURL, { headers: header })
                .then(response => {
                    $scope.districts = response.data.data;
                    console.log("tinha gaihgahkgukagn", $scope.districts)
                })
                .catch(error => {
                    console.error(error);
                });
            $scope.diachi = $scope.selectedProvince.ProvinceName;

        }
        $("#nav-huyen-tab").tab("show");

        // $("#modaledit #nav-huyen-tab").tab("show");

    };
    $scope.onDistrict = function () {
        $scope.communes = []; // Clear the current list of communes

        if ($scope.selectedDistrict) {
            // Construct the API URL for the selected district

            $scope.mahuyen = $scope.selectedDistrict.DistrictID;
            var apiURL = 'https://dev-online-gateway.ghn.vn/shiip/public-api/master-data/ward?district_id=' + $scope.selectedDistrict.DistrictID;

            $scope.diachi = $scope.selectedDistrict.DistrictName + "," + $scope.diachi;
            $http.get(apiURL, { headers: header })
                .then(response => {
                    $scope.communes = response.data.data;
                    console.log("xã phường vn", $scope.communes)
                })
                .catch(error => {
                    console.error(error);
                });
        }
        $("#nav-xa-tab").tab("show");

    }

    $scope.onWard = function () {
        if ($scope.selectedWard) {
            $scope.maxa = $scope.selectedWard.WardCode;
            $scope.diachi = $scope.selectedWard.WardName + "," + $scope.diachi;
            $("#result").val($scope.diachi);
            console.log("tỉnh và huyện ", $scope.selectedWard.WardCode);

        }


    }
    // code test api tỉnh thành viet nam giao hàng nhanh


    $scope.phiship = 0;
    $scope.tienvc = 0;
    $scope.loaddcmd = function () {

        $http.get(`/rest/diachimd`).then(resp => {
            $scope.diachimd = resp.data;
            var mahuyen = $scope.diachimd.mahuyen

            // alert(mahuyen)

            var body = {
                "service_id": 53320,
                "insurance_value": 70000,
                "coupon": null,
                "from_district_id": 1572,
                "to_district_id": parseInt(mahuyen),
                "to_ward_code": null,
                "height": 15,
                "length": 15,
                "weight": 1000,
                "width": 15
            };

            // Tiếp tục xử lý yêu cầu HTTP ở đây


            var url = 'https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee';
            $http.post(url, body, { headers: header })
                .then(response => {
                    $scope.ship = response.data;
                    $scope.phiship = $scope.ship.data.total

                    $scope.tong = $scope.tong + $scope.phiship
                    $scope.tienvc = $scope.phiship;
                    $scope.tienshipgiam = $scope.phiship;
                    console.log("ship nè", $scope.phiship);

                })
                .catch(error => {
                    console.error(error);
                });


        }).catch(error => {
            console.log("Error", error)
        });


    }


    $scope.loaddcmd();
    $http.get(`/rest/account`).then(resp => {
        $scope.accounts = resp.data;


    }).catch(error => {
        console.log("Error", error)
    });


    $scope.loaddiachis = function () {
        $http.get(`/rest/diachi`).then(resp => {
            $scope.diachis = resp.data;
            console.log("diachiafaf", resp.data)
            if ($scope.diachis.length == 0) {
                $("#adddc").modal("show");
            }

        }).catch(error => {
            console.log("Error", error)
        });

    }
    $scope.loaddiachis();

    $scope.adddiachi = function (event) {
        var data = {
            tennn: $scope.account.hotenkh,
            sdtnn: $scope.account.dienthoai,
            diachi: $scope.diachict + ", " + $scope.diachi,
            maxa: $scope.selectedWard.WardCode,
            mahuyen: $scope.selectedDistrict.DistrictID,
            matinh: $scope.selectedProvince.ProvinceID,
        }


        $http.post("/rest/address", data).then(
            (resp) => {
                console.log("thanhcong", resp);
                swal("Thành Công", "Thêm địa chỉ thành công", "success").then(function () {
                    $("#adddc").modal("hide");
                })
                $scope.loaddcmd();

                $scope.loaddiachis();
            },
            (error) => {

                console.log("Error", error);
            }
        );





    }
    $scope.tiengiamgia = 0;
    $scope.magiamgia = 0;
    $scope.tienshipgiam = 0;

    $scope.cbthanhtoan = function () {
       
         
        
        console.log("mã giảm giá đã chọn", $scope.magiamgia)
        var checkoutData = {
            diachi: parseInt($scope.diachimd.id),
            ghichu: $scope.ghichu,
            totalAmount: $scope.tong,
            idgiam: $scope.magiamgia,
            sanphamcheck: $scope.sanphamcheck.map(item => item.sanpham.masp),
            tienvc:  $scope.tienshipgiam
        };
        // alert($scope.magiamgia)

        if ($scope.selectedPaymentMethod == "1") {
            $http.post('/rest/addhdonline', checkoutData)
                .then(function (response) {
                    swal("Thành Công", "Tiến hành thanh toán", "success").then(function () {
                        var paymentUrl = response.data.paymentUrl;
                        window.location.href = paymentUrl;
                        $scope.clear();
                    })
                    // var paymentUrl = response.data.paymentUrl;
                    console.log("duong link thanh toán", paymentUrl);
                    // window.location.href = paymentUrl;
                })
                .catch(function (error) {
                    console.log("thất bại", error);
                });
        } else if ($scope.selectedPaymentMethod == "2") {
            // alert("vao day")
            $http.post('/rest/zalopay/create-order', checkoutData)
                .then(function (response) {
                    swal("Thành Công", "Tiến hành thanh toán", "success").then(function () {
                        var urlzalopay = response.data.orderurl;
                        window.location.href = urlzalopay;
                        $scope.clear();
                    })
                    // var paymentUrl = response.data.paymentUrl;
                    console.log("duong link thanh toán", response.data);
                    // window.location.href = paymentUrl;
                })
                .catch(function (error) {
                    console.log("thất bại", error);
                });
        }
        else {
            $http.post('/rest/addhd', checkoutData)
                .then(function (response) {
                    swal("Thành Công", "Đặt hàng thành công", "success").then(function () {
                        window.location.href = 'account';
                        $scope.clear();
                    });

                })
                .catch(function (error) {
                    console.log("thất bại", error);
                });
        }


    }
    $scope.checkoptions = function () {
        $scope.diachimd = [];
        if ($scope.selectedId != undefined) {

            $http.get(`/rest/diachimdm/` + $scope.selectedId).then(resp => {

                $scope.diachimd = resp.data;
                var mahuyen = $scope.diachimd.mahuyen
                // alert(mahuyen)

                var body = {
                    "service_id": 53320,
                    "insurance_value": 70000,
                    "coupon": null,
                    "from_district_id": 1572,
                    "to_district_id": parseInt(mahuyen),
                    "to_ward_code": null,
                    "height": 15,
                    "length": 15,
                    "weight": 1000,
                    "width": 15
                };

                // Tiếp tục xử lý yêu cầu HTTP ở đây


                var url = 'https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee';
                $http.post(url, body, { headers: header })
                    .then(response => {
                        $scope.ship = response.data;
                        $scope.phiship = $scope.ship.data.total
                        console.log("ship nè duoi", $scope.phiship);
                        $scope.tienshipgiam = $scope.phiship;

                    })
                    .catch(error => {
                        console.error(error);
                    });

                console.log("augfuagjhgiuahighagh", $scope.diachimd)
            }).catch(error => {
                console.log("Error", error)
            });

        }



    };
    $scope.khuyenmais = [];
    $scope.loadkhm = function () {


        $http.get(`/rest/magiamgia`).then(resp => {
            $scope.khuyenmais = resp.data;

        }).catch(error => {
            console.log("Error", error)
        });
    }
    $scope.loadkhm();
    $scope.selectkhm = function () {
        $http.get(`/rest/magiamgia/` + $scope.chomakhm).then(resp => {
            $scope.khuyenmai = resp.data;
            console.log("magiamgia", $scope.khuyenmai)
            $("#modalmagiam").modal("hide")
        }).catch(error => {
            console.log("Error", error)
        });


    }

    $scope.apdungkm = function () {
        $scope.tong = $scope.tongtien + $scope.phiship

        if ($scope.khuyenmai.loai == "2") {
            if ($scope.tongtien >= $scope.khuyenmai.giamin) {
                $scope.tiengiamgia = $scope.khuyenmai.tiengiam;
                $scope.tong = $scope.tong - $scope.tiengiamgia;
                $scope.magiamgia = $scope.khuyenmai.idgiam;
            }
            else if ($scope.tongtien >= $scope.khuyenmai.giamin && $scope.tongtien <= $scope.khuyenmai.giamax) {
                $scope.tiengiamgia = $scope.khuyenmai.tiengiam;
                $scope.tong = $scope.tong - $scope.tiengiamgia;
                $scope.magiamgia = $scope.khuyenmai.idgiam;
            }
            else {
                swal("Thông Báo", "Bạn chưa đủ điều kiện áp dụng mã giảm giá", "warning");
                $scope.tiengiamgia = 0
                $scope.khuyenmai.magiam = "Chọn mã giảm giá"
                $scope.tienshipgiam = $scope.phiship
                $scope.magiamgia = 0;
            }
        } else if ($scope.khuyenmai.loai == "1") {
            if ($scope.tongtien >= $scope.khuyenmai.giamin) {
                if($scope.phiship <= $scope.khuyenmai.tiengiam){
                    $scope.tienshipgiam= 0;
                }else{
                    $scope.tienshipgiam = $scope.phiship - $scope.khuyenmai.tiengiam;

                }
                $scope.tong = $scope.tongtien + $scope.tienshipgiam;
                $scope.magiamgia = $scope.khuyenmai.idgiam;
                $scope.tiengiamgia = 0
            }

            else {
                swal("Thông Báo", "Bạn chưa đủ điều kiện áp dụng mã giảm giá", "warning");
                $scope.tiengiamgia = 0
                $scope.khuyenmai.magiam = "Chọn mã giảm giá"
                $scope.magiamgia = 0;
            }
        }

    }
    $scope.getdc = function (id) {
        var url = `rest/getdc/` + id;
        $http.get(url).then(
            (resp) => {
                $scope.diachiupdate = resp.data;
                $scope.dcct = resp.data.diachi.split(', ')[0];
                $scope.diacct = resp.data.diachi.split(', ')[1];
                // $("#ketqua").val(resp.data.diachi.split(', ')[1]);

                $scope.maxa = $scope.diachiupdate.maxa;
                $scope.mahuyen = $scope.diachiupdate.mahuyen
                $scope.matinh = $scope.diachiupdate.matinh

                console.log("địa chỉ update ", $scope.dcct);
                $("#modaledit").modal("show");
                $("#modaldoi").modal("hide");
            },
            (error) => {
                // alert("Đặt hàng lỗi!");
                console.log("Error", error);
            }
        );


    }
    $scope.capnhatdc = function (id) {
        $scope.address = '';
        if ($scope.diachi == null) {
            $scope.address = $scope.diacct;
        } else {
            $scope.address = $scope.diachi;
        }
        // console.log("giatrikq",$scope.diachi);

        var url = `rest/updatedc/` + id;
        var data = {
            tennn: $scope.diachiupdate.tennn,
            sdtnn: $scope.diachiupdate.sdtnn,
            diachi: $scope.dcct + ", " + $scope.address,
            maxa: $scope.maxa,
            mahuyen: $scope.mahuyen,
            matinh: $scope.matinh,
        }
        $http.put(url, data)
            .then(response => {
                swal("Thành Công", "Cập nhật địa chỉ thành công", "success").then(function () {

                    $("#modaledit").modal("hide");
                    $("#modaldoi").modal("show")

                });
                $scope.loaddiachis();
            })
            .catch(error => {
                console.error(error);
            });
    }

    $scope.momodal = function () {
        $("#modalmagiam").modal("show")
    }

});

app.controller("accountctrl", function ($http, $scope, $timeout) {
    $scope.accounts = [];
    $scope.diachis = [];
    $scope.hoadonct = [];
    $scope.maxa = null;
    $scope.mahuyen = null;
    $scope.matinh = null;
    $scope.load = function () {
        $http.get(`/rest/diachi`).then(resp => {
            $scope.diachis = resp.data;
            console.log("diachi", resp.data)

        }).catch(error => {
            console.log("Error", error)
        });
    }
    $scope.load();

    $http.get(`/rest/account`).then(resp => {
        $scope.accounts = resp.data;
        $scope.getHdId(1);
    }).catch(error => {
        console.log("Error", error)
    });
    // code test api tỉnh thành viet nam giao hàng nhanh
    var header = {
        "token": "fed84849-6abb-11ee-b1d4-92b443b7a897",
        "shop_id": "189769"
    };
    $scope.provinces = []; // You should load this array with the available provinces/cities
    $scope.loadProvinces = function () {
        // Construct the API URL for loading provinces
        var apiURL = 'https://dev-online-gateway.ghn.vn/shiip/public-api/master-data/province';

        $http.get(apiURL, { headers: header })
            .then(response => {
                $scope.provinces = response.data.data;
                console.log("tỉnh thanh pho ghn", $scope.provinces);
            })
            .catch(error => {
                console.error(error);
            });
    };
    $scope.loadProvinces();
    $scope.diachi = null;
    $scope.onProvince = function () {
        $scope.districts = []; // Clear the current list of districts

        if ($scope.selectedProvince) {
            $scope.matinh = $scope.selectedProvince.ProvinceID;
            // Construct the API URL for the selected province
            var apiURL = 'https://dev-online-gateway.ghn.vn/shiip/public-api/master-data/district?province_id=' + $scope.selectedProvince.ProvinceID;

            $http.get(apiURL, { headers: header })
                .then(response => {
                    $scope.districts = response.data.data;
                    console.log("tinha gaihgahkgukagn", $scope.districts)
                })
                .catch(error => {
                    console.error(error);
                });
            $scope.diachi = $scope.selectedProvince.ProvinceName;
        }
        $("#nav-huyen-tab").tab("show");
        $("#nav-huyen-tab1").tab("show");

    };
    $scope.onDistrict = function () {
        $scope.communes = []; // Clear the current list of communes

        if ($scope.selectedDistrict) {
            $scope.mahuyen = $scope.selectedDistrict.DistrictID;
            // Construct the API URL for the selected district
            var apiURL = 'https://dev-online-gateway.ghn.vn/shiip/public-api/master-data/ward?district_id=' + $scope.selectedDistrict.DistrictID;
            $scope.diachi = $scope.selectedDistrict.DistrictName + "," + $scope.diachi;
            $http.get(apiURL, { headers: header })
                .then(response => {
                    $scope.communes = response.data.data;
                })
                .catch(error => {
                    console.error(error);
                });
        }
        $("#nav-xa-tab").tab("show");

        $("#nav-xa-tab1").tab("show");
    }

    $scope.onWard = function () {
        if ($scope.selectedWard) {
            $scope.maxa = $scope.selectedWard.WardCode;
            $scope.diachi = $scope.selectedWard.WardName + "," + $scope.diachi;
            $("#result").val($scope.diachi);
            // console.log("tỉnh và huyện ", $scope.diachi);
            $("#ketqua").val($scope.diachi);
            console.log("xã phường vn", $scope.communes)
        }


    }
    // code test api tỉnh thành viet nam giao hàng nhanh
    $scope.adddiachi = function () {
        var data = {
            tennn: $scope.account.hotenkh,
            sdtnn: $scope.account.dienthoai,
            diachi: $scope.diachict + ", " + $scope.diachi,
            maxa: $scope.selectedWard.WardCode,
            mahuyen: $scope.selectedDistrict.DistrictID,
            matinh: $scope.selectedProvince.ProvinceID,
        }
        $http.post("/rest/address", data).then(
            (resp) => {
                console.log("thanhcong", resp);
                swal("Thành Công", "Thêm địa chỉ thành công", "success").then(function () {

                    $("#exampleModalCenter").modal("hide");

                });
                $("#nav-tinh-tab").tab("show");
                $scope.resetForm()
                $scope.load();
            },
            (error) => {

                console.log("Error", error);
            }
        );


    }
    $scope.getdc = function (id) {
        var url = `rest/getdc/` + id;
        $http.get(url).then(
            (resp) => {
                $scope.diachiupdate = resp.data;
                $scope.dcct = resp.data.diachi.split(', ')[0];
                $scope.diacct = resp.data.diachi.split(', ')[1];
                $scope.maxa = $scope.diachiupdate.maxa;
                $scope.mahuyen = $scope.diachiupdate.mahuyen,
                    $scope.matinh = $scope.diachiupdate.matinh,


                    // $("#ketqua").val(resp.data.diachi.split(', ')[1]);
                    console.log("địa chỉ update ", $scope.diachiupdate);
                $("#modaledit").modal("show");
            },
            (error) => {
                // alert("Đặt hàng lỗi!");
                console.log("Error", error);
            }
        );


    }
    $scope.capnhatdc = function (id) {
        $scope.address = '';
        if ($scope.diachi == null) {
            $scope.address = $scope.diacct;
        } else {
            $scope.address = $scope.diachi;
        }
        // console.log("giatrikq",$scope.diachi);

        var url = `rest/updatedc/` + id;
        var data = {
            tennn: $scope.diachiupdate.tennn,
            sdtnn: $scope.diachiupdate.sdtnn,
            diachi: $scope.dcct + ", " + $scope.address,
            maxa: $scope.maxa,
            mahuyen: $scope.mahuyen,
            matinh: $scope.matinh,
        }
        $http.put(url, data)
            .then(response => {
                swal("Thành Công", "Cập nhật địa chỉ thành công", "success").then(function () {

                    $("#modaledit").modal("hide");

                });
                $scope.load();
            })
            .catch(error => {
                console.error(error);
            });
    }
    $scope.removeadc = function (id) {
        swal({
            title: "Cảnh báo",
            text: "Bạn có chắc chắn muốn xóa địa chỉ này?",
            buttons: ["Hủy bỏ", "Đồng ý"],
            icon: "warning",
        }).then((willDelete) => {
            if (willDelete) {
                var url = `rest/remove-adress/` + id
                $http.delete(url).then(
                    (resp) => {
                        swal("Thành công", "Xóa địa chỉ thành công", "success")
                        $scope.load()
                    },
                    (error) => {
                        // alert("Đặt hàng lỗi!");
                        console.log("Error", error);
                    }
                );
            }


        })
    }
    $scope.updatett = function (id) {
        var url = `rest/updatett/` + id;
        $http.put(url)
            .then(response => {
                console.log("thanhcaonghaiuhfahfghaui", response.data);
                $scope.load();
            })
            .catch(error => {
                console.error(error);
            });
    }

    $scope.resetForm = function () {
        // $scope.selectedProvince = $scope.provinces[0];
        // $scope.selectedDistrict ='';
        // $scope.selectedWard ='';
        $scope.result = '';
        $scope.diachict = '';
    };
    $scope.hoadonct = [];
    $scope.getHdId = function (tthd) {
        var url = `/rest/hoadontt?tthd=${tthd}`;
        $http.get(url).then(resp => {
            $scope.hoadons = resp.data;
            

            // Sử dụng một hàm đệ quy để xử lý mỗi yêu cầu $http.get một cách tuần tự
            function processHoadonct(index) {
                if (index < resp.data.length) {
                    var currentItem = resp.data[index];
                    var url2 = `/rest/hoadonct?mahd=${currentItem.mahd}`;
                    $http.get(url2).then(response => {
                        $scope.hoadonct.push(response.data);
                        processHoadonct(index + 1); // Gọi lại hàm cho hóa đơn tiếp theo
                    }).catch(error => {
                        console.log("Error", error);
                    });
                } else {
                    // Tất cả các yêu cầu đã hoàn thành, in dữ liệu
                    console.log("hoadonct", $scope.hoadonct);
                    console.log("hoadon", $scope.hoadons);
                }
            }

            // Bắt đầu quá trình xử lý từ hóa đơn đầu tiên
            processHoadonct(0);
        }).catch(error => {
            console.log("Error", error);
        });
    };

    $scope.goToSinglePage = function (masp) {
        window.location.href = `/product-details/${masp}`; // Sử dụng chuỗi template (ES6)
    };

    $scope.calculateTotalAmount = function(hoadonct) {
        let totalAmount = 0;

        // Iterate over each item in the detailed invoice
        angular.forEach(hoadonct, function(hdct) {
            totalAmount += hdct.sanpham.giasp * hdct.soluong;
        });

        return totalAmount.toLocaleString('vi-VN');
    };

    $scope.huydon = function (mahd) {

        swal({
            title: "Cảnh báo",
            text: "Bạn có chắc chắn hủy đơn hàng này?",
            buttons: ["Hủy bỏ", "Đồng ý"],
            icon: "warning"
        }).then((willDelete) => {
            if (willDelete) {
                $scope.hoadons = {
                    trangthaihd: 5
                }
                var url = `/rest/hoadonhuy?mahd=${mahd}`;
                var item = angular.copy($scope.hoadons);
                $http.put(url, item).then(resp => {
                    $scope.getHdId(1);
                    swal("Thành Công", "Hủy đơn hàng thành công", "success")
                }).catch(error => {
                    console.log("Error", error)
                })
                $scope.getHdId(1);
                $scope.$apply();
            }
        })






    }

    




});

app.controller("kiemtractrl", function ($scope, $http,) {
    $(".card.mt-3").hide();

    $scope.kt = function () {
        if ($scope.mahd && $scope.email) {
            $http.get(`/rest/hdem?mahd=${$scope.mahd}&email=${$scope.email}`)
                .then(function (response) {
                    // Handle the successful response here
                    $scope.trackinghd = response.data;
                    if (response.data && response.data.data !== null) {
                        // console.log("hoauidgagfjhabdoadonct",  $scope.trackinghd);
                        $http.get(`/rest/hoadonct?mahd=${$scope.trackinghd.mahd}`).then(function (rsp) {
                            $scope.hdcts = rsp.data;
                            console.log("hoadoadonct", $scope.hdcts);
                            $(".card.mt-3").toggle();
                            $('html, body').animate({
                                scrollTop: $(".card").offset().top
                            }, 1000);
                        }).catch(function (error) {
                            // Handle errors here
                            console.error(error);
                        });
                    } else {
                        // Dữ liệu là null, thực hiện hành động khi dữ liệu không hợp lệ
                        swal("Thông báo", "Không tìm thấy đơn hàng của bạn", "error");
                    }


                })
                .catch(function (error) {
                    // Handle errors here 
                    // swal("Thông báo", "Mã hóa đơn hoặc email bạn nhập không đúng vui lòng kiểm tra thông tin đơn hàng trong email", "error")
                    console.error(error);
                });

        } else if (!$scope.mahd) {
            swal("Thông báo", "Vui lòng nhập mã hóa đơn", "warning")
        } else if (!$scope.email) {
            swal("Thông báo", "Vui lòng nhập email", "warning")
        }







    }
});

app.controller("blogctrl", function ($scope, $http, $sce) {
    $scope.pageSize = 3;
    $scope.currentPage = 1;
    $scope.tintucs = []
    $scope.goToSinglePage = function (id) {
        window.location.href = `/blog-signle/${id}`; // Sử dụng chuỗi template (ES6)
    };
    $scope.getCurrentPageTintucs = function () {
        var begin = ($scope.currentPage - 1) * $scope.pageSize;
        var end = begin + $scope.pageSize;
        return $scope.tintucs.slice(begin, end);
    };
    $scope.getPageArray = function (totalPages) {
        // Tạo một mảng từ 1 đến totalPages
        return new Array(totalPages).fill(0).map((_, index) => index + 1);
    };
    $scope.goToPage = function (page) {
        if (page >= 1 && page <= $scope.totalPages) {
            $scope.currentPage = page;
        }
    };
    $http.get(`/rest/counttin`)
        .then(function (resp) {
            $scope.counttin = resp.data;
            console.log("Danh mục", resp);
        }).catch(error => {
            console.log("Error", error)
        });
    $http.get("/rest/tintuc").then(function (resp) {
        $scope.tintucs = resp.data;
        $scope.totalPages = Math.ceil($scope.tintucs.length / $scope.pageSize);
    }).catch(function (error) {
        console.error(error);
    });
    // Tìm kiếm tin tức
    $scope.timktin = function () {

        $http.get("rest/timkiemtin", { params: { noidung: $scope.search } }).then(res => {
            $scope.tintucs = res.data;
            $scope.totalPages = Math.ceil($scope.tintucs.length / $scope.pageSize);

            console.log("tin tìm", res.data);
        }).catch(error => {
            console.log(error)
        })
    }


});

app.controller("blog-detail", function ($scope, $http, $sce) {
    $http.get(`/rest/counttin`)
        .then(function (resp) {
            $scope.counttin = resp.data;
            console.log("Danh mục", resp);
        }).catch(error => {
            console.log("Error", error)
        });
    $http.get("/rest/tintuc").then(function (resp) {
        $scope.tintucs = resp.data;

    }).catch(function (error) {
        // Handle errors here 
        // swal("Thông báo", "Mã hóa đơn hoặc email bạn nhập không đúng vui lòng kiểm tra thông tin đơn hàng trong email", "error")
        console.error(error);
    });
    window.onload = function () {
        // Lấy đoạn cuối của URL (được kỳ vọng là itemId)
        var id = window.location.pathname.split('/').pop();
        // console.log(':', masp);

        if (id) {
            $scope.loadItemDetails(id);
        }
        $(document).ready(function () {
            var targetElement = $(".blog_area");
            if (targetElement.length) {
                $('html, body').animate({
                    scrollTop: targetElement.offset().top
                }, 1000); // Thời gian cuộn (milliseconds)
            }
        });
    };
    $scope.fullDescription = ""
    $scope.loadItemDetails = function (id) {
        $http.get('/rest/tintuc/' + id).then(function (resp) {
            $scope.tintucct = resp.data;


            $scope.fullDescription = $scope.tintucct.noidung;
            // Số ký tự hiển thị ban đầu
            var initialCharsToShow = 2000;

            // Biến kiểm soát việc hiển thị mô tả
            $scope.showFullDescription = false;

            $scope.buttonText = 'Xem thêm';
            // Hàm cắt phần mô tả
            $scope.truncateDescription = function (description, chars) {
                if (description.length > chars) {
                    return $sce.trustAsHtml(description.substring(0, chars) + '...');
                }

                return $sce.trustAsHtml(description);
            };

            // Tạo một bản sao của phần mô tả ban đầu
            $scope.displayedDescription = $scope.truncateDescription($scope.fullDescription, initialCharsToShow);

            // Xử lý sự kiện khi nhấn nút "Xem thêm"
            $scope.toggleDescription = function () {
                $scope.showFullDescription = !$scope.showFullDescription;

                if ($scope.showFullDescription) {
                    $scope.displayedDescription = $sce.trustAsHtml($scope.fullDescription);
                    $scope.buttonText = 'Thu gọn';

                } else {
                    $scope.displayedDescription = $scope.truncateDescription($scope.fullDescription, initialCharsToShow);
                    $scope.buttonText = 'Xem thêm';
                    $(document).ready(function () {
                        var targetElement = $(".section_gap");
                        if (targetElement.length) {
                            $('html, body').animate({
                                scrollTop: targetElement.offset().top
                            }, 1000); // Thời gian cuộn (milliseconds)
                        }
                    });
                }
            };
        }).catch(function (error) {
            console.error(error);
        });
    }
    $scope.goToSinglePage = function (id) {
        window.location.href = `/blog-signle/${id}`; // Sử dụng chuỗi template (ES6)
    };
});
