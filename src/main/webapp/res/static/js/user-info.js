layui.use(['jquery','_util','flow','http'],function (exports) {
    const $ = layui.jquery,
        _util = layui._util,
        flow = layui.flow,
        http = layui.http;

    let user = layui.sessionData('user');
    if (!user.user) {
        _util.showLoginPage();
    }

    let userData = user.user;

    $('.photo').attr('src',userData.photo);
    $('.username').text(userData.username);
    $('.info').html(userData.sex ? '<i class="layui-icon">&#xe662;</i>' : '<i class="layui-icon">&#xe661;</i>');

    let self = true;

    let url = window.location.href;
    let params = url.split('?')[1];
    if (params) {
        let userId = params.split('=')[1];
        if (userId !== user.user.id) {
            self = false;
            http.get({
                url: '/user/id/' + userId,
                success(data) {
                    userData = data;
                    $('.photo').attr('src',userData.photo);
                    $('.username').text(userData.username);
                    $('.info').html(userData.sex ? '<i class="layui-icon">&#xe662;</i>' : '<i class="layui-icon">&#xe661;</i>');
                },
                error(status,err) {
                    layer.msg(err,{time:2000,icon:6});
                }
            })
        }
    }

    flow.load({
        elem: '#articles' //流加载容器
        ,scrollElem: '#articles' //滚动条所在元素，一般不用填，此处只是演示需要。
        ,done: function(page, next){ //执行下一页的回调
            //模拟数据插入
            setTimeout(function(){
                let curNum = (page - 1) * 10;
                http.get({
                    url: `/user/articles/${userData.id}/${curNum}/10`,
                    success(data) {
                        let list = [];
                        data.map((item) => {
                            list.push(`
                            <li class="layui-timeline-item">
                                <i class="layui-icon layui-timeline-axis"></i>
                                <div class="layui-timeline-content layui-text">
                                    <h3 class="layui-timeline-title">${_util.dateFormatStr(item.saveTime)}</h3>
                                    <p>
                                        <i class="layui-icon">&#xe609;</i> ${userData.username} 发布 <a href="/details.html?id=${item.id}">${item.title}</a>                                        
                                    </p>
                                </div>
                            </li>
                            `);
                        });
                        next(list.join(''),data.length > 0);
                    },
                    error(status,err) {
                        layer.msg(err,{time:2000,icon:6});
                    }
                });
            }, 500);
        }
    });

    exports('user-info',{})
});