package cn.finull.xianyan.dao;

import cn.finull.framework.db.annotation.Select;
import cn.finull.framework.db.orm.BaseDao;
import cn.finull.xianyan.pojo.UserArticleStar;

import java.util.Map;

public interface IUserArticleStarDao extends BaseDao<UserArticleStar> {

    @Select("SELECT COUNT(`id`) AS `id` FROM `user_article_star` WHERE `author_id` = ? AND `article_id` = ? AND `star_status` = 1;")
    Map<String, Long> selectCountByUserIdAndAId(int userId, int aId);

    @Select("SELECT `id`,`article_id`,`author_id`,`star_status` FROM `user_article_star` " +
            "WHERE `author_id` = ? AND `article_id` = ?;")
    UserArticleStar selectByUserIdAndAId(int userId, int aId);
}
