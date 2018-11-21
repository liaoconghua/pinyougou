/** 定义订单控制器 */
app.controller("orderController", function ($scope, $controller, $interval, $location, baseService) {
    /** 指定继承cartController */
    $controller("cartController", {$scope: $scope});

    //通过itemId查询用户购物车订单
    $scope.findCartByItemId = function () {
        let itemIds = $location.search().itemIds;
        // alert(itemIds);
        baseService.sendPost('/cart/findCartByItemId', itemIds)
        // baseService.sendGet('/cart/findCart')
            .then(function (response) {
                $scope.carts = response.data;
                if ($scope.carts.length < 1) {
                    alert('没有需要结算的商品！');
                    location.href = 'http://cart.pinyougou.com/cart.html';
                }
                $scope.totalEntity.totalNum = 0;
                $scope.totalEntity.totalMoney = 0.00;
                for (let i = 0; i < $scope.carts.length; i++) {
                    let cart = $scope.carts[i];
                    for (let j = 0; j < cart.orderItems.length; j++) {
                        let orderItem = cart.orderItems[j];
                        $scope.totalEntity.totalNum += orderItem.num;
                        $scope.totalEntity.totalMoney += orderItem.totalFee;
                    }
                }
            }, function (error) {
                if (error.status !== 200) {
                    alert("请选择商品后，再结算！");
                    location.href = 'http://cart.pinyougou.com/cart.html';
                }
            });
    };

    /** 根据登录用户获取地址 */
    $scope.findAddressByUser = function () {
        baseService.sendGet("/address/findAddressByUser")
            .then(function (response) {
                $scope.addressList = response.data;
                /** 循环用户地址集合 */
                for (let i in response.data) {
                    if (response.data[i].isDefault === "1") {
                        /** 设置默认地址 */
                        $scope.address = response.data[i];
                        break;
                    }
                }
            });
    };

    //修改地址详情数据回显
    $scope.show = function (address) {
        $scope.addressEntity = JSON.parse(JSON.stringify(address));
    };

    //添加地址
    $scope.addOrUpdateAddress = function () {
        let url = "addAddress";
        if ($scope.addressEntity.id) {
            url = "updateAddress";
        }
        baseService.sendPost("/address/" + url, $scope.addressEntity)
            .then(function (response) {
                if (response.data) {
                    alert("操作成功！");
                    $scope.findAddressByUser();
                } else {
                    alert("操作失败！");
                }
            });
    };

    //删除用户地址
    $scope.deleteAddress = function (id) {
        baseService.sendGet("/address/deleteAddress", "id=" + id)
            .then(function (response) {
                if (response.data) {
                    alert("删除成功！");
                    $scope.findAddressByUser();
                } else {
                    alert("删除失败！");
                }
            });
    };

    /** 选择地址 */
    $scope.selectAddress = function (item) {
        $scope.address = item;
    };

    /** 判断是否是当前选中的地址 */
    $scope.isSelectedAddress = function (item) {
        return item === $scope.address;
    };

    /** 定义order对象封装参数 */
    $scope.order = {paymentType: '1'};

    /** 选择支付方式 */
    $scope.selectPayType = function (type) {
        $scope.order.paymentType = type;
    };

    /** 保存订单 */
    $scope.saveOrder = function () {
        // 设置收件人地址
        $scope.order.receiverAreaName = $scope.address.address;
        // 设置收件人手机号码
        $scope.order.receiverMobile = $scope.address.mobile;
        // 设置收件人
        $scope.order.receiver = $scope.address.contact;
        // 发送异步请求
        baseService.sendPost("/order/save", $scope.order)
            .then(function (response) {
                if (response.data) {
                    // 如果是微信支付，跳转到扫码支付页面
                    if ($scope.order.paymentType === "1") {
                        location.href = "/order/pay.html";
                    } else {
                        // 如果是货到付款，跳转到成功页面
                        location.href = "/order/paysuccess.html";
                    }
                } else {
                    alert("订单提交失败！");
                }
            });
    };

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
        return $location.search().money;
    };


});