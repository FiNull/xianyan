/**
 * 登录与注册
 */
layui.define(['jquery', '_util'], function (exports) {

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

    function request(keyword) {
        window.location.href = 'index.html?keyword=' + keyword;
    }

    // 点击搜索按钮
    $('.search-icon').click(function () {
        let keyword = $(this).next().val().trim();
        if (keyword) {
            request(keyword);
        }
    });
    // 搜索框回车
    $('.search-input').keyup(function (event) {
        let keyword = $('.search-input').val().trim();
        // 回车
        if (event.keyCode === 13 && keyword) {
            request(keyword);
        }
    });

    exports('header', {});
});