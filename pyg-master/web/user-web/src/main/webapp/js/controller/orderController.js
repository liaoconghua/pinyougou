/** 定义订单控制器 */
app.controller("orderController", function ($scope, $controller, $interval, $location, baseService) {
    /** 指定继承cartController */
    $controller("cartController", {$scope: $scope});

    /** 定义order对象封装参数 */
    $scope.order = {paymentType: '1'};

    /** 生成微信支付二维码 */
    $scope.genPayCode = function () {
        baseService.sendGet("/order/getPayCode").then(function (response) {
            /** 获取金额(转化成元) */
            $scope.money = (response.data.totalFee / 100).toFixed(2);
            /** 获取订单交易号 */
            $scope.outTradeNo = response.data.outTradeNo;
            /** 生成二维码 */
            // var qr = new QRious({
            //     element: document.getElementById('qrious'),
            //     size: 250,
            //     level: 'H',
            //     value: response.data.codeUrl
            // });
            document.getElementById('zxing').src = location.origin + "/barcode" + "?url=" + response.data.codeUrl;
            //开启定时器
            var timer = $interval(function () {
                //发送请求，查询支付状态
                baseService.sendGet("/order/queryPayStatus?outTradeNo="
                    + $scope.outTradeNo)
                    .then(function (response) {
                        if (response.data.status === 1) {// 支付成功
                            /** 取消定时器 */
                            $interval.cancel(timer);
                            location.href = "/order/paysuccess.html?money="
                                + $scope.money;
                        }
                        if (response.data.status === 3) {// 支付失败
                            /** 取消定时器 */
                            $interval.cancel(timer);
                            location.href = "/order/payfail.html";
                        }
                    });
            }, 3000, 60);
            /** 执行60次(3分钟)之后需要回调的函数 */
            timer.then(function () {
                alert("微信支付二维码失效！");
            });
        });
    };

    /** 获取支付总金额 */
    $scope.getMoney = function () {
        $scope.money = $location.search().money;
    };


});