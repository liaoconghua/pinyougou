//基础控制层
app.controller('baseController', function ($scope, $http) {
    //获取登录用户名方法
    $scope.loadUsername = function () {
        //定义重定向URL
        $scope.redirectUrl = encodeURIComponent(location.href);
        //获取登录用户名
        $http.get("/user/showName").then(function (response) {
            $scope.loginName = response.data.loginName;
        });
    };
});