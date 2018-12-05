package cn.finull.xianyan.dao;

import cn.finull.framework.db.annotation.Select;
import cn.finull.framework.db.orm.BaseDao;
import cn.finull.xianyan.pojo.UserCommentStar;
import java.util.Map;

public interface IUserCommentStarDao extends BaseDao<UserCommentStar> {

    @Select("SELECT COUNT(`id`) AS `id` FROM `user_comment_star` WHERE `author_id` = ? AND `comment_id` = ? AND `star_status` = 1;")
    Map<String,Long> selectCountByUserIdAndCId(Integer userId, Integer cId);

    @Select("SELECT `id`,`author_id`,`comment_id`,`star_status` FROM `user_comment_star` " +
            "WHERE `author_id` = ? AND `comment_id` = ?;")
    UserCommentStar selectByUserIdAndCId(Integer userId,Integer cId);
}
