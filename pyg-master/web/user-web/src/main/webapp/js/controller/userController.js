/** 定义控制器层 */
app.controller('userController', function ($scope, $controller, baseService) {

    //继承基础控制器
    $controller('baseController', {$scope: $scope});

    /** 定义用户对象 */
    $scope.user = {};

    /** 定义保存用户方法 */
    $scope.save = function () {
        if ($scope.user.password !== $scope.password) {
            alert("密码不一致，请重新输入！");
            return;
        }
        baseService.sendPost("/user/save", $scope.user)
            .then(function (response) {
                if (response.data) {
                    alert("注册成功！");
                    $scope.user = {};
                    $scope.password = "";
                } else {
                    alert("注册失败！");
                }
            });
    };

    /** 定义发送短信验证码方法 */
    $scope.sendCode = function () {
        if ($scope.user.phone) {
            baseService.sendGet("/user/sendCode?phone="
                + $scope.user.phone)
                .then(function (response) {
                    alert(response.data ? "发送成功！" : "发送失败！");
                });
        } else {
            alert("请输入手机号码！");
        }
    };

    /** 定义保存用户方法 */
    $scope.save = function () {
        if ($scope.user.password !== $scope.password) {
            alert("密码不一致，请重新输入！");
            return;
        }
        baseService.sendPost("/user/save?smsCode="
            + $scope.smsCode, $scope.user)
            .then(function (response) {
                if (response.data) {
                    alert("注册成功！");
                    $scope.user = {};
                    $scope.password = "";
                    $scope.smsCode = "";
                } else {
                    alert("注册失败！");
                }
            });
    }


});