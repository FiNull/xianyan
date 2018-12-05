package cn.finull.xianyan.config;

import cn.finull.framework.db.orm.BaseDao;
import cn.finull.framework.db.orm.DaoInitialize;
import cn.finull.xianyan.dao.*;
import java.util.List;

/**
 * 数据访问层配置
 */
public class DaoConfig extends DaoInitialize {

    @Override
    public void addDaoClass(List<Class<? extends BaseDao>> list) {
        list.add(IArticleDao.class);
        list.add(ICommentDao.class);
        list.add(IUserArticleStarDao.class);
        list.add(IUserCommentStarDao.class);
        list.add(IUserDao.class);
    }
}
