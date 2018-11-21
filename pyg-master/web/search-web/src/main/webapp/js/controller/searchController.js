/** 定义搜索控制器 */
app.controller("searchController", function ($scope, $sce, $controller, $location, baseService) {

    //继承继承控制层
    $controller('baseController', {$scope: $scope});

    //定义跳转页码的数据变量
    $scope.jumpPage = 1;

    /** 定义搜索参数对象 */
    $scope.searchParam = {
        keywords: '', category: '',
        brand: '', price: '', spec: {},
        page: 1, rows: 20, sortField: '', sort: ''
    };

    /** 添加搜索选项方法 */
    $scope.addSearchItem = function (key, value) {
        //判断是商品分类、品牌、价格
        if (key === 'category' || key === 'brand' || key === 'price') {
            $scope.searchParam[key] = value;
        } else {//规格,增加键值
            $scope.searchParam.spec[key] = value;
        }
        //执行搜索
        $scope.search();
    };

    /** 删除搜索选项方法 */
    $scope.removeSearchItem = function (key) {
        //判断是商品分类、品牌、价格
        if (key === "category" || key === "brand" || key === 'price') {
            $scope.searchParam[key] = "";
        } else {//规格,减少键值
            delete $scope.searchParam.spec[key];
        }
        //执行搜索
        $scope.search();
    };

    /** 定义搜索方法 */
    $scope.search = function () {
        if ($scope.keyword) {
            $scope.searchParam.keywords = $scope.keyword;
        } else {
            $scope.keyword = $scope.searchParam.keywords;
        }
        baseService.sendPost("/Search", $scope.searchParam)
            .then(function (response) {
                /** 获取搜索结果 */
                $scope.resultMap = response.data;
                /** 调用初始化页码方法 */
                initPageNum();
            });
    };

    /** 初始化页码 */
    let initPageNum = function () {
        /** 定义页码数组 */
        $scope.pageNums = [];
        /** 获取总页数 */
        let totalPages = $scope.resultMap.totalPages;
        /** 开始页码 */
        let firstPage = 1;
        /** 结束页码 */
        let lastPage = totalPages;

        /** 如果总页数大于5，显示部分页码 */
        if (totalPages > 5) {
            // 如果当前页码处于前面位置
            if ($scope.searchParam.page <= 3) {
                lastPage = 5;
                $scope.firstDot = false;
                $scope.lastDot = true;
            } else if ($scope.searchParam.page > totalPages - 3) {
                lastPage = totalPages;
                firstPage = totalPages - 4;
                $scope.firstDot = true;
                $scope.lastDot = false;
            } else {//当前页码在中间位置
                lastPage = $scope.searchParam.page + 2;
                firstPage = $scope.searchParam.page - 2;
                $scope.firstDot = true;
                $scope.lastDot = true;
            }
        } else {
            $scope.firstDot = false; // 前面没点
            $scope.lastDot = false; // 后面没点
        }

        for (let i = firstPage; i <= lastPage; i++) {
            $scope.pageNums.push(i);
        }
    };

    //修改当前页码
    $scope.changePage = function (oldPage) {
        let curPage = parseInt(oldPage);//转换页数
        if (curPage >= 1 && curPage <= $scope.resultMap.totalPages) {
            if ($scope.searchParam.page !== curPage) {
                $scope.searchParam.page = curPage;
                $scope.jumpPage = $scope.searchParam.page;
                $scope.search();
            }
        }
    };

    //监控变量jumpPage
    $scope.$watch('jumpPage', function (newValue, oldValue) {
        let number = parseInt(newValue);
        if (!isNaN(number)) {//number是一个数字且不等于1
            if (number <= 1) {//不能小于1
                $scope.jumpPage = 1;
            } else if (number > $scope.resultMap.totalPages) {//不能大于总页数
                $scope.jumpPage = $scope.resultMap.totalPages;
            } else {
                $scope.jumpPage = number;
            }
        } else {
            $scope.jumpPage = parseInt(oldValue);
        }
    });

    /** 定义排序搜索方法 */
    $scope.sortSearch = function (sortField, sort) {
        $scope.searchParam.sortField = sortField;
        $scope.searchParam.sort = sort;
        $scope.search();
    };

    // 将文本转化成html
    $scope.trustHtml = function (html) {
        return $sce.trustAsHtml(html);
    };

    /** 获取检索关键字 */
    $scope.getkeywords = function () {
        $scope.searchParam.keywords = $location.search().keywords;
        $scope.search();
    };


});
