document.addEventListener('DOMContentLoaded', function() {
   
var categories = [];

var data = [];

var table1 = document.getElementById("bang1");
// Lấy tất cả các hàng trong bảng
var rows = table1.querySelectorAll('table tr');

// Lặp qua từng hàng trong bảng
rows.forEach(function (row) {
	var columns = row.querySelectorAll('td');
	if (columns.length === 2) {
		// Lấy dữ liệu từ cột đầu tiên và thêm vào mảng categories
		categories.push(columns[0].textContent);

		// Lấy dữ liệu từ cột thứ hai và thêm vào mảng data
		data.push(parseFloat(columns[1].textContent)); // Chuyển đổi sang kiểu số nếu cần
	}
});
const chart = Highcharts.chart('thongkedoanhthu', {
	title: {
		text: 'THỐNG KÊ DOANH THU',
		align: 'center'
	},
	colors: [
		'#4caefe',
		'#3fbdf3',
		'#35c3e8',
		'#2bc9dc',
		'#20cfe1',
		'#16d4e6',
		'#0dd9db',
		'#03dfd0',
		'#00e4c5',
		'#00e9ba',
		'#00eeaf',
		'#23e274'
	],
	xAxis: [{
		categories: categories.map(function (category) {
			return 'Tháng ' + category;
		}),
		// crosshair: true
	}],
	series: [{
		type: 'column',
		name: 'Doanh thu',
		borderRadius: 5,
		colorByPoint: true,
		data: data,
		showInLegend: false
	}]
});
document.getElementById('plain').addEventListener('click', () => {
	chart.update({
		chart: {
			inverted: false,
			polar: false
		},

	});
});
document.getElementById('inverted').addEventListener('click', () => {
	chart.update({
		chart: {
			inverted: true,
			polar: false
		},
	});
});

// thống kê hóa đơn và số lượng người đang kí

var categories = [];
var data2 = [];
var table2 = document.getElementById("bang2");
// Lấy tất cả các hàng trong bảng
var rows = table2.querySelectorAll('table tr');
// Lặp qua từng hàng trong bảng

rows.forEach(function (row) {
	var columns = row.querySelectorAll('td');
	if (columns.length === 2) {
		// Lấy dữ liệu từ cột đầu tiên và thêm vào mảng categories
		categories.push(columns[0].textContent);

		// Lấy dữ liệu từ cột thứ hai và thêm vào mảng data
		data2.push(parseFloat(columns[1].textContent)); // Chuyển đổi sang kiểu số nếu cần
	}
});
var data3 = [];
var table3 = document.getElementById("bang3");
// Lấy tất cả các hàng trong bảng
var rows = table3.querySelectorAll('table tr');
// Lặp qua từng hàng trong bảng

rows.forEach(function (row) {
	var columns = row.querySelectorAll('td');
	if (columns.length === 2) {
		// Lấy dữ liệu từ cột đầu tiên và thêm vào mảng categories
		categories.push(columns[0].textContent);

		// Lấy dữ liệu từ cột thứ hai và thêm vào mảng data
		data3.push(parseFloat(columns[1].textContent)); // Chuyển đổi sang kiểu số nếu cần
	}
});
Highcharts.chart('container2', {
	chart: {
		zoomType: 'xy'
	},
	title: {
		text: 'Thống kê số khách hàng đăng kí và đơn hàng theo tháng',
		align: 'center'
	},
	xAxis: [{
		categories: categories.map(function (category) {
			return 'Tháng ' + category;
		}),
		crosshair: true
	}],
	yAxis: [{ // Primary yAxis
		labels: {
			forma: function () {
				// this.value chứa giá trị dữ liệu của bạn
				// Thay thế {data} bằng giá trị dữ liệu của bạn
				return this.value + 'người'; // Thay đổi '°C' bằng đơn vị hoặc chuỗi bạn muốn hiển thị
			},
			style: {
				color: Highcharts.getOptions().colors[1]
			}
		},
		title: {
			text: '',
			style: {
				color: Highcharts.getOptions().colors[1]
			}
		}
	}, { // Secondary yAxis
		title: {
			text: '',
			style: {
				color: Highcharts.getOptions().colors[0]
			}
		},

		labels: {
			formatter: function () {
				// this.value chứa giá trị dữ liệu của bạn
				// Thay thế {data} bằng giá trị dữ liệu của bạn
				return Math.round(this.value); // Thay đổi '°C' bằng đơn vị hoặc chuỗi bạn muốn hiển thị
			},
			style: {
				color: Highcharts.getOptions().colors[0]
			}
		},
		opposite: true
	}],
	tooltip: {
		shared: true
	},
	legend: {
		align: 'left',
		x: 80,
		verticalAlign: 'top',
		y: 60,
		floating: true,
		backgroundColor:
			Highcharts.defaultOptions.legend.backgroundColor || // theme
			'rgba(255,255,255,0.25)'
	},
	series: [{
		name: 'Số lượng đơn hàng',
		type: 'column',
		data: data3,
		tooltip: {
			valueSuffix: ' đơn hàng'
		}

	}, {
		name: 'Số người đăng kí',
		type: 'spline',
		data: data2,
		tooltip: {
			valueSuffix: ' người đăng kí'
		}
	}]
});
});

