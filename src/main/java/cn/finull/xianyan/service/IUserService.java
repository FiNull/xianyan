package cn.finull.xianyan.service;

import cn.finull.framework.core.bean.Service;
import cn.finull.framework.db.annotation.Transactional;
import cn.finull.xianyan.bo.UserBO;
import cn.finull.xianyan.vo.UserVO;

public interface IUserService extends Service {
    @Override
    default Class getClassKey() {
        return IUserService.class;
    }

    /**
     * 检查用户名或邮箱是否存在
     * @return true：已存在；false：不存在
     */
    boolean check(String username);

    /**
     * 用户注册
     * @return 用户信息
     */
    @Transactional
    UserVO register(UserBO userBO);

    /**
     * 用户登录
     * @return 用户信息
     */
    UserVO login(String username, String password);

    /**
     * 修改用户信息
     * @return 用户信息
     */
    UserVO updateUserInfo(UserBO userBO,Integer userId);
}
