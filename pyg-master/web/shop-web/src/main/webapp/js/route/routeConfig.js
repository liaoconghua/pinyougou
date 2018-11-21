/** 配置路由 */
app.config(function ($stateProvider, $urlRouterProvider) {
    $urlRouterProvider
        .when("", "goodsList")
        .when("/", "goodsList");
    $stateProvider.state('goodsList', {//导航用的名字，如<a ui-sref="login">login</a>里的login
        url: '/goods',//访问路径
        templateUrl: '/admin/goods.html',
    }).state('goodsList.goods_edit', {
        url: '/goods_edit',
        templateUrl: '/admin/goods_edit.html',
    })
});