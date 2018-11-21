/** 内容类型控制器层 */
app.controller('contentCategoryController', function ($scope, $controller, baseService) {
    //继承基础控制器层
    $controller('baseController', {$scope: $scope});

    /** 定义搜索对象 */
    $scope.searchEntity = {};

    /** 分页查询品牌信息 */
    $scope.search = function (page, rows) {
        $scope.findByPage("/contentCategory", page, rows);
    };

    /** 展示修改的内容 */
    $scope.show = function (entity) {
        $scope.entity = JSON.parse(JSON.stringify(entity));
    };

    /** 保存内容 */
    $scope.saveOrUpdate = function () {
        $scope.saveOUpdate("/contentCategory");
    };

    /** 批量删除内容分类 */
    $scope.delete = function () {
        $scope.deleteByIds("/contentCategory");
    };


});