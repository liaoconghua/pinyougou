/** 规格控制器 */
app.controller('specificationController', function ($scope, $controller, baseService) {
    // 继承基础控制器
    $controller('baseController', {$scope: $scope});

    /** 分页查询品牌信息 */
    $scope.search = function (pageNum, pageSize) {
        $scope.findByPage("/specification", pageNum, pageSize);
    };

    /** 新增规格选项行 */
    $scope.addTableRow = function () {
        $scope.entity.specificationOptions.push({});
    };

    /** 删除规格选项行 */
    $scope.deleteTableRow = function (index) {
        $scope.entity.specificationOptions.splice(index, 1);
    };

    /** 更新数据回显 */
    $scope.updateShow = function (entity) {
        let url = '/specification/findSpecification?id=' + entity.id;
        $scope.entity = {id: null, specName: '', specificationOptions: []};
        $scope.entity.id = entity.id;
        $scope.entity.specName = entity.specName;
        baseService.sendGet(url).then(function (respose) {
            $scope.entity.specificationOptions = respose.data;
        });
    };

    /** 保存新增规格 */
    $scope.saveOrUpdate = function () {
        $scope.saveOUpdate("/specification");
    };

    /** 删除规格 */
    $scope.delete = function () {
        $scope.deleteByIds("/specification");
    }

});
