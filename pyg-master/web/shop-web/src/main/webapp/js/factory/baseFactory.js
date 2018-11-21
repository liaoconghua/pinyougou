app.factory('baseFactory', function () {
    var testObject = [];
    var _setter = function (data) {
        testObject.push(data);
    };
    var _getter = function () {
        return testObject;
    };
    return {
        setter: _setter,
        getter: _getter
    }
});