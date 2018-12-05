
layui.use(['http','jquery','_util','layer','animations'],function () {
    let http = layui.http;
    let $ = layui.$;
    let _util = layui._util;
    let layer = layui.layer;
    let animations = layui.animations;

    // 当前页面数据个数
    let currentNum = 0;

    function getArticles() {
        http.get({
            url: `/articles/${currentNum}/5`,
            success: function (data) {
                if (!data.length) {
                    let $btn = $('.addition');
                    $btn.text('已经到底部了');
                    $btn.attr('class','layui-btn layui-btn-disabled');
                    return;
                }
                currentNum += data.length;
                for (let a of data) {
                    $('#articles').append(`
                    <div class="item">
                        <div class="item-box  layer-photos-demo1 layer-photos-demo">
                            <h3><a href="details.html?id=${a.id}">${_util.html2Escape(a.title)}</a></h3>
                            <h5>来自：<span><a href="user-info.html?userId=${a.authorId}">${_util.html2Escape(a.author)}</a></span></h5>
                            <p>${a.text.substring(0, 200)}</p>
                            <img src="${a.mainPic}" width="200" height="130">
                        </div>
                        <div class="comment count">
                            <a>发布于：${_util.dateFormat(a.saveTime)}</a>
                            <a href="javascript:;" class="like${a.star ? ' layblog-this' : ''}" articleId="${a.id}">${a.star ? '已赞' : '点赞'}</a>
                        </div>
                    </div>
                `);
                }
                // 点赞文章
                $('.like').on('click',function () {
                    animations.starArticle(this);
                });
            },
            error: function (status, err) {
                layer.msg(err,{time:2000,icon:5});
            }
        });
    }

    getArticles();

    $('.addition').on('click',function () {
        getArticles();
    });
});