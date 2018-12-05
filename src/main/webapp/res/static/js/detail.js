layui.use(['http','jquery','_util','layer','animations'],function () {
    let http = layui.http;
    let $ = layui.$;
    let _util = layui._util;
    let layer = layui.layer;
    let animations = layui.animations;

    let url = window.location.href;
    let id = url.substring(url.indexOf('?') + 1).split("=")[1];

    // 获取文章详情
    http.get({
        url: `/article/${id}`,
        success: function (data) {
            $('#article').html(`
            <div class="item-box  layer-photos-demo1 layer-photos-demo">
                <h3>${_util.html2Escape(data.title)}</h3>
                <h5><span><a href="user-info.html?userId=${data.authorId}">${_util.html2Escape(data.author)}</a></span> 发布于：<span>${_util.dateFormat(data.saveTime)}</span></h5>
                <p>${data.content}</p>
                <div class="count layui-clear">
                    <span class="pull-left">阅读 <em>${data.readNum}</em></span>
                    <span class="pull-right article-like like${data.star ? ' layblog-this' : ''}" articleId="${data.id}"><i class="layui-icon layui-icon-praise"></i><em>${data.starNum}</em></span>
                </div>
            </div>
            `);

            // 点赞文章
            $('.article-like').on('click',function () {
                animations.starArticle(this);
            });
        },
        error: function (status,err) {
            layer.msg(err,{time:2000,icon:5});
        }
    });

    let currentNum = 0;

    function getComments() {
        // 获取评论
        http.get({
            url: `/comments/${id}/${currentNum}/5`,
            success: function (data) {
                if (!data.length) {
                    let $btn = $('.addition');
                    $btn.text('已经到底部了');
                    $btn.attr('class','layui-btn layui-btn-disabled');
                    return;
                }
                currentNum += data.length;
                for (let c of data) {
                    $('#comments').append(`
                    <div class="info-item">
                        <img class="info-img" src="${c.photo}" alt="" height="60" width="60">
                        <div class="info-text">
                            <p class="title count">
                                <span><a href="user-info.html?userId=${c.authorId}">${_util.html2Escape(c.author)}</a> 发布于：${_util.dateFormat(c.saveTime)}</span>
                                <span class="info-img comment-like like${c.star ? ' layblog-this' : ''}" commentId="${c.id}"><i class="layui-icon layui-icon-praise"></i><em>${c.starNum}</em></span>
                            </p>
                            <p class="info-intr">${_util.html2Escape(c.content)}</p>
                        </div>
                    </div>
                `);
                }

                // 点赞评论
                $('.comment-like').on('click',function () {
                    animations.starComment(this);
                });
            },
            error: function (status,err) {
                layer.msg(err,{time:2000,icon:5});
            }
        });
    }

    getComments();

    // 加载更多
    $('.addition').on('click',function () {
        getComments();
    });

    // 点击写评论
    $('.comment-btn').on('click',function () {
        let user = layui.sessionData('user');
        if (Object.keys(user).length === 0) {
            // 用户未登录
            _util.showLoginPage();
            return;
        }
        $('.comment-box').toggle(500);
    });

    // 点击提交评论
    $('.edit-comment-btn').on('click',function () {
        let value = $('.comment-txt').val();
        if (!value) {
            layer.msg('评论不能为空',{time:2000,icon:6});
            return;
        }
        if (value.length > 200) {
            layer.msg('评论不能超过200字',{time:2000,icon:6});
            return;
        }
        http.post({
            url: '/comment',
            data: JSON.stringify({
                articleId: id,
                content: value
            }),
            success: function (data) {
                $('#comments').prepend(`
                    <div class="info-item">
                        <img class="info-img" src="${data.photo}" alt="" height="60" width="60">
                        <div class="info-text">
                            <p class="title count">
                                <span><a href="user-info.html?userId=${data.authorId}">${_util.html2Escape(data.author)}</a> 发布于：${_util.dateFormat(data.saveTime)}</span>
                                <span class="info-img comment-like like${data.star ? ' layblog-this' : ''}" commentId="${data.id}"><i class="layui-icon layui-icon-praise"></i><em>${data.starNum}</em></span>
                            </p>
                            <p class="info-intr">${_util.html2Escape(data.content)}</p>
                        </div>
                    </div>
                `);
                $('.comment-box').toggle(500);
                $('.comment-txt').val('');
                currentNum ++;

                // 点赞评论
                $('.comment-like').on('click',function () {
                    animations.starComment(this);
                });
            },
            error: function (status,err) {
                if (status === 403) {
                    _util.showLoginPage();
                    return;
                }
                layer.msg(err,{time:2000,icon:5});
            }
        })
    });
});