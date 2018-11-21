/** 商家审核控制器 */
app.controller('sellerController', function ($scope, $controller, baseService) {
    /** 继承基础控制器 */
    $controller('baseController', {$scope: $scope});

    /** 分页查询品牌信息 */
    $scope.search = function (pageNum, pageSize) {
        /** 发送异步请求分页查询品牌数据 */
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

    /** 修改商家状态 */
    $scope.updateStatus = function (sellerId, status) {
        baseService.sendGet("/seller/updateStatus?sellerId=" +
            sellerId + "&status=" + status).then(function (respose) {
            if (respose.data) {
                $scope.reload();
            } else {
                alert("操作失败！");
            }
        });
    };

});