package cn.finull.framework.core;

import cn.finull.framework.config.AppConfig;
import cn.finull.framework.core.response.HttpStatus;
import cn.finull.framework.db.BasicDataSource;
import cn.finull.framework.db.BasicTransactionManager;
import cn.finull.framework.except.BadParameterException;
import cn.finull.framework.except.MethodNotAllowedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 当项目开始后，将会创建一个容器管理整个项目
 */
@WebListener
public class ApplicationInitListener implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationInitListener.class);

    public void contextInitialized(ServletContextEvent sce) {
        List<String> repertories = AppConfig.getRepertories();
        List<String> handlers = AppConfig.getHandlers();
        List<String> dao = AppConfig.getDBDao();
        // 初始化数据源与事务管理器
        BeanRepertory.getInstance().add(new BasicDataSource());
        BeanRepertory.getInstance().add(new BasicTransactionManager());
        // 初始化 bean仓库
        repertories.forEach(r -> {
            try {
                Class.forName(r).newInstance();
            } catch (Exception e) {
                LOG.error("Load Repertories failure: {}",e.getMessage());
                throw new RuntimeException(e);
            }
        });
        // 初始化 dao中的bean
        dao.forEach(d -> {
            try {
                Class.forName(d).newInstance();
            } catch (Exception e) {
                LOG.error("Load dao failure: {}",e.getMessage());
                throw new RuntimeException(e);
            }
        });
        // 初始化所有依赖
        BeanRepertory.getInstance().init();
        // 初始化所有handler
        handlers.forEach(h -> {
            try {
                Class.forName(h).newInstance();
            } catch (Exception e) {
                LOG.error("Load Handlers failure: {}",e.getMessage());
                throw new RuntimeException(e);
            }
        });
        // 初始化常规异常处理
        AdviceRepertory repertory = AdviceRepertory.getInstance();
        repertory.add(BadParameterException.class,(e,r) -> {
            r.setStatus(HttpStatus.BAD_REQUEST);
            Map<String,String> message = new HashMap<>();
            message.put("message",e.getMessage());
            return message;
        });
        repertory.add(MethodNotAllowedException.class,(e,r) -> {
            r.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
            Map<String,String> message = new HashMap<>();
            message.put("message",e.getMessage());
            return message;
        });
        // 初始化所有异常函数
        List<String> advice = AppConfig.getAdvice();
        advice.forEach(s -> {
            try {
                Class.forName(s).newInstance();
            } catch (Exception e) {
                LOG.error("Load advice failure: {}",e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }
}
