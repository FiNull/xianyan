layui.define(['jquery','layer','_util','http'],function(exports){
    let $ = layui.jquery
        ,layer = layui.layer
        ,_util = layui._util
        ,http = layui.http;


    // start 导航显示隐藏
    $("#mobile-nav").on('click', function(){
      $("#pop-nav").toggle();
    });
    // end 导航显示隐藏

    // 添加jquery插件
    $.extend({
        tipsBox: function (options) {
            options = $.extend({
                obj: null,  //jq对象，要在那个html标签上显示
                str: "+1",  //字符串，要显示的内容;也可以传一段html，如: "<b style='font-family:Microsoft YaHei;'>+1</b>"
                startSize: "12px",  //动画开始的文字大小
                endSize: "30px",    //动画结束的文字大小
                interval: 600,  //动画时间间隔
                color: "red",    //文字颜色
                callback: function () { }    //回调函数
            }, options);

            $("body").append("<span class='num'>" + options.str + "</span>");

            var box = $(".num");
            var left = options.obj.offset().left + options.obj.width() / 2;
            var top = options.obj.offset().top - 10;
            box.css({
                "position": "absolute",
                "left": left + "px",
                "top": top + "px",
                "z-index": 9999,
                "font-size": options.startSize,
                "line-height": options.endSize,
                "color": options.color
            });
            box.animate({
                "font-size": options.endSize,
                "opacity": "0",
                "top": top - parseInt(options.endSize) + "px"
            }, options.interval, function () {
                box.remove();
                options.callback();
            });
        }
    });
    function niceIn(prop){
      prop.find('i').addClass('niceIn');
      setTimeout(function(){
        prop.find('i').removeClass('niceIn');
      },1000);
    }

    // start  图片遮罩
    var layerphotos = document.getElementsByClassName('layer-photos-demo');
    for(var i = 1;i <= layerphotos.length;i++){
      layer.photos({
        photos: ".layer-photos-demo"+i+""
        ,anim: 0
      });
    }
    // end 图片遮罩

    // 动画模块
    var animations = {
        updateStar($this) {
            if (!$this.hasClass('layblog-this')) {
                $this.text = '已赞';
                $this.addClass('layblog-this');
                $.tipsBox({
                    obj: $this,
                    str: "+1",
                    callback() {
                        var child = $this.children('em');
                        if (!child) {
                            child.text = parseInt(child.text) + 1;
                        }
                    }
                });
                niceIn($this);
                layer.msg('点赞成功', {
                    icon: 6
                    ,time: 1000
                })
            }
            else {
                $this.text = '点赞';
                $this.removeClass('layblog-this');
                $.tipsBox({
                    obj: $this,
                    str: "-1",
                    callback() {
                        var child = $this.children('em');
                        if (!child) {
                            child.text = parseInt(child.text) - 1;
                        }
                    }
                });
                niceIn($this);
                layer.msg('已取消', {
                    icon: 6
                    ,time: 1000
                })
            }
        },
        // 点赞文章
        starArticle(_this) {
            var __this = this;
            // 判断用户是否登录
            let user = layui.sessionData('user');
            if (!user.user) {
                _util.showLoginPage();
                return;
            }

            let $this = $(_this);
            let articleId = $this.attr('articleId');

            // 修改点赞状态
            http.put({
                url: '/article/' + articleId,
                success(data) {
                    __this.updateStar($this);
                },
                error(status,err) {
                    if (status === 406) {
                        _util.showLoginPage();
                        return;
                    }
                    layer.msg(err, {
                        icon: 5
                        ,time: 1000
                    })
                }
            });
        },
        // 点赞评论
        starComment(_this) {

            var __this = this;

            // 判断用户是否登录
            let user = layui.sessionData('user');
            if (!user.user) {
                _util.showLoginPage();
                return;
            }

            let $this = $(_this);
            let commentId = $this.attr('commentId');

            http.put({
                url: '/comment/star/' + commentId,
                success(data) {
                    __this.updateStar($this)
                },
                error(status,err) {
                    if (status === 406) {
                        _util.showLoginPage();
                        return;
                    }
                    layer.msg(err, {
                        icon: 5
                        ,time: 1000
                    })
                }
            })
        }
    };

    exports('animations', animations);
});  
