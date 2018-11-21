/** 定义基础模块(不带分页模块) */
var app = angular.module('pinyougou',[]);
/** 配置位置提供者 */
app.config(function ($locationProvider) {
    $locationProvider.html5Mode(true);
});