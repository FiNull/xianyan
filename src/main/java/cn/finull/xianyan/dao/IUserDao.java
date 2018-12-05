package cn.finull.xianyan.dao;

import cn.finull.framework.db.annotation.Select;
import cn.finull.framework.db.orm.BaseDao;
import cn.finull.xianyan.pojo.User;
import java.util.Map;

public interface IUserDao extends BaseDao<User> {

    @Select("SELECT COUNT(`id`) AS `id` FROM `users` WHERE `username` = ?;")
    Map<String,Long> checkUsername(String username);

    @Select("SELECT `id`,`username`,`password`,`sex`,`photo`,`role`,`register_time` " +
            "FROM `users` WHERE `username` = ?;")
    User selectByUserName(String username);
}
