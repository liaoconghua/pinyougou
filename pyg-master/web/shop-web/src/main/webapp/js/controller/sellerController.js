/** 定义控制器层 */
app.controller('sellerController', function ($scope, $controller, baseService) {

    /** 指定继承baseController */
    $controller('baseController', {$scope: $scope});

    //定义修改商家密码对象
    $scope.sellerPwd = {password: '', newPwd: '', reNewPwd: ''};

    /** 查询条件对象 */
    $scope.searchEntity = {};
    /** 分页查询(查询条件) */
    $scope.search = function (page, rows) {
        baseService.findByPage("/seller/findByPage", page,
            rows, $scope.searchEntity)
            .then(function (response) {
                /** 获取分页查询结果 */
                $scope.dataList = response.data.rows;
                /** 更新分页总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
            });
    };

    /** 添加或修改 */
    $scope.saveOrUpdate = function () {

        /** 发送post请求 */
        baseService.sendPost("/seller/save", $scope.seller)
            .then(function (response) {
                if (response.data) {
                    /** 跳转到商家登录页面 */
                    location.href = "/shoplogin.html";
                } else {
                    alert("操作失败！");
                }
            });
    };

    /** 显示修改 */
    $scope.show = function (entity) {
        /** 把json对象转化成一个新的json对象 */
        $scope.entity = JSON.parse(JSON.stringify(entity));
    };

    /** 批量删除 */
    $scope.delete = function () {
        if ($scope.ids.length > 0) {
            baseService.deleteById("/seller/delete", $scope.ids)
                .then(function (response) {
                    if (response.data) {
                        /** 重新加载数据 */
                        $scope.reload();
                    } else {
                        alert("删除失败！");
                    }
                });
        } else {
            alert("请选择要删除的记录！");
        }
    };

    //修改sellerInfo数据回显
    $scope.showSellerInfo = function () {
        baseService.sendGet('/seller/showSellerInfo')
            .then(function (response) {
                $scope.sellerInfo = response.data;
            });
    };

    //修改sellerInfo
    $scope.updateSellerInfo = function () {
        baseService.sendPost('/seller/updateSellerInfo', $scope.sellerInfo)
            .then(function (response) {
                if (response.data) {
                    alert("修改成功！");
                } else {
                    alert("修改失败！");
                }
            });
    };

    //修改商家密码
    $scope.updateSellerPwd = function () {
        if ($scope.sellerPwd.password === '') {
            alert("原密码不能为空！");
            return;
        } else if ($scope.sellerPwd.newPwd !== $scope.sellerPwd.reNewPwd) {
            alert("两次输入的密码不一致！");
            return;
        } else if ($scope.sellerPwd.newPwd === '') {
            alert('新密码不能为空！');
            return;
        }
        baseService.sendPost('/seller/updateSellerPwd', $scope.sellerPwd)
            .then(function (response) {
                let resultMap = response.data;
                if (resultMap.updateRow !== undefined) {
                    alert("修改成功！");
                    location.href = '/logout';
                } else if (resultMap.PwdFault !== undefined) {
                    alert("原密码输入错误！");
                } else {
                    alert("修改失败！");
                }
            });
    };
});