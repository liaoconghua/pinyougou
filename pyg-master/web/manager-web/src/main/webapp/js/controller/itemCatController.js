/** 商品分类管理控制器 */
app.controller('itemCatController', function ($scope, $controller, baseService) {
    // 继承基础控制器
    $controller('baseController', {$scope: $scope});

    $scope.parentId = 0;
    /** 分页查询品牌信息 */
    $scope.search = function (pageNum, pageSize) {
        /** 发送异步请求分页查询品牌数据 */
        let url = '/itemCat/findByPage?pageNum=' + pageNum + '&pageSize=' +
            pageSize + '&parentId=' + $scope.parentId;
        baseService.sendGet(url, $scope.searchEntity)
            .then(function (respose) {
                $scope.dataList = respose.data.dataList;
                /** 更新总记录数 */
                $scope.paginationConf.totalItems = respose.data.totalItems;
            });
    };


    //根据父级id查询商品分类
    $scope.findItemCatByParentId = function (parentId) {
        baseService.sendGet('/itemCat/findItemCatByParentId?parentId=' + parentId)
            .then(function (respose) {
                $scope.dataList = respose.data;
                $scope.parentId = parentId;
                /** 更新总记录数 */
                $scope.paginationConf.totalItems = respose.data.length;
            });
    };

    /** 默认为1级 */
    $scope.grade = 1;
    /** 查询下级 */
    $scope.selectList = function (entity, grade) {
        $scope.grade = grade;
        if (grade === 1) {
            $scope.itemCat_1 = null;
            $scope.itemCat_2 = null;
        }
        if (grade === 2) {
            $scope.itemCat_1 = entity;
            $scope.itemCat_2 = null;
        }
        if (grade === 3) {
            $scope.itemCat_2 = entity;
        }

        /** 查询此级下级列表 */
        $scope.findItemCatByParentId(entity.id);
    };

    /** 查询所有类型模板 */
    $scope.findAllTypeTemplate = function () {
        baseService.sendGet('/typeTemplate/findAllTypeTemplate')
            .then(function (respose) {
                $scope.typeIdList = {data: respose.data};
            });
    };


    /** 保存模板 */
    $scope.saveOrUpdate = function () {
        $scope.saveOUpdate("/itemCat");
    };

    /** 显示修改 */
    $scope.show = function (entity) {
        $scope.updateShow(entity);
        // let dataList = $scope.typeIdList.data;
        // for (let i = 0; i < dataList.length; i++) {
        //     if (dataList[i].id === entity.typeId) {
        //         $scope.entity.typeId = dataList[i];
        //     }
        // }
    };

    /** 批量删除品牌 */
    $scope.delete = function () {
        $scope.deleteByIds("/itemCat");
    };


});