layui.define(['jquery','form','http'],function (exports) {

    let $ = layui.$,
        form = layui.form,
        http = layui.http;

    let template = `
        <div id="tmp" style="width: 85%;margin-top: 50px">
            <div id="register" style="display: none;">
                <form class="layui-form">
                    <fieldset class="layui-elem-field layui-field-title" style="margin-left: 80px;padding-left: 170px">
                        <legend>注册</legend>
                    </fieldset>
                    <div class="layui-form-item">
                        <label class="layui-form-label">
                            <i class="layui-icon layui-icon-username"></i>
                        </label>
                        <div class="layui-input-block">
                            <input type="text" name="username" required lay-verify="required" placeholder="请输入用户名" autocomplete="off" class="layui-input">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">
                            <i class="layui-icon layui-icon-cellphone"></i>
                        </label>
                        <div class="layui-input-block">
                            <input type="text" name="phone" required lay-verify="phone" placeholder="请输入手机号" autocomplete="off" class="layui-input">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">
                            <i class="layui-icon layui-icon-password"></i>
                        </label>
                        <div class="layui-input-block">
                            <input type="password" name="password" required lay-verify="required" placeholder="请输入密码" autocomplete="off" class="layui-input">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">
                            <i class="layui-icon layui-icon-password"></i>
                        </label>
                        <div class="layui-input-block">
                            <input type="password" name="repassword" required lay-verify="required" placeholder="请确认密码" autocomplete="off" class="layui-input">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <div class="layui-input-block">
                            <button class="layui-btn" lay-submit="" lay-filter="register">注册</button> 
                            <button class="layui-btn layui-btn-primary show-login">登录</button>
                        </div>
                    </div>
                </form>
            </div>
            <div id="login">
                <form class="layui-form">
                    <fieldset class="layui-elem-field layui-field-title" style="margin-left: 80px;padding-left: 170px">
                        <legend>登录</legend>
                    </fieldset>
                    <div class="layui-form-item">
                        <label class="layui-form-label">
                            <i class="layui-icon layui-icon-username"></i>
                        </label>
                        <div class="layui-input-block">
                            <input type="text" name="username" required lay-verify="required" placeholder="请输入用户名" autocomplete="off" class="layui-input">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">
                            <i class="layui-icon layui-icon-password"></i>
                        </label>
                        <div class="layui-input-block">
                            <input type="password" name="password" required lay-verify="required" placeholder="请输入密码" autocomplete="off" class="layui-input">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <div class="layui-input-block">
                            <button class="layui-btn" lay-submit="" lay-filter="login">登录</button>
                            <button class="layui-btn layui-btn-primary show-register">注册</button> 
                        </div>
                    </div>
                </form>
            </div>
        </div>
    `;

    let _util = {
        dateFormat(val) {
            let date = new Date(val);
            let year = date.getFullYear(),
                month = date.getMonth() + 1,//月份是从0开始的
                day = date.getDate(),
                hour = date.getHours(),
                min = date.getMinutes(),
                sec = date.getSeconds();
            return `${year}-${month < 10 ? '0' + month : month}-${day < 10 ? '0' + day : day} ${hour < 10 ? '0'+hour : hour}:${min < 10 ? '0'+min : min}:${sec < 10 ? '0'+sec : sec}`;
        },
        html2Escape(sHtml) {
            return sHtml.replace(/[<>&"]/g, function (c) {
                return {'<': '&lt;', '>': '&gt;', '&': '&amp;', '"': '&quot;'}[c];
            });
        },
        showLoginPage() {
            layer.open({
                type: 1,
                title: '登录/注册',
                skin: 'layui-layer-rim',
                area: ['600px', '420px'],
                content: template
            });
            $('.show-login').on('click',function () {
                $('#register').hide(300);
                $('#login').show(300);
            });
            $('.show-register').on('click',function () {
                $('#login').hide(300);
                $('#register').show(300);
            });
            // 登录
            form.on('submit(login)',function (data) {
                http.post({
                    url: '/user/' + data.field.username,
                    data: JSON.stringify(data.field),
                    success: function (data) {
                        // 存储用户信息
                        layui.sessionData('user',{
                            key: 'user',
                            value: data
                        });
                        window.location.reload(true);
                    },
                    error: function (status,err) {
                        if (status === 406)
                            layer.msg('用户名或密码不正确！',{time:1000,icon:5});
                        else
                            layer.msg(err,{time:1000,icon:5});
                    }
                });
                return false;
            });
            // 注册
            form.on('submit(register)',function (data) {

                let userInfo = data.field;

                if (userInfo.repassword != userInfo.password) {
                    layer.msg('前后密码不一致',{time:1000,icon:5});
                    return false;
                }

                // 检查用户名是否已被注册
                http.get({
                    url: '/user/' + userInfo.username,
                    success: function (data) {
                        // 注册用户
                        http.post({
                            url: '/user',
                            data: JSON.stringify(userInfo),
                            success: function (data) {
                                // 存储用户信息
                                layui.sessionData('user',{
                                    key: 'user',
                                    value: data
                                });
                                window.location.reload(true);
                            },
                            error: function (status,err) {
                                layer.msg('注册失败',{time: 1000,icon:5});
                            }
                        });
                    },
                    error: function (status,err) {
                        layer.msg('注册失败，用户名已存在',{time: 1000,icon:5});
                    }
                });
                return false;
            });
        }
    };

    exports('_util',_util);
});