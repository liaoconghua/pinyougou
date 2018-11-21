/** 品牌服务层 */
app.service("baseService", function ($http) {
});
app.controller("brandController", function ($scope, $http) {
    /** 读取列表数据绑定到表格中 */
    $scope.findAll = function () {
        /** 发送异步请求查询数据 */
        $http.get("/brand/findAll").then(function (response) {
            $scope.dataList = response.data;
        });
    };

    /** 添加或修改品牌 */
    $scope.saveOrUpdate = function () {
        var url = "/save";
        if ($scope.brand.id) {
            url = "/update";
        }
        $http.post("/brand" + url, $scope.brand).then(function (value) {
            if (value.data) {
                $scope.reload();
            } else {
                alert("操作失败！")
            }
        });
    };

    /** 修改回显 */
    $scope.updateShow = function (brand) {
        $scope.brand = JSON.parse(JSON.stringify(brand));
    };

    /** 删除品牌 */
    //获取ids
    // $scope.ids = [];
    $scope.row = {};
    $scope.updateSelection = function (id, checked) {
        if (checked) {
            // $scope.ids.push(id);
            $scope.row[id].id = id;
        } else {
            // $scope.ids.splice($scope.ids.indexOf(id), 1);
            delete $scope.row[id];
        }
    };
    //批量删除
    $scope.delete = function () {
        let ids = [];
        for (let idx in $scope.row) {
            ids.push(parseInt(idx));
        }
        if (ids === []) {
            alert("请选择要删除的品牌");
        } else {
            $http.get("/brand/deleteByIds?ids=" + ids).then(function (respose) {
                // $http.get("/brand/deleteByIds", {params: ids}).then(function (respose) {
                if (respose.data) {
                    alert("删除成功");
                    $scope.reload();
                } else {
                    alert("删除失败")
                }
            });
        }
    };

    /** 分页指令配置信息对象  */
    $scope.paginationConf = {
        currentPage: 1,//当前页数
        itemsPerPage: 10,//每页显示的数据
        totalItems: 0, // 总记录数
        // perPageOptions: [10, 20, 30],
        onChange: function () {
            $scope.reload();
        }
    };
    /** 重新加载列表数据 */
    $scope.reload = function () {
        /** 切换页码 */
        $scope.search($scope.paginationConf.currentPage,
            $scope.paginationConf.itemsPerPage);
    };
    /** 分页查询品牌信息 */
    $scope.search = function (pageNum, pageSize) {
        /** 发送异步请求分页查询品牌数据 */
        $http.get('/brand/findByPage?pageNum=' + pageNum + '&pageSize=' + pageSize, {params: $scope.searchEntity})
            .then(function (respose) {
                $scope.dataList = respose.data.brandList;
                /** 更新总记录数 */
                $scope.paginationConf.totalItems = respose.data.totalItems;
            });
    };
});
