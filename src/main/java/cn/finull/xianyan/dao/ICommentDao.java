package cn.finull.xianyan.dao;

import cn.finull.framework.db.annotation.Select;
import cn.finull.framework.db.orm.BaseDao;
import cn.finull.xianyan.pojo.Comment;
import java.util.List;

public interface ICommentDao extends BaseDao<Comment> {

    @Select("SELECT `id`,`article_id`,`author_id`,`content`,`del_status`,`star_num`,`save_time` " +
            "FROM `comments` WHERE `article_id` = ? AND `del_status` = 1 ORDER BY `save_time` DESC LIMIT ?,?;")
    List<Comment> selectAll(Integer articleId,int curNum,int len);
}
