/** 定义基础控制器层 */
app.controller('baseController', function ($scope, baseService) {

    /** 定义多选数组 */
    $scope.checkedArr = [];

    /** 分页指令配置信息对象  */
    $scope.paginationConf = {
        currentPage: 1,//当前页数
        itemsPerPage: 10,//每页显示的数据
        totalItems: 0, // 总记录数
        perPageOptions: [1, 2, 5, 10, 15, 20, 30],
        onChange: function () {
            $scope.reload();
        }
    };

    /** 修改回显 */
    $scope.updateShow = function (entity) {
        $scope.entity = JSON.parse(JSON.stringify(entity));
    };

    /** 添加或修改品牌 */
    $scope.saveOUpdate = function (classify) {
        let url = "/save";
        if ($scope.entity.id) {
            url = "/update";
        }
        baseService.sendPost(classify + url, $scope.entity)
            .then(function (response) {
                if (response.data) {
                    $scope.reload();
                } else {
                    alert("操作失败！");
                }
            });
    };

    //获取需要删除的ids
    $scope.ids = [];
    $scope.updateSelection = function (id, index) {
        if ($scope.checkedArr[index]) {
            $scope.ids.push(id);
        } else {
            $scope.ids.splice($scope.ids.indexOf(id), 1);
        }
        $scope.ckAll = $scope.dataList.length === $scope.ids.length;
    };

    /** 全选功能 */
    $scope.checkedAll = function (selected) {
        let dataList = $scope.dataList;
        $scope.ids = [];
        for (let i = 0; i < dataList.length; i++) {
            $scope.checkedArr[i] = selected;
            if (selected) {
                $scope.ids.push(dataList[i].id);
            }
        }
    };

    $scope.findByPage = function (classify, pageNum, pageSize) {
        /** 发送异步请求分页查询品牌数据 */
        baseService.findByPage(classify + "/findByPage", pageNum, pageSize, $scope.searchEntity)
            .then(function (respose) {
                $scope.dataList = respose.data.dataList;
                /** 更新总记录数 */
                $scope.paginationConf.totalItems = respose.data.totalItems;
            });
    };

    /** 重新加载列表数据 */
    $scope.reload = function () {
        $scope.ids = [];
        $scope.checkedArr = [];
        $scope.ckAll = false;
        /** 切换页码 */
        $scope.search($scope.paginationConf.currentPage,
            $scope.paginationConf.itemsPerPage);
    };

    $scope.deleteByIds = function (classify) {
        if ($scope.ids.length > 0) {
            baseService.sendGet(classify + "/deleteByIds?ids=" + $scope.ids)
                .then(function (respose) {
                    if (respose.data) {
                        alert("删除成功!");
                        $scope.reload();
                    } else {
                        alert("删除失败!");
                    }
                });
        } else {
            alert("未选择要删除的数据!");
        }
    };

    /** 提取数组中json某个属性，返回拼接的字符串(逗号分隔) */
    $scope.jsonArr2Str = function (jsonArrStr, key) {
        // 把jsonArrStr转化成JSON数组对象
        let jsonArr = JSON.parse(jsonArrStr);
        let resArr = [];
        for (let i = 0; i < jsonArr.length; i++) {
            let json = jsonArr[i];
            resArr.push(json[key]);
        }
        return resArr.join('，');
    };

});