/** 商品类型模板控制器 */
app.controller('typeTemplateController', function ($scope, $controller, baseService) {
    //继承基础控制器
    $controller('baseController', {$scope: $scope});

    /** 分页查询类型模板信息 */
    $scope.search = function (pageNum, pageSize) {
        $scope.findByPage("/typeTemplate", pageNum, pageSize);
    };

    /** 品牌列表 */
    $scope.findBrandList = function () {
        baseService.sendGet("/brand/findBrandList")
            .then(function (response) {
                $scope.brandList = {data: response.data};
            });
    };

    /** 规格列表 */
    $scope.findSpecList = function () {
        baseService.sendGet("/specification/findSpecList")
            .then(function (response) {
                $scope.specList = {data: response.data};
            });
    };

    /** 新增扩展属性行 */
    $scope.addTableRow = function () {
        $scope.entity.customAttributeItems.push({});
    };

    /** 删除扩展属性行 */
    $scope.deleteTableRow = function (index) {
        $scope.entity.customAttributeItems.splice(index, 1);
    };

    /** 保存模板 */
    $scope.saveOrUpdate = function () {
        $scope.saveOUpdate("/typeTemplate");
    };

    /** 显示修改 */
    $scope.show = function (entity) {
        $scope.entity = JSON.parse(JSON.stringify(entity));
        /** 转换品牌列表 */
        $scope.entity.brandIds = JSON.parse(entity.brandIds);
        /** 转换规格列表 */
        $scope.entity.specIds = JSON.parse(entity.specIds);
        /** 转换扩展属性 */
        $scope.entity.customAttributeItems = JSON
            .parse(entity.customAttributeItems);
    };

    /** 批量删除品牌 */
    $scope.delete = function () {
        $scope.deleteByIds("/typeTemplate");
    };

});