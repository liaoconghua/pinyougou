/** 首页控制器 */
app.controller("indexController", function ($scope, $controller, baseService) {

    //继承继承控制层
    $controller('baseController', {$scope: $scope});

    //定义支付状态数组
    $scope.payStatusType = ['未付款', '已付款', '未发货', '已发货', '交易成功', '交易关闭', '待评价'];

    //定义用户针对支付状态操作的数组
    $scope.payStatusOperation = ['立即付款', '提醒发货', '提醒发货', '确认收货', '去评价', '立即购买', '去评价'];

    //定义跳转页码的数据变量
    $scope.jumpPage = 1;

    /** 定义查询参数对象 */
    $scope.orderParam = {
        pageNum: 1, pageSize: 10, username: ''
    };

    //获得该用户的所有订单
    $scope.findOrderByPage = function () {
        if (location.href === "http://user.pinyougou.com/home-order-pay.html") {
            $scope.orderParam.status = 1;
        } else if (location.href === "http://user.pinyougou.com/home-order-send.html") {
            $scope.orderParam.status = 2;
        } else if (location.href === "http://user.pinyougou.com/home-order-receive.html") {
            $scope.orderParam.status = 4;
        } else if (location.href === "http://user.pinyougou.com/home-order-evaluate.html") {
            $scope.orderParam.status = 7;
        }
        if ($scope.loginName) {
            $scope.orderParam.username = $scope.loginName;
            // alert(JSON.stringify($scope.orderParam));
            baseService.sendPost('/user/findOrderByPage', $scope.orderParam)
                .then(function (response) {
                    $scope.resultMap = response.data;
                    /** 调用初始化页码方法 */
                    initPageNum();
                    // let str = "Apple iPhone 8 Plus (A1864) 移动4G 64G";
                    // let name = str.substr(str.lastIndexOf(' ', str.lastIndexOf(' ') - 1) + 1);
                    // alert(name);
                });
        }
    };
    window.onload = function () {
        $scope.findOrderByPage();
    };

    /** 初始化页码 */
    let initPageNum = function () {
        /** 定义页码数组 */
        $scope.pageNums = [];
        /** 获取总页数 */
        let totalPages = $scope.resultMap.totalPages;
        /** 开始页码 */
        let firstPage = 1;
        /** 结束页码 */
        let lastPage = totalPages;

        /** 如果总页数大于5，显示部分页码 */
        if (totalPages > 5) {
            // 如果当前页码处于前面位置
            if ($scope.orderParam.pageNum <= 3) {
                lastPage = 5;
                $scope.firstDot = false;
                $scope.lastDot = true;
            } else if ($scope.orderParam.pageNum > totalPages - 3) {
                lastPage = totalPages;
                firstPage = totalPages - 4;
                $scope.firstDot = true;
                $scope.lastDot = false;
            } else {//当前页码在中间位置
                lastPage = $scope.orderParam.pageNum + 2;
                firstPage = $scope.orderParam.pageNum - 2;
                $scope.firstDot = true;
                $scope.lastDot = true;
            }
        } else {
            $scope.firstDot = false; // 前面没点
            $scope.lastDot = false; // 后面没点
        }

        for (let i = firstPage; i <= lastPage; i++) {
            // alert("l");
            $scope.pageNums.push(i);
        }
        // alert($scope.pageNums);
    };

    //修改当前页码
    $scope.changePage = function (oldPage) {
        let curPage = parseInt(oldPage);//转换页数
        if (curPage >= 1 && curPage <= $scope.resultMap.totalPages) {
            if ($scope.orderParam.pageNum !== curPage) {
                $scope.orderParam.pageNum = curPage;
                $scope.jumpPage = $scope.orderParam.pageNum;
                $scope.findOrderByPage();
            }
        }
    };

    //监控变量jumpPage
    $scope.$watch('jumpPage', function (newValue, oldValue) {
        let number = parseInt(newValue);
        if (!isNaN(number)) {//number是一个数字且不等于1
            if (number <= 1) {//不能小于1
                $scope.jumpPage = 1;
            } else if (number > $scope.resultMap.totalPages) {//不能大于总页数
                $scope.jumpPage = $scope.resultMap.totalPages;
            } else {
                $scope.jumpPage = number;
            }
        } else {
            $scope.jumpPage = parseInt(oldValue);
        }
    });

    //取消订单
    $scope.closeOrder = function (orderId) {
        baseService.sendGet('/order/closeOrder' + '?orderId=' + orderId)
            .then(function (response) {
                if (response.data) {
                    alert("取消成功！");
                    $scope.findOrderByPage();
                } else {
                    alert("取消失败！");
                }
            });
    };


    /** 保存订单 */
    $scope.saveOrder = function (orderEntity) {
        if (orderEntity.status !== "1") {
            return;
        }
        // 发送异步请求
        baseService.sendPost("/order/save", orderEntity)
            .then(function (response) {
                if (response.data) {
                    // 如果是微信支付，跳转到扫码支付页面
                    if (orderEntity.paymentType === "1") {
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

    /** 定义多选数组 */
    $scope.checkedArr = [];

    //获取需要合并订单的ids
    $scope.ids = [];
    $scope.updateSelection = function (id, index) {
        if ($scope.checkedArr[index]) {
            $scope.ids.push(id);
        } else {
            $scope.ids.splice($scope.ids.indexOf(id), 1);
        }
        $scope.ckAll = $scope.resultMap.orderList.length === $scope.ids.length;
        // alert($scope.ids);
    };

    /** 全选功能 */
    $scope.checkedAll = function (selected) {
        let orderList = $scope.resultMap.orderList;
        $scope.ids = [];
        for (let i = 0; i < orderList.length; i++) {
            $scope.checkedArr[i] = selected;
            if (selected) {
                $scope.ids.push(orderList[i].orderId);
            }
        }
        // alert($scope.ids);
    };

    //通过id数组生成支付日志
    $scope.mergeOrder = function () {
        // alert($scope.ids);
        // return;
        if ($scope.ids.length > 0) {
            baseService.sendGet("/order/mergeOrder?ids=" + $scope.ids)
                .then(function (response) {
                    if (response.data) {
                        // 如果是微信支付，跳转到扫码支付页面
                        // if (orderEntity.paymentType === "1") {
                            location.href = "/order/pay.html";
                        // } else {
                            // 如果是货到付款，跳转到成功页面
                            // location.href = "/order/paysuccess.html";
                        // }
                    } else {
                        alert("订单提交失败！");
                    }
                });
        } else {
            alert("未选择要提交的数据!");
        }
    };

});
