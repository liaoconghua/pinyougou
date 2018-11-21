/** 定义购物车控制器 */
app.controller('cartController', function ($scope, $controller, baseService) {
    // 指定继承baseController
    $controller('baseController', {$scope: $scope});
    /** 定义总计对象 */
    $scope.totalEntity = {totalNum: 0, totalMoney: 0.00, subtotalMoney: 0.00, subtotalNum: 0};

    /** 查询购物车数据 */
    $scope.findCart = function () {
        baseService.sendGet("/cart/findCart")
            .then(function (response) {
                $scope.carts = response.data;
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
            });
    };

    /** 添加SKU商品到购物车 */
    $scope.addCart = function (itemId, num, orderItem, fatherCk, index) {
        if (num === -1 && fatherCk !== undefined && index !== undefined &&
            $scope.carts[fatherCk].orderItems[index].num <= 1) {
            return;
        }
        baseService.sendGet("/cart/addCart",
            "itemId=" + itemId + "&num=" + num)
            .then(function (response) {
                if (response.data) {
                    // 重新加载购物车数据
                    $scope.findCart();
                    if ($scope.selectedOne(fatherCk, itemId)) {
                        $scope.totalEntity.subtotalNum += num;
                        if (num > 0) {
                            $scope.totalEntity.subtotalMoney += orderItem.price;
                        } else {
                            $scope.totalEntity.subtotalMoney -= orderItem.price;
                        }
                    }
                } else {
                    alert("操作失败！");
                }
            });
    };

    //跳转到结算页
    $scope.clearing = function () {
        if ($scope.checkedArr.length > 0) {
            for (let i = 0; i < $scope.checkedArr.length; i++) {
                if ($scope.checkedArr[i].length > 0) {
                    location.href = "/order/getOrderInfo.html?itemIds=" + JSON.stringify($scope.checkedArr);
                    return;
                }
            }
        }
        alert("你未选择需要结算的商品！");
    };

    /** 定义多选数组 */
    $scope.checkedArr = [];
    //定义全选模型
    $scope.ckAll = [];

    //全选按钮
    function checkedFatherAll() {
        let checkedTotal = 0;
        for (let i = 0; i < $scope.checkedArr.length; i++) {
            checkedTotal += $scope.checkedArr[i].length;
        }
        let cartsTotal = 0;
        for (let i = 0; i < $scope.carts.length; i++) {
            cartsTotal += $scope.carts[i].orderItems.length
        }
        $scope.ckFatherAll = cartsTotal === checkedTotal;
    }

    //更新选中状态
    $scope.updateSelection = function (itemId, fatherCk, index) {
        if (!$scope.checkedArr[fatherCk]) {
            for (let i = 0; i <= fatherCk; i++) {
                if ($scope.checkedArr[i] === undefined) {
                    $scope.checkedArr[i] = [];
                }
            }
        }
        if ($scope.checkedArr[fatherCk].indexOf(itemId) === -1) {
            $scope.checkedArr[fatherCk][$scope.checkedArr[fatherCk].length] = itemId;
            let orderItem = $scope.carts[fatherCk].orderItems[index];
            $scope.totalEntity.subtotalMoney += orderItem.price * orderItem.num;
            $scope.totalEntity.subtotalNum += orderItem.num;
        } else {
            $scope.checkedArr[fatherCk].splice($scope.checkedArr[fatherCk].indexOf(itemId), 1);
            let orderItem = $scope.carts[fatherCk].orderItems[index];
            $scope.totalEntity.subtotalMoney -= orderItem.totalFee;
            $scope.totalEntity.subtotalNum -= orderItem.num;
        }
        $scope.ckAll[fatherCk] = $scope.checkedArr[fatherCk].length === $scope.carts[fatherCk].orderItems.length;
        checkedFatherAll();
    };

    /** 全选功能,将itemId push到ids中 */
    $scope.checkedAll = function (selected, fatherCk) {
        if (fatherCk !== undefined) {
            let orderItems = $scope.carts[fatherCk].orderItems;
            for (let i = 0; i <= fatherCk; i++) {
                if ($scope.checkedArr[i] === undefined) {
                    $scope.checkedArr[i] = [];
                }
            }
            $scope.checkedArr[fatherCk] = [];
            for (let i = 0; i < orderItems.length; i++) {
                if (selected) {
                    $scope.checkedArr[fatherCk][$scope.checkedArr[fatherCk].length] = orderItems[i].itemId;
                    $scope.totalEntity.subtotalMoney += orderItems[i].totalFee;
                    $scope.totalEntity.subtotalNum += orderItems[i].num;
                } else {
                    $scope.totalEntity.subtotalMoney -= orderItems[i].totalFee;
                    $scope.totalEntity.subtotalNum -= orderItems[i].num;
                }
            }
        } else {
            for (let i = 0; i < $scope.carts.length; i++) {
                let orderItems = $scope.carts[i].orderItems;
                $scope.checkedArr[i] = [];
                for (let j = 0; j < orderItems.length; j++) {
                    if (selected) {
                        $scope.checkedArr[i][$scope.checkedArr[i].length] = orderItems[j].itemId;
                        $scope.totalEntity.subtotalMoney += orderItems[j].totalFee;
                        $scope.totalEntity.subtotalNum += orderItems[j].num;
                    } else {
                        $scope.totalEntity.subtotalMoney -= orderItems[j].totalFee;
                        $scope.totalEntity.subtotalNum -= orderItems[j].num;
                    }
                }
                if (!$scope.ckAll[i]) {
                    $scope.ckAll[i] = [];
                }
                $scope.ckAll[i] = $scope.checkedArr[i].length === orderItems.length;
            }
        }
        checkedFatherAll();
    };

    //判断按钮是否选中
    $scope.selectedOne = function (fatherCk, itemId) {
        if ($scope.checkedArr[fatherCk]) {
            for (let i = 0; i < $scope.checkedArr[fatherCk].length; i++) {
                if ($scope.checkedArr[fatherCk][i] === itemId) {
                    return true;
                }
            }
        }
        return false;
    };


});
