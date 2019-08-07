package cn.finull.framework.core;

import cn.finull.framework.config.AppConfig;
import cn.finull.framework.core.request.Parameter;
import cn.finull.framework.core.request.Router;
import cn.finull.framework.core.request.URI;
import cn.finull.framework.core.response.ResponseEntity;
import cn.finull.framework.except.MethodNotAllowedException;
import cn.finull.framework.json.JSON;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.function.Function;

/**
 * @author FiNull
 * 中央处理器
 */
@WebFilter(filterName = "DispatchFilter", value = "/*")
public class ZDispatchServlet implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 封装参数
        Parameter parameter = new Parameter(request, response);
        URI uri = new URI(request.getMethod(), request.getRequestURI());
        // 获得处理器
        Function handler = Router.getInstance().handler(uri);
        if (handler == null) {
            if (Router.getInstance().matchPath(request.getRequestURI())) {
                throw new MethodNotAllowedException("Method Not Allowed: [" + request.getMethod() + ":" + request.getRequestURI() + "]");
            }
            // 不通过框架处理该请求
            filterChain.doFilter(request, response);
            return;
        }
        // 获得restful uri 参数和值
        parameter.body().addParams(uri.getParams());
        // 执行处理器，并获得返回值
        Object respBody = handler.apply(parameter);

        Writer writer = response.getWriter();
        if (respBody == null) {
            // 返回空指针
            response.setContentType(Media.APPLICATION_JSON_UTF_8);
            writer.write("null");
            writer.flush();
        } else if (respBody instanceof String) {
            String url = (String) respBody;
            if (url.startsWith("redirect:")) {
                // 请求重定向
                response.sendRedirect(url.substring(url.indexOf(":") + 1));
            } else if (url.startsWith("forward:")) {
                // 请求转发
                request.getRequestDispatcher(url.substring(url.indexOf(":") + 1)).forward(request, response);
            } else {
                // 转发至模板
                request.getRequestDispatcher(AppConfig.getViewPrefix() + url + AppConfig.getViewSuffix()).forward(request, response);
            }
        } else {
            // 以json形式返回
            response.setContentType(Media.APPLICATION_JSON_UTF_8);

            Object respData = respBody;
            if (respBody instanceof ResponseEntity) {
                response.setStatus(((ResponseEntity) respBody).getStatus());
                respData = ((ResponseEntity) respBody).getData();
            }

            writer.write(JSON.format(respData).toString());
            writer.flush();
        }
    }
}
