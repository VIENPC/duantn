var app = angular.module("myapp", []);
app.filter('unsafeHtml', ['$sce', function ($sce) {
    return function (val) {
        return $sce.trustAsHtml(val);
    };
}]);

