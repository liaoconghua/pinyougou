/** 商品详情控制层 */
app.controller('itemController', function ($scope, $controller, $http) {
    /** 指定继承baseController */
    $controller("baseController", {$scope: $scope});

    //定义用户选择的规格选项
    $scope.specItems = {};

    //定义购买数量操作方法
    $scope.changeNum = function (num) {
        $scope.num += num;
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    };

    //监控变量购买数量num
    $scope.$watch('num', function (newValue, oldValue) {
        let number = parseInt(newValue);
        if (!isNaN(number)) {//number是一个数字
            if (number < 1) {
                $scope.num = 1;
            } else {
                $scope.num = number;
            }
        } else {
            $scope.num = parseInt(oldValue);
        }
    });

    /** 根据用户选中的规格选项，查找对应的SKU商品 */
    var searchSku = function () {
        for (let i = 0; i < itemList.length; i++) {
            /** 判断规格选项是不是当前用户选中的 */
            if (itemList[i].spec === JSON.stringify($scope.specItems)) {
                $scope.sku = itemList[i];
                return;
            }
        }
    };

    //添加用户选择的规格选项
    $scope.selectSpec = function (name, value) {
        $scope.specItems[name] = value;
        /** 查找对应的SKU商品 */
        searchSku();
    };

    //判断某个规格选项是否被选中
    $scope.isSelected = function (name, value) {
        return $scope.specItems[name] === value;
    };

    /** 加载默认的SKU */
    $scope.loadSku = function () {
        /** 取第一个SKU */
        $scope.sku = itemList[0];
        /** 获取SKU商品选择的选项规格 */
        $scope.specItems = JSON.parse($scope.sku.spec);
    };

    /** 添加SKU商品到购物车 */
    $scope.addToCart = function () {
        $http.get("http://cart.pinyougou.com/cart/addCart?itemId="
            + $scope.sku.id + "&num=" + $scope.num, {"withCredentials": true})
            .then(function (response) {
                if (response.data) {
                    /** 跳转到购物车页面 */
                    location.href = 'http://cart.pinyougou.com/cart.html';
                } else {
                    alert("请求失败！");
                }
            });
    };


});