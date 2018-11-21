//基础控制层
app.controller('baseController', function ($scope, $http, $filter) {
        //获取登录用户名
        $scope.loadUsername = function () {
            //定义重定向URL
            $scope.redirectUrl = window.encodeURIComponent(location.href);
            //获取登录用户名
            $http.get("/user/showName").then(function (response) {
                $scope.loginName = response.data.loginName;
            });
        };

        //获取当前用户信息
        $scope.userInfo = function () {
            $http.get("/userSetting/userInfo")
                .then(function (response) {
                    $scope.userEntity = response.data;
                    $scope.userEntity.birthday = $filter('date')($scope.userEntity.birthday, 'yyyy-MM-dd HH:mm:ss');
                    $scope.userEntity.address = JSON.parse($scope.userEntity.address);
                    $scope.headPic = $scope.userEntity.headPic;
                });
        };

    }
)
;