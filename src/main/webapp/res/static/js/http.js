/**
 * 获取后端数据
 */
layui.define('jquery', function (exports) {
    var $ = layui.$;

    var _http = {
        host: 'http://localhost:8080',
        requestWapper: function (method, content) {
            this.request({
                uri: content.url,
                method: method,
                data: content.data,
                success: content.success,
                error: content.error
            })
        },
        request: function (header) {
            var _this = this;
            $.ajax({
                url: _this.host + header.uri,
                type: header.method || 'GET',
                cache: false,
                contentType: header.contentType || 'application/json',
                data: header.data,
                success: function (result) {
                    header.success(result)
                },
                error: function (xhr, status, error) {
                    if (xhr.status === 201) {
                        header.success(status)
                    }
                    else {
                        header.error(xhr.status, status)
                    }
                }
            })
        }
    };

    var http = {
        get(content) {
            _http.requestWapper('GET', content)
        },
        post(content) {
            _http.requestWapper('POST', content)
        },
        put(content) {
            _http.requestWapper('PUT', content)
        },
        delete(content) {
            _http.requestWapper('DELETE', content)
        },
        fileUpload(content) {
            _http.request({
                uri: content.url,
                method: 'POST',
                contentType: 'multipart/form-data',
                data: content.data,
                success: content.success,
                error: content.error
            })
        }
    };

    exports('http', http);
});

