/** 定义控制器层 */
app.controller('goodsEditController', function ($scope, $controller, baseService, baseFactory) {

    /** 指定继承baseController */
    $controller('baseController', {$scope: $scope});


    $scope.persons = baseFactory.getter();
    console.log("persons=" + $scope.persons);



});