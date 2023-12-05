var app = angular.module('myapp');
app.controller('product_detailctrl', function ($scope, $http, $window, $location, $sce) {

    $scope.soluong = 1;
    var masp = null;
    $scope.saotb = 0;
    window.onload = function () {
        // Lấy đoạn cuối của URL (được kỳ vọng là itemId)
        masp = window.location.pathname.split('/').pop();
        console.log('productID:', masp);

        if (masp) {
            $scope.loadItemDetails(masp); 
            // load đánh giá
            $http.get("/rest/loaddg/" + masp).then((resp) => {

                $scope.danhgias = resp.data;
                console.log("đánh giá", $scope.danhgias)
                var sodanhgia = $scope.danhgias.sodanhgia

                $scope.getStars(sodanhgia);
                $http.get("/rest/countdg/" + masp).then((resp) => {

                    $scope.solandg = resp.data;

                    $http.get("/rest/sumsao/" + masp).then((resp) => {

                        $scope.tongsao = resp.data;
                        if ($scope.tongsao.length == 0) {
                            $scope.saotb = 0;
                        } else {
                            $scope.saotb = $scope.tongsao / $scope.solandg;

                        }
                        console.log("saotrungbinh", $scope.saotb);
                    }, (error) => {

                        console.log("Error", error);
                    })


                }, (error) => {

                    console.log("Error", error);
                })

            }, (error) => {

                console.log("Error", error);
            })
            $scope.danhgiaResult = {};
            $http.get('/rest/count-by-sao/' + masp)
                .then(function (response) {
                    $scope.danhgiaResult = response.data;
                    console.log("các so lần danh gia", $scope.danhgiaResult)
                })
                .catch(function (error) {
                    console.error('Error fetching data:', error);
                });
        }
        $(document).ready(function () {
            var targetElement = $(".product_image_area");
            if (targetElement.length) {
                $('html, body').animate({
                    scrollTop: targetElement.offset().top
                }, 1000); // Thời gian cuộn (milliseconds)
            }
        });
    };



    $scope.getStars = function (sodanhgia) {
        var stars = [];
        for (var i = 0; i < sodanhgia; i++) {
            stars.push(i);
        }
        return stars;
    };
    // Số ký tự hiển thị ban đầu

    $scope.fullDescription = ""

    $scope.loadItemDetails = function (masp) {
        $http.get('/rest/sanphams/' + masp)
            .then(function (response) {
                $http.get('/rest/hinhsanphams/' + masp)
                    .then(function (response) {
                        $scope.hinhsanpham = response.data;
                        console.log('hinh:', $scope.hinhsanpham); // Add this line
                        $scope.hinh2 = $scope.hinhsanpham[0].hinh;

                        // Lấy hình ảnh từ đối tượng thứ hai
                        $scope.hinh3 = $scope.hinhsanpham[1].hinh;

                        // In ra để kiểm tra
                        console.log("Hình đầu tiên:",  $scope.hinh2);
                        console.log("Hình thứ hai:",  $scope.hinh3);
                    })

                // Add this line
                $scope.sanphams = response.data;
                // console.log("manhom", $scope.sanphams.nhomsanpham.manhom)
                $http.get('/rest/sanphamdm/' + $scope.sanphams.nhomsanpham.manhom)
                    .then(function (response) {
                        $scope.sanphamsdm = response.data;
                    })
                $scope.fullDescription = $scope.sanphams.thongtin;
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
                            var targetElement = $("#myTabContent");
                            if (targetElement.length) {
                                $('html, body').animate({
                                    scrollTop: targetElement.offset().top
                                }, 1000); // Thời gian cuộn (milliseconds)
                            }
                        });
                    }
                };
            })


    };


    // Gọi hàm để khởi tạo Owl Carousel

    $scope.incrementQuantity = function () {
        var result = document.getElementById('sst');
        var sst = result.value;
        if (!isNaN(sst)) {
            result.value++;
            $scope.soluong = result.value;
        }
        console.log("thong tin đã cắt", $scope.displayedDescription)
    }
    $scope.decrementQuantity = function () {
        var result = document.getElementById('sst');
        var sst = result.value;
        if (!isNaN(sst) && sst > 1) {
            result.value--;
            $scope.soluong = result.value;
        }
    }

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

    $scope.addToCart = function (masp) {
        if ($scope.account == null) {
            swal("Thông báo", "Vui lòng đăng nhập trước!", "warning").then(function () {
                sessionStorage.setItem('redirectFrom', $location.absUrl());
                window.location.href = "/auth/login/form";
            });
        } else {
            var url = `/rest/addcartsl`;
            var data = {
                masp: masp,
                soluong: $scope.soluong

            };
            $http.post(url, data).then(
                (resp) => {

                    swal("Thành Công", "Thêm sản phẩm vào giỏ hàng thành công", "success")
                },
                (error) => {
                    alert("Đặt hàng lỗi!");
                    console.log("Error", error);
                }
            );
        }
    }

    $scope.goToSinglePage = function (masp) {
        window.location.href = `/product-details/${masp}`; // Sử dụng chuỗi template (ES6)
    };
    $scope.selectedRating = "5";
    $scope.noidungdg = null;
    $scope.danhgia = function () {
        // alert("vao danh")
        if ($scope.account == null) {
            swal("Thông báo", "Vui lòng đăng nhập trước!", "warning").then(function () {
                sessionStorage.setItem('redirectFrom', $location.absUrl());
                window.location.href = "/auth/login/form";
            });
        } else {
            if ($scope.noidungdg == null) {
                swal("Thông báo", "Bạn chưa nhập nội dung đánh giá!", "warning")
            } else {
                // alert($scope.selectedRating)

                $http.get("/rest/danhgia", { params: { masp: parseInt(masp), sodanhgia: $scope.selectedRating, noidungdg: $scope.noidungdg } }).then((rsp) => {
                    swal("Thông báo", "Đánh giá thành công!", "success")
                    $scope.danhgias = rsp.data;
                    window.onload();
                    $scope.noidungdg = null;
                }, (error) => {
                    swal("Thông báo", "Bạn chưa mua sản phẩm hoặc đã đánh giá!", "warning")
                    $scope.noidungdg = null;
                })


            }
        }
    }

    $scope.xoadg = function(id){
        swal({
            title: "Cảnh báo",
            text: "Bạn có chắc chắn muốn xóa đánh giá này?",
            buttons: ["Hủy bỏ", "Đồng ý"],
            icon: "warning",
        }).then((willDelete) => {
            if (willDelete) {
                
                $http.delete(`/rest/remove-dg/`, { params: { masp: parseInt(masp), id: id} }).then(
                    (resp) => {
                        swal("Thành công", "Xóa đánh giá thành công!", "success")
                        $scope.danhgias = resp.data;
                    },
                    (error) => {
                        // alert("Đặt hàng lỗi!");
                        console.log("Error", error);
                    }
                );
            }


        });
    }
});