package cn.finull.xianyan.dao;

import cn.finull.framework.db.annotation.Select;
import cn.finull.framework.db.orm.BaseDao;
import cn.finull.xianyan.pojo.Article;
import java.util.List;

public interface IArticleDao extends BaseDao<Article> {

    @Select("SELECT `id`,`title`,`content`,`text`,`main_pic`,`read_num`,`star_num`," +
            "`author_id`,`del_status`,`save_time`,`update_time` FROM `articles` " +
            "WHERE `del_status` = 1 ORDER BY `save_time` DESC " +
            "LIMIT ?,?;")
    List<Article> selectAll(int curNum,int len);

    @Select("SELECT `id`,`title`,`content`,`text`,`main_pic`,`read_num`,`star_num`," +
            "`author_id`,`del_status`,`save_time`,`update_time` FROM `articles` " +
            "WHERE `del_status` = 1 AND `author_id` = ? ORDER BY `save_time` DESC " +
            "LIMIT ?,?;")
    List<Article> selectAll(Integer authorId,int curNum,int len);
}
