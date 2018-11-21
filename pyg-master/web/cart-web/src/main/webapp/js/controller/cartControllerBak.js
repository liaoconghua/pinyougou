/** 定义购物车控制器 */
app.controller('cartController', function ($scope, $controller, baseService) {
    // 指定继承baseController
    $controller('baseController', {$scope: $scope});

    /** 查询购物车数据 */
    $scope.findCart = function () {
        baseService.sendGet("/cart/findCart")
            .then(function (response) {
                $scope.carts = response.data;
            });
    };

    /** 添加SKU商品到购物车 */
    $scope.addCart = function (itemId, num) {
        baseService.sendGet("/cart/addCart",
            "itemId=" + itemId + "&num=" + num)
            .then(function (response) {
                if (response.data) {
                    // 重新加载购物车数据
                    $scope.findCart();
                } else {
                    alert("操作失败！");
                }
            });
    };

    /** 查询购物车数据 */
    $scope.findCart = function () {
        baseService.sendGet("/cart/findCart")
            .then(function (response) {
                $scope.carts = response.data;
                /** 定义总计对象 */
                $scope.totalEntity = {totalNum: 0, totalMoney: 0.00};
                for (let i = 0; i < response.data.length; i++) {
                    // 获取购物车
                    let cart = response.data[i];
                    // 迭代购物车订单明细集合
                    for (let j = 0; j < cart.orderItems.length; j++) {
                        // 获取订单明细
                        let orderItem = cart.orderItems[j];
                        // 购买总件数
                        $scope.totalEntity.totalNum += orderItem.num;
                        // 购买总金额
                        $scope.totalEntity.totalMoney += orderItem.totalFee;
                    }
                }
            });
    };

    //定义选中总计数据模型
    $scope.selectedTotalEntity = {totalPrice: 0.00, totalNum: 0};
    /** 定义多选数组 */
    $scope.checkedArr = [];
    //获取需要删除的ids
    $scope.ids = [];
    //定义全选模型
    $scope.ckAll = [];
    $scope.updateSelection = function (id, fatherCk, index) {
        if (!$scope.ids[fatherCk]) {
            $scope.ids[fatherCk] = [];
        }
        if (!$scope.ckAll[fatherCk]) {
            $scope.ckAll[fatherCk] = [];
        }
        let orderItem = $scope.carts[fatherCk].orderItems[index];
        if ($scope.checkedArr[fatherCk][index]) {
            $scope.ids[fatherCk].push(id);
            if (orderItem.itemId === id) {
                $scope.selectedTotalEntity.totalPrice += orderItem.price * orderItem.num;
                $scope.selectedTotalEntity.totalNum += orderItem.num;
            }
        } else {
            $scope.ids[fatherCk].splice($scope.ids[fatherCk].indexOf(id), 1);
            if (orderItem.itemId === id) {
                $scope.selectedTotalEntity.totalPrice -= orderItem.price * orderItem.num;
                $scope.selectedTotalEntity.totalNum -= orderItem.num;
            }
        }
        $scope.ckAll[fatherCk] = $scope.carts[fatherCk].orderItems.length === $scope.ids[fatherCk].length;
        let cartsAllLength = 0;
        for (let i = 0; i < $scope.carts.length; i++) {
            cartsAllLength += $scope.carts[i].orderItems.length;
        }
        let idsAllLength = 0;
        for (let j = 0; j < $scope.ids.length; j++) {
            idsAllLength += $scope.ids[j].length;
        }
        $scope.ckFatherAll = cartsAllLength === idsAllLength;
    };

    /** 全选功能,将itemId push到ids中 */
    $scope.checkedAll = function (selected, fatherCk) {
        if (!$scope.checkedArr[fatherCk]) {
            $scope.checkedArr[fatherCk] = [];
        }
        if (!$scope.ckAll[fatherCk]) {
            $scope.ckAll[fatherCk] = [];
        }
        let carts = $scope.carts;
        $scope.ids[fatherCk] = [];
        for (let i = 0; i < carts[fatherCk].orderItems.length; i++) {
            let orderItem = $scope.carts[fatherCk].orderItems[i];
            $scope.checkedArr[fatherCk][i] = selected;
            if (selected) {
                $scope.ids[fatherCk].push(carts[fatherCk].orderItems[i].itemId);
                $scope.selectedTotalEntity.totalPrice += orderItem.price * orderItem.num;
                $scope.selectedTotalEntity.totalNum += orderItem.num;
            } else {
                $scope.selectedTotalEntity.totalPrice -= orderItem.price * orderItem.num;
                $scope.selectedTotalEntity.totalNum -= orderItem.num;
            }
        }
        let cartsAllLength = 0;
        for (let i = 0; i < $scope.carts.length; i++) {
            cartsAllLength += $scope.carts[i].orderItems.length;
        }
        let idsAllLength = 0;
        for (let j = 0; j < $scope.ids.length; j++) {
            idsAllLength += $scope.ids[j].length;
        }
        $scope.ckFatherAll = cartsAllLength === idsAllLength;
    };

    //全选
    $scope.checkedFatherAll = function (ckFatherAll) {
        if (!$scope.checkedArr) {
            $scope.checkedArr = [];
        }
        let carts = $scope.carts;
        $scope.ids = [];
        for (let i = 0; i < carts.length; i++) {
            if (!$scope.checkedArr[i]) {
                $scope.checkedArr[i] = [];
            }
            $scope.ids[i] = [];
            $scope.ckAll[i] = ckFatherAll;
            for (let j = 0; j < carts[i].orderItems.length; j++) {
                let orderItem = $scope.carts[i].orderItems[j];
                $scope.checkedArr[i][j] = ckFatherAll;
                if (ckFatherAll) {
                    $scope.ids[i].push(carts[i].orderItems[j].itemId);
                    $scope.selectedTotalEntity.totalPrice += orderItem.price * orderItem.num;
                    $scope.selectedTotalEntity.totalNum += orderItem.num;
                } else {
                    $scope.selectedTotalEntity.totalPrice -= orderItem.price * orderItem.num;
                    $scope.selectedTotalEntity.totalNum -= orderItem.num;
                }
            }
        }
    };


});
