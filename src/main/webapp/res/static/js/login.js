/**
 * 登录与注册
 */
layui.define(['jquery','_util'],function (exports) {

    const $ = layui.jquery,
        _util = layui._util;

    $('#user').click(function () {
        let user = layui.sessionData('user');
        if (!user.user) {
            _util.showLoginPage();
        }
        else {
            window.location.href = '/user-info.html?userId=' + user.user.id;
        }
    });

    exports('login',{});
});