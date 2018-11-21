/** 定义首页控制器层 */
app.controller("indexController", function ($scope, $controller, $location, baseService) {

    //继承基础控制层
    $controller('baseController', {$scope: $scope});

    /** 根据广告分类id查询广告内容 */
    $scope.findContentByCategoryId = function (categoryId) {
        baseService.sendGet("/content/findContentByCategoryId?categoryId="
            + categoryId).then(function (response) {
            $scope.contentList = response.data;
        });
    };

    /** 跳转到搜索系统 */
    $scope.search = function () {
        let keyword = $scope.keywords ? $scope.keywords : "";
        location.href = "http://search.pinyougou.com?keywords=" + keyword;
        // $location.absUrl("search.pinyougou.com").search({keywords: keyword});
    };

    //回车搜索
    $scope.onEnter = function (event) {
        if (event.keyCode === 13) {
            $scope.search();
        }
    }

});