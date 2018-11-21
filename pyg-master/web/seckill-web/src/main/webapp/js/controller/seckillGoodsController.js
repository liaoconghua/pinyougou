/** 定义秒杀商品控制器 */
app.controller("seckillGoodsController", function ($scope, $controller, $location, $timeout, baseService) {

    /** 指定继承cartController */
    $controller("baseController", {$scope: $scope});

    //获取秒杀商品列表
    $scope.findAllSeckillGoods = function () {
        baseService.sendGet("/seckillGoods/findAllSeckillGoods").then(function (response) {
            $scope.seckillGoodsList = response.data;
        });
    };

    //获得用户选择的商品
    $scope.findSeckillGoodsById = function () {
        let id = $location.search().id;
        baseService.sendGet('/seckillGoods/findSeckillGoodsById?id=' + id)
            .then(function (response) {
                $scope.item = response.data;
                $scope.getSeckillTimeStr($scope.item.endTime);
            });
    };

    //获取秒杀时间字符串
    $scope.getSeckillTimeStr = function (endTime) {
        if (new Date(endTime).getTime() > new Date().getTime()) {
            //获取结束时间的毫秒数
            let milliSeconds = Math.floor(new Date(endTime).getTime() - new Date().getTime());
            //获取秒数
            let seconds = Math.floor(milliSeconds / 1000);
            //获取分钟数
            let minutes = Math.floor(seconds / 60);
            //获取小时数
            let hours = Math.floor(minutes / 60);
            //剩余秒杀时间数组
            let seckillTimeArr = [];
            if (hours > 0) {
                seckillTimeArr.push($scope.formattingTime(Math.floor(hours)) + ":");
            }
            if (minutes > 0) {
                seckillTimeArr.push($scope.formattingTime(Math.floor(minutes - hours * 60)) + ":");
            }
            if (seconds > 0) {
                seckillTimeArr.push($scope.formattingTime(Math.floor(seconds - minutes * 60)));
            }
            //剩余秒杀时间字符串
            $scope.seckillTimeStr = seckillTimeArr.join("");
            $timeout(function () {
                $scope.getSeckillTimeStr(endTime);
            }, 1000);
        } else {
            alert("秒杀结束");
            location.href = "http://seckill.pinyougou.com";
        }
    };

    //格式化时间，补0
    $scope.formattingTime = function (time) {
        return time < 9 ? "0" + time : time;
    };

    //提交订单
    $scope.submitOrder = function () {
        //判断用户是否登录
        if ($scope.loginName) {
            baseService.sendGet("/seckillOrder/submitOrder?id=" + $scope.item.id)
                .then(function (response) {
                    if (response.data) {
                        location.href = "/order/pay.html";
                    } else {
                        alert("下单失败！");
                    }
                });
        } else {
            location.href = "http://sso.pinyougou.com/?service=" + $scope.redirectUrl;
        }
    }

});