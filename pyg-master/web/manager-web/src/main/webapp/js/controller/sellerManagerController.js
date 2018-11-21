/** 商家管理控制器 */
app.controller('sellerManagerController', function ($scope, $controller, baseService) {
    /** 继承基础控制器 */
    $controller('baseController', {$scope: $scope});

    //定义商家审核状态数组
    $scope.checkStatusArr = ['待审核', '已审核', '审核未通过', '关闭'];

    /** 分页查询商家信息 */
    $scope.search = function (pageNum, pageSize) {
        /** 发送异步请求分页查询商家数据 */
        baseService.findByPage('/seller/findByPage', pageNum, pageSize, $scope.searchEntity)
            .then(function (respose) {
                $scope.dataList = respose.data.dataList;
                /** 更新总记录数 */
                $scope.paginationConf.totalItems = respose.data.totalItems;
            });
    };

    /** 显示修改 */
    $scope.show = function (entity) {
        /** 把json对象转化成一个新的json对象 */
        $scope.entity = JSON.parse(JSON.stringify(entity));
    };

});