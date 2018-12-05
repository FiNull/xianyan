package cn.finull.framework.core.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * 为请求设置编码
 */
@WebFilter(filterName = "EncodingFilter",value = "/*")
public class AEncodingFilter implements Filter {
    public void doFilter(ServletRequest req, ServletResponse resp,
                         FilterChain chain) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        chain.doFilter(req, resp);
    }
}
