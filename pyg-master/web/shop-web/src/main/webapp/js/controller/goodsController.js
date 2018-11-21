/** 定义控制器层 */
app.controller('goodsController', function ($scope, $controller, $location, baseService) {

    /** 指定继承baseController */
    $controller('baseController', {$scope: $scope});

    /** 定义商品状态数组 */
    $scope.status = ['未审核', '已审核', '审核未通过', '关闭'];
    /** 定义商品上下架状态数组 */
    $scope.marketableStatus = ['已下架', '已上架'];
    //定义数据存储结构
    $scope.goods = {goodsDesc: {itemImages: [], specificationItems: []}};

    /** 查询条件对象 */
    $scope.searchEntity = {};
    /** 分页查询(查询条件) */
    $scope.search = function (page, rows) {
        $scope.findByPage("/goods", page, rows);
    };

    /** 保存商品 */
    $scope.saveOrUpdate = function () {
        /** 定义请求URL */
        let url = "save"; // 添加
        if ($scope.goods.id) {
            url = "update"; // 修改
        }
        /** 获取富文本编辑器的内容 */
        $scope.goods.goodsDesc.introduction = editor.html();
        // 发送异步请求
        baseService.sendPost("/goods/" + url, $scope.goods).then(
            function (response) {
                if (response.data) {
                    alert("保存成功！");
                    // 清空表单
                    $scope.goods = {goodsDesc: {introduction: null}};
                    /** 清空富文本编辑器 */
                    editor.html('');
                } else {
                    alert("保存失败！");
                }
            }
        );
    };

    /** 显示修改 */
    $scope.show = function (entity) {
        /** 把json对象转化成一个新的json对象 */
        $scope.entity = JSON.parse(JSON.stringify(entity));
    };

    /** 批量删除 */
    $scope.delete = function () {
        $scope.deleteByIds("/goods");
    };

    /**上传图片 */
    $scope.uploadFile = function () {
        baseService.uploadFile().then(function (response) {
            //如果上传成功，取出url
            if (response.data.status === 200) {
                //设置图片的访问地址
                $scope.picEntity.url = response.data.url;
            } else {
                alert("上传失败！");
            }
        });
    };

    /** 添加图片到数组 */
    $scope.addPic = function () {
        $scope.goods.goodsDesc.itemImages.push($scope.picEntity);
    };

    /** 数组中移除图片 */
    $scope.removePic = function (index) {
        $scope.goods.goodsDesc.itemImages.splice(index, 1);
    };

    /** 根据父级ID查询分类 */
    $scope.findItemCatByParentId = function (parentId, name) {
        baseService.sendGet("/itemCat/findItemCatByParentId",
            "parentId=" + parentId).then(function (response) {
            $scope[name] = response.data;
        });
    };

    /** 监控 goods.category1Id 变量，查询二级分类 */
    $scope.$watch('goods.category1Id', function (newValue, oldValue) {
        if (newValue) {
            /** 根据选择的值查询二级分类 */
            $scope.findItemCatByParentId(newValue, "itemCatList2");
        } else {
            $scope.itemCatList2 = [];
        }
    });

    /** 监控 goods.category2Id 变量，查询三级分类 */
    $scope.$watch('goods.category2Id', function (newValue, oldValue) {
        if (newValue) {
            /** 根据选择的值查询三级分类 */
            $scope.findItemCatByParentId(newValue, "itemCatList3");
        } else {
            $scope.itemCatList3 = [];
        }
    });

    // $watch():用于监听goods.category3Id变量是否发生改变
    $scope.$watch('goods.category3Id', function (newValue, oldValue) {
        if (newValue) {
            // 循环三级分类数组 List<ItemCat> : [{},{}]
            for (let i = 0; i < $scope.itemCatList3.length; i++) {
                // 取一个数组元素 {}
                let itemCat = $scope.itemCatList3[i];
                // 判断id
                if (itemCat.id === newValue) {
                    $scope.goods.typeTemplateId = itemCat.typeId;
                    break;
                }
            }
        } else {
            $scope.goods.typeTemplateId = null;
        }
    });

    /** 监控 goods.typeTemplateId 模板ID */
    $scope.$watch('goods.typeTemplateId', function (newValue, oldValue) {
        if (!newValue) {
            $scope.brandIds = null;
            $scope.goods.goodsDesc.customAttributeItems = null;
            $scope.specList = null;
            return;
        }
        //查询该模版对应的品牌
        baseService.findOne("/typeTemplate/findOne", newValue)
            .then(function (response) {
                /** 获取模版中的品牌列表 */
                $scope.brandIds = JSON.parse(response.data.brandIds);
                /** 如果没有id，则加载模板中的扩展数据 */
                if (!$location.search().id) {
                    /** 设置扩展属性 */
                    $scope.goods.goodsDesc.customAttributeItems =
                        JSON.parse(response.data.customAttributeItems);
                }
            });

        /** 查询该模版对应的规格与规格选项 */
        baseService.findOne("/typeTemplate/findSpecByTemplateId",
            newValue).then(function (respose) {
            $scope.specList = respose.data;
        })
    });

    /** 定义修改规格选项方法 */
    $scope.updateSpecAttr = function ($event, name, value) {
        /** 根据json对象的key到json数组中搜索该key值对应的对象 */
        var obj = $scope.searchJsonByKey($scope.goods.goodsDesc
            .specificationItems, 'attributeName', name);
        /** 判断对象是否为空 */
        if (obj) {
            /** 判断checkbox是否选中 */
            if ($event.target.checked) {
                /** 添加该规格选项到数组中 */
                obj.attributeValue.push(value);
            } else {
                /** 取消勾选，从数组中删除该规格选项 */
                obj.attributeValue.splice(obj.attributeValue
                    .indexOf(value), 1);
                /** 如果选项都取消了，将此条记录删除 */
                if (obj.attributeValue.length === 0) {
                    $scope.goods.goodsDesc.specificationItems.splice(
                        $scope.goods.goodsDesc.specificationItems.indexOf(obj), 1);
                }
            }
        } else {
            /** 如果为空，则新增数组元素 */
            $scope.goods.goodsDesc.specificationItems.push(
                {"attributeName": name, "attributeValue": [value]});
        }
    };

    /** 创建SKU商品方法 */
    $scope.createItems = function () {
        /** 定义SKU数组，并初始化 */
        $scope.goods.items = [{
            spec: {}, price: 0, num: 9999,
            status: '0', isDefault: '0'
        }];
        /** 定义选中的规格选项数组 */
        let specItems = $scope.goods.goodsDesc.specificationItems;
        if (specItems.length === 0) {
            $scope.goods.items = [];
        }
        for (var i = 0; i < specItems.length; i++) {
            /** 扩充原SKU数组方法 */
            $scope.goods.items = swapItems($scope.goods.items,
                specItems[i].attributeName,
                specItems[i].attributeValue);
        }
    };

    /** 扩充SKU数组方法 */
    var swapItems = function (items, attributeName, attributeValue) {
        /** 创建新的SKU数组 */
        let newItems = [];
        /** 迭代旧的SKU数组，循环扩充 */
        for (let i = 0; i < items.length; i++) {
            // 迭代规格选项值数组
            for (let j = 0; j < attributeValue.length; j++) {
                // 克隆旧的SKU商品，产生新的SKU商品
                let newItem = JSON.parse(JSON.stringify(items[i]));
                // 增加新的key与value
                newItem.spec[attributeName] = attributeValue[j];
                // 添加到新的SKU数组
                newItems.push(newItem);
            }
        }
        return newItems;
    };

    /** 商家商品上下架(修改可销售状态) */
    $scope.updateMarketable = function (status) {
        if ($scope.ids.length > 0) {
            let goodsList = $scope.dataList;
            for (let i = 0; i < goodsList.length; i++) {
                if ($scope.ids.indexOf(goodsList[i].id) === 0) {
                    if (goodsList[i].auditStatus !== "1") {
                        alert("有商品未审核通过，请等待审核通过后再操作！");
                        return;
                    }
                }
            }
            baseService.sendGet("/goods/updateMarketable", "ids=" +
                $scope.ids + "&status=" + status)
                .then(function (response) {
                    if (response.data) {
                        alert("操作成功！");
                        $scope.reload();
                        $scope.row = {};
                    } else {
                        alert("操作失败！");
                    }
                });
        } else {
            alert("请选择要操作的商品！");
        }
    };

    /** 将entity对象传递给goods_edit.html页面 */
    $scope.goodsEdit = function (entity) {
        baseService.sendPost('/admin/goods_edit.html', entity);
    };

    /** 跳转到编辑商品的页面 */
    $scope.editGoods = function (id) {
        location.href = '/admin/goods_edit.html?id=' + id;
    };

    $scope.getParam = function () {
        let id = $location.search().id;
        if (!id) {
            return;
        }
        baseService.sendGet("/goods/findGoodsById?id=" + id)
            .then(function (response) {
                $scope.goods = response.data;
                /** 富文本编辑器添加商品介绍 */
                editor.html($scope.goods.goodsDesc.introduction);
                /** 把图片json字符串转化成图片数组 */
                $scope.goods.goodsDesc.itemImages =
                    JSON.parse($scope.goods.goodsDesc.itemImages);
                /** 把扩展属性json字符串转化成数组 */
                $scope.goods.goodsDesc.customAttributeItems =
                    JSON.parse($scope.goods.goodsDesc.customAttributeItems);
                /** 把规格json字符串转化成数组 */
                $scope.goods.goodsDesc.specificationItems =
                    JSON.parse($scope.goods.goodsDesc.specificationItems);
                /** SKU列表规格json字符串转换对象 */
                for (let i = 0; i < $scope.goods.items.length; i++) {
                    $scope.goods.items[i].spec =
                        JSON.parse($scope.goods.items[i].spec);
                }
            });
    };

    /** 根据规格名称和选项名称返回是否被勾选 */
    $scope.checkAttributeValue = function (specName, optionName) {
        //定义规格选项数组
        var specItems = $scope.goods.goodsDesc.specificationItems;
        //搜索规格选项对象
        var obj = $scope.searchJsonByKey(specItems, 'attributeName', specName);
        if (obj) {
            return obj.attributeValue.indexOf(optionName) >= 0;
        }
        return false;
    }
});