layui.use(['layedit', '_util', 'http', 'jquery', 'layer'], function () {
    let layedit = layui.layedit;
    let _util = layui._util;
    let http = layui.http;
    let $ = layui.$;
    let layer = layui.layer;

    // 设置图片接口
    layedit.set({
        uploadImage: {
            url: 'http://localhost:8080/upload' //接口url
        }
    });
    //建立编辑器
    let index = layedit.build('article', {
        height: 400
    });

    let user = layui.sessionData('user');
    if (!user.user) {
        _util.showLoginPage();
        return;
    }

    $('.save').on('click', function () {
        var title = $('#title').val();
        var content = layedit.getContent(index);
        var text = layedit.getText(index);

        if (!title) {
            layer.msg('标题不能为空', {time: 2000, icon: 6});
            return;
        }
        if (title.length > 20) {
            layer.msg('标题不能超过20字', {time: 2000, icon: 6});
            return;
        }

        if (!content) {
            layer.msg('正文不能为空', {time: 2000, icon: 6});
            return;
        }
        if (content.length > 10000) {
            layer.msg('正文不能超过10000字符', {time: 2000, icon: 6});
            return;
        }

        var mainPic = $('<body>' + content + '</body>').find('img').attr('alt');
        if (mainPic && mainPic.startsWith('[') && mainPic.endsWith(']')) {
            mainPic = '';
        }
        http.post({
            url: '/article',
            data: JSON.stringify({
                title: title,
                content: content,
                text: text,
                mainPic: mainPic || 'item.png'
            }),
            success: function (data) {
                window.location.href = "index.html";
            },
            error: function (status, err) {
                if (status === 403) {
                    _util.showLoginPage();
                    return;
                }
                layer.msg(err, {time: 2000, icon: 5});
            }
        })
    });
});