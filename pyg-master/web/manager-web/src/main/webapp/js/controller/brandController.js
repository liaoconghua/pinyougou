/** 添加控制器 */
app.controller("brandController", function ($scope, $controller) {
    /** 定义基础控制器层 */
    $controller('baseController', {$scope: $scope});

    /** 添加或修改品牌 */
    $scope.saveOrUpdate = function () {
        $scope.saveOUpdate("/brand");
    };

    /** 批量删除品牌 */
    $scope.delete = function () {
        $scope.deleteByIds("/brand");
    };

    /** 分页查询品牌信息 */
    $scope.search = function (pageNum, pageSize) {
        $scope.findByPage("/brand", pageNum, pageSize);
    };
});