package cn.finull.framework.core.filter;

import cn.finull.framework.config.AppConfig;
import cn.finull.framework.core.AdviceRepertory;
import cn.finull.framework.json.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.BiFunction;

@WebFilter(filterName = "ExceptionFilter",value = "/*")
public class BExceptionFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(BExceptionFilter.class);

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        try {
            chain.doFilter(req, resp);
        } catch (Throwable e) {
            BiFunction handler = AdviceRepertory.getInstance().handler(e.getClass());
            if (handler == null) {
                LOG.error("Handler Exception Failure: {}",e.getMessage());
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            else {
                try {
                    Object respBody = handler.apply(
                            e.getClass()
                                    .getConstructor(Throwable.class)
                                    .newInstance(e),
                            response
                    );
                    if (respBody == null) {
                        response.getWriter().flush();
                        return;
                    }
                    if (respBody instanceof String) {
                        request.getRequestDispatcher(AppConfig.getViewPrefix() + respBody + AppConfig.getViewSuffix())
                                .forward(request,response);
                    }
                    else {
                        response.getWriter().write(JSON.format(respBody).toString());
                        response.getWriter().flush();
                    }
                } catch (Exception e1) {
                    throw new RuntimeException(e1);
                }
            }
        }
    }
}
