/** 定义商品控制器层 */
app.controller('goodsController', function ($scope, $controller, baseService) {
    /** 指定继承baseController */
    $controller('baseController', {$scope: $scope});

    /** 定义商品状态数组 */
    $scope.status = ['未审核', '已审核', '审核未通过', '关闭'];
    /** 定义搜索对象 */
    $scope.searchEntity = {};

    /** 分页查询品牌信息 */
    $scope.search = function (page, rows) {
        $scope.findByPage("/goods", page, rows);
    };

    /** 审批商品，修改状态 */
    $scope.updateStatus = function (status) {
        if ($scope.ids.length > 0) {
            baseService.sendGet("/goods/updateStatus?ids=" +
                $scope.ids + "&status=" + status).then(function (respose) {
                if (respose.data) {
                    $scope.reload();
                } else {
                    alert("操作失败！")
                }
            });
        } else {
            alert("请先选择要审核的商品！")
        }
    };

    /** 批量删除商品(修改删除状态) */
    $scope.delete = function () {
        $scope.deleteByIds("/goods");
    };


});