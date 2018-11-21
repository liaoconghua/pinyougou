/** 定义控制器层 */
app.controller('userSettingController', function ($scope, $controller, baseService) {

    //继承继承控制层
    $controller('baseController', {$scope: $scope});

    //定义用户地址数据模型
    // $scope.userEntity.address = {province: null, city: null, area: null};
    //定义职业数据模型
    $scope.jobList = [{id: "1", name: "程序员"}, {id: "2", name: "产品经理"}, {id: "3", name: "UI设计师"}];

    //定义地址数据模型
    $scope.addressEntity = {};


    //获取所有地址
    $scope.findAddressList = function () {
        baseService.sendGet('/userSetting/findAddressList')
            .then(function (response) {
                $scope.addressList = response.data;
            });
    };

    //获取省份
    $scope.findProvinceList = function () {
        baseService.sendGet('/userSetting/findProvinceList')
            .then(function (response) {
                $scope.provinceList = response.data;
            });
    };

    //获取城市
    $scope.findCityList = function (provinceId) {
        baseService.sendGet('/userSetting/findCityList' + "?provinceId=" + provinceId)
            .then(function (response) {
                $scope.cities = response.data;
            });
    };

    //获取区域
    $scope.findAreaList = function (provinceId) {
        baseService.sendGet('/userSetting/findAreaList' + "?cityId=" + provinceId)
            .then(function (response) {
                $scope.areas = response.data;
            });
    };

    //监听省份id
    $scope.$watch('userEntity.address.provinceId', function (newValue, oldValue) {
        if (newValue) {
            $scope.findCityList(newValue);
        }
    });

    //监听市级id
    $scope.$watch('userEntity.address.cityId', function (newValue, oldValue) {
        if (newValue) {
            $scope.findAreaList(newValue);
        }
    });

    //更新用户信息
    $scope.updateUserInfo = function () {
        baseService.sendPost('/userSetting/updateUserInfo', $scope.userEntity)
            .then(function (response) {
                if (response.data) {
                    alert("保存成功！");
                    $scope.headPic = $scope.userEntity.headPic;
                } else {
                    alert("保存失败！");
                }
            });
    };

    //更新头像
    $scope.updateHeadPic = function () {
        baseService.uploadFile().then(function (response) {
            /** 如果上传成功，取出url */
            if (response.data.status === 200) {
                /** 设置图片访问地址 */
                $scope.userEntity.headPic = response.data.url;
            } else {
                alert("上传失败！");
            }
        });
    };

    //监听省份id
    $scope.$watch('addressEntity.provinceId', function (newValue, oldValue) {
        if (newValue) {
            $scope.findCityList(newValue);
        }
    });

    //监听市级id
    $scope.$watch('addressEntity.cityId', function (newValue, oldValue) {
        if (newValue) {
            $scope.findAreaList(newValue);
        }
    });

    /** 根据登录用户获取地址列表 */
    $scope.findAddressByUser = function () {
        baseService.sendGet("/address/findAddressByUser")
            .then(function (response) {
                $scope.addressDetailsList = response.data;
                /** 循环用户地址集合 */
                for (let i = 0; i < $scope.addressDetailsList.length; i++) {
                    if ($scope.addressDetailsList[i].isDefault === "1") {
                        /** 设置默认地址 */
                        $scope.addressDefault = $scope.addressDetailsList[i];
                        break;
                    }
                }
            });
    };

    //截取地址
    function reduceAddress() {
//地址中有城镇
        if ($scope.addressEntity.townId) {//北京市 市辖区 东城区 bbb
            $scope.addressEntity.address = $scope.addressEntity.address.substr(
                $scope.addressEntity.address = $scope.addressEntity.address.indexOf(
                    ' ', $scope.addressEntity.address.indexOf(' ') - 1) + 1);
        }
        //地址中有城市
        if ($scope.addressEntity.cityId) {
            $scope.addressEntity.address = $scope.addressEntity.address.substr(
                $scope.addressEntity.address = $scope.addressEntity.address.indexOf(
                    ' ', $scope.addressEntity.address.indexOf(' ') - 1) + 1);
        }
        //地址中有省份
        if ($scope.addressEntity.provinceId) {
            $scope.addressEntity.address = $scope.addressEntity.address.substr(
                $scope.addressEntity.address = $scope.addressEntity.address.indexOf(
                    ' ', $scope.addressEntity.address.indexOf(' ') - 1) + 1);
        }
    }

    //回显用户购物地址
    $scope.showAddress = function (address) {
        $scope.addressEntity = JSON.parse(JSON.stringify(address));
        reduceAddress();
    };

    //生成地址，组合地址
    function generateAddress() {
        //区域id转区域名
        if ($scope.addressEntity.townId) {
            for (let i = 0; i < $scope.areas.length; i++) {
                if ($scope.areas[i].areaId === $scope.addressEntity.townId) {
                    $scope.addressEntity.address = $scope.areas[i].area + " " + $scope.addressEntity.address;
                }
            }
        }
        //城市id转城市名
        if ($scope.addressEntity.cityId) {
            for (let i = 0; i < $scope.cities.length; i++) {
                if ($scope.cities[i].cityId === $scope.addressEntity.cityId) {
                    $scope.addressEntity.address = $scope.cities[i].city + " " + $scope.addressEntity.address;
                }
            }
        }
        //省份id转省份名
        if ($scope.addressEntity.provinceId) {
            for (let i = 0; i < $scope.provinceList.length; i++) {
                if ($scope.provinceList[i].provinceId === $scope.addressEntity.provinceId) {
                    $scope.addressEntity.address = $scope.provinceList[i].province + " " + $scope.addressEntity.address;
                }
            }
        }
    }

//添加地址
    $scope.addOrUpdateAddress = function () {
        let url = "addAddress";
        if ($scope.addressEntity.id) {
            url = "updateAddress";
        }
        generateAddress();
        baseService.sendPost("/address/" + url, $scope.addressEntity)
            .then(function (response) {
                if (response.data) {
                    alert("操作成功！");
                    $scope.findAddressByUser();
                } else {
                    alert("操作失败！");
                }
            });
    };

    //修改默认地址
    $scope.setDefaultAddress = function (address) {
        let addressIdArr = [];
        addressIdArr.push(address.id);
        for (let i = 0; i < $scope.addressDetailsList.length; i++) {
            if ($scope.addressDetailsList[i].isDefault === "1") {
                addressIdArr.push($scope.addressDetailsList[i].id);
            }
        }
        baseService.sendGet('/address/setDefaultAddress' + '?ids=' + addressIdArr)
            .then(function (response) {
                if (response.status === 200) {
                    $scope.findAddressByUser();
                }
            });
    };

    //将省份id转成省份名
    // $scope.provinceIdToName = function (provinceId) {
    //     if (provinceId !== undefined) {
    //         // $scope.findProvinceList();
    //         if ($scope.provinceList !== undefined) {
    //             for (let i = 0; i < $scope.provinceList.length; i++) {
    //                 if ($scope.provinceList[i].provinceId === provinceId) {
    //                     return $scope.provinceList[i].province;
    //                 }
    //             }
    //         }
    //     }
    // };

    //将城市id转换成城市名
    // $scope.cityIdToName = function (provinceId, cityId) {
    //     if (provinceId !== undefined && cityId !== undefined) {
    //         baseService.sendGet('/userSetting/findCityList' + "?provinceId=" + provinceId)
    //             .then(function (response) {
    //                 $scope.cities = response.data;
    //                 for (let i = 0; i < $scope.cities.length; i++) {
    //                     if ($scope.cities[i].cityId === cityId) {
    //                         return $scope.cities[i].city;
    //                     }
    //                 }
    //             });
    //     }
    // };

    //删除地址
    $scope.deleteAddress = function (addressId) {
        baseService.sendGet('/address/deleteAddress' + "?id=" + addressId).then(function (response) {
            if (response.data) {
                alert("删除成功！");
                $scope.findAddressByUser();
            } else {
                alert("删除失败！");
            }
        });
    };

    //定义修改密码对象
    $scope.userPwd = {};
    //修改密码
    $scope.updatePwd = function () {
        if (!$scope.userPwd.password) {
            return;
        }
        if (!$scope.userPwd.rePassword) {
            return;
        }
        if ($scope.userPwd.password === $scope.userPwd.rePassword) {
            $scope.userPassword = {password: null};
            $scope.userPassword.password = $scope.userPwd.password;
            baseService.sendPost("/userSetting/updatePwd", $scope.userPassword)
                .then(function (response) {
                    if (response.data) {
                        alert("修改成功！");
                        $scope.userPwd = {};
                        // alert(JSON.stringify(location.href));
                        location.href = "http://sso.pinyougou.com/logout?service=" + $scope.redirectUrl;
                    } else {
                        alert("修改失败！");
                    }
                });
        }
    };

    //获得加密手机号
    $scope.getPhone = function () {
        let phoneStr = '';
        if ($scope.userEntity && $scope.userEntity) {
            phoneStr = '';
            let phone = $scope.userEntity.phone;
            // phone[3] = "*";
            // phone[4] = "*";
            // phone[5] = "*";
            // phone[6] = "*";
            phoneStr += phone[0];
            phoneStr += phone[1];
            phoneStr += phone[2];
            phoneStr += "****";
            phoneStr += phone[7];
            phoneStr += phone[8];
            phoneStr += phone[9];
            phoneStr += phone[10];
            // alert(phone[3]);
        }
        return phoneStr;
    };

    //发送短信
    $scope.sendCode = function () {
        let sendCodePhone = '';
        if ($scope.addPhone.newPhone !== '') {
            sendCodePhone = $scope.addPhone.newPhone;
        } else if ($scope.userEntity && $scope.userEntity.phone) {
            sendCodePhone = $scope.userEntity.phone;
        } else {
            alert("请输入手机号码！");
            return;
        }
        baseService.sendGet("/user/sendCode?phone="
            + sendCodePhone)
            .then(function (response) {
                alert(response.data ? "发送成功！" : "发送失败！");
            });
        // alert(JSON.stringify($scope.userEntity.phone));
        // baseService.sendGet('/user/sendCode' + "?phone=" + $scope.userEntity.phone)
        //     .then(function (response) {
        //         if (!response.data) {
        //             alert("发送失败！");
        //         }
        //     });
    };

    //定义用户验证对象
    $scope.userVerify = {verifyCode: '', smsCode: ''};
    //校验验证是否通过
    $scope.checkVerify = function (userVerify) {
        if ($scope.userVerify.verifyCode === '') {
            alert('请输入随机验证码！');
            return;
        }
        if ($scope.userEntity.phone === '') {
            alert("手机号不存在！");
            return;
        }
        if ($scope.userVerify.smsCode === '') {
            alert('请输入手机验证码！');
            return;
        }
        baseService.sendGet('/user/checkSmsCode' +
            "?verifyCode=" + $scope.userVerify.verifyCode +
            "&phone=" + $scope.userEntity.phone +
            "&smsCode=" + $scope.userVerify.smsCode).then(function (response) {
            if (response.data) {
                location.href = "home-setting-address-phone.html";
            } else {
                alert("校验失败！");
            }
        });
    };

    //定义添加手机号对象
    $scope.addPhone = {newPhone: '', verifyCode: '', smsCode: ''};
    //校验后更新用户手机号
    $scope.updatePhone = function () {
        $scope.addPhone.phone = $scope.userEntity.phone;
        if ($scope.addPhone.newPhone === '') {
            alert("请输入新手机号！");
            return;
        }
        if ($scope.addPhone.verifyCode === '') {
            alert("图片验证码必填！");
            return;
        }
        if ($scope.addPhone.smsCode === '') {
            alert("短信验证码必填！");
            return;
        }
        baseService.sendPost("/userSetting/updatePhone", $scope.addPhone).then(function (response) {
            if (response.data) {
                location.href = "home-setting-address-complete.html";
            } else {
                alert("校验失败了！");
            }
        }, function (error) {
            alert(JSON.stringify(error.data));
        });
    };


});

