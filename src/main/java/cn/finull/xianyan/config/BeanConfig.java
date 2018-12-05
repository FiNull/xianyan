package cn.finull.xianyan.config;

import cn.finull.framework.core.BeanRepertory;
import cn.finull.framework.core.BeanRepertoryInitialize;
import cn.finull.xianyan.handler.*;
import cn.finull.xianyan.service.impl.ArticleServiceImpl;
import cn.finull.xianyan.service.impl.CommentServiceImpl;
import cn.finull.xianyan.service.impl.UserServiceImpl;

/**
 * 仓库配置
 */
public class BeanConfig extends BeanRepertoryInitialize {

    @Override
    public void addBeans(BeanRepertory repertory) {
        addHandlers(repertory);
        addServices(repertory);
    }

    private void addHandlers(BeanRepertory repertory) {
        repertory.add(new UserHandler());
        repertory.add(new FileHandler());
        repertory.add(new ArticleHandler());
        repertory.add(new CommentHandler());
    }

    private void addServices(BeanRepertory repertory) {
        repertory.add(new UserServiceImpl());
        repertory.add(new ArticleServiceImpl());
        repertory.add(new CommentServiceImpl());
    }
}
