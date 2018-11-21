/** 内容控制器层 */
app.controller('contentController', function ($scope, $controller, baseService) {
    //继承基础控制器层
    $controller('baseController', {$scope: $scope});
    //自定义广告状态数组
    $scope.status = ["无效", "有效"];

    /** 定义搜索对象 */
    $scope.searchEntity = {};

    /** 分页查询品牌信息 */
    $scope.search = function (page, rows) {
        /** 调用服务层分页查询数据 */
        baseService.findByPage("/content/findByPage", page,
            rows, $scope.searchEntity)
            .then(function (response) {
                $scope.dataList = response.data.dataList;
                /** 更新总记录数 */
                $scope.paginationConf.totalItems = response.data.totalItems;
            });
    };

    /**定义文件上传方法 */
    $scope.uploadFile = function () {
        baseService.uploadFile().then(function (response) {
            /** 如果上传成功，取出url */
            if (response.data.status === 200) {
                /** 设置图片访问地址 */
                $scope.entity.pic = response.data.url;
            } else {
                alert("上传失败！");
            }
        });
    };

    /** 加载广告分类数据 */
    $scope.findContentCategoryList = function () {
        baseService.sendGet("/contentCategory/findAll")
            .then(function (response) {
                $scope.contentCategoryList = response.data;
            });
    };

    /** 展示修改的内容 */
    $scope.show = function (entity) {
        $scope.entity = JSON.parse(JSON.stringify(entity));
        $scope.entity.status = $scope.entity.status === '1';
    };

    /** 保存内容 */
    $scope.saveOrUpdate = function () {
        let url = '/save';
        if ($scope.entity.id) {
            url = '/update';
        }
        $scope.entity.status = $scope.entity.status ? "1" : "0";
        baseService.sendPost('/content' + url, $scope.entity).then(function (response) {
            if (response.data) {
                $scope.reload();
            } else {
                alert("操作失败");
            }
        });
    };

    /** 批量删除品牌 */
    $scope.delete = function () {
        let ids = [];
        for (let idx in $scope.row) {
            ids.push(parseInt(idx));
        }
        if (ids.length > 0) {
            baseService.sendGet("/content/deleteByIds?ids=" +
                ids).then(function (respose) {
                if (respose.data) {
                    alert("删除成功");
                    $scope.reload();
                    $scope.row = {};
                } else {
                    alert("删除失败")
                }
            });
        } else {
            alert("未勾选任何数据");
        }
    };


});