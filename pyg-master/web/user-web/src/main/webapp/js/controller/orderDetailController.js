/** 定义控制器层 */
app.controller('orderDetailController', function ($scope, $controller, $location, baseService) {

    //继承订单列表控制层
    $controller('indexController', {$scope: $scope});

    //定义交易进度棒
    $scope.orderStatusBar = ['finished', 'current', 'todo'];

    //定义订单状态
    $scope.currOrderStatus = ['已提交', '已付款', '已付款', '已发货', '已确认','已关闭', '待评价', '已评价'];

    //定义支付类型数组
    $scope.payTypeArr = ['在线支付', '货到付款'];

    //定义交易状态数组
    $scope.transactionStatus = ['未付款', '已付款', '未发货', '已发货', '交易成功', '交易关闭', '待评价'];

    //获取地址栏上的参数
    $scope.getParam = function () {
        $scope.orderEntity = JSON.parse($location.search().orderEntity);
        // $scope.orderEntity.status = 5;
    };
});