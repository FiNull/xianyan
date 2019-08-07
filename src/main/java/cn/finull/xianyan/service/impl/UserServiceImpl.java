package cn.finull.xianyan.service.impl;

import cn.finull.framework.config.AppConfig;
import cn.finull.framework.util.HashIDUtil;
import cn.finull.framework.util.ObjectUtil;
import cn.finull.framework.util.StringUtil;
import cn.finull.xianyan.bo.UserBO;
import cn.finull.xianyan.dao.IUserDao;
import cn.finull.xianyan.pojo.User;
import cn.finull.xianyan.service.IUserService;
import cn.finull.xianyan.vo.UserVO;
import org.mindrot.jbcrypt.BCrypt;

public class UserServiceImpl implements IUserService {

    private IUserDao iUserDao;

    @Override
    public void init() {
        iUserDao = get(IUserDao.class);
    }

    @Override
    public boolean check(String username) {
        int result = iUserDao.checkUsername(username).get("id").intValue();
        return result > 0;
    }

    private UserVO generatorUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        ObjectUtil.copyObject(user, userVO);
        userVO.setPhoto(AppConfig.getHttpPrefix() + userVO.getPhoto());
        userVO.setId(HashIDUtil.encode(user.getId()));
        return userVO;
    }

    @Override
    public UserVO register(UserBO userBO) {
        User user = new User();
        ObjectUtil.copyObject(userBO, user);
        // 密码加密
        user.setPassword(BCrypt.hashpw(userBO.getPassword(), BCrypt.gensalt()));

        int result = iUserDao.insert(user);
        UserVO userVO = null;
        if (result > 0) {
            user = iUserDao.selectById(user.getId());
            userVO = generatorUserVO(user);
        }
        return userVO;
    }

    @Override
    public UserVO login(String username, String password) {
        User user = iUserDao.selectByUserName(username);
        UserVO userVO = null;
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            userVO = generatorUserVO(user);
        }
        return userVO;
    }

    @Override
    public UserVO updateUserInfo(UserBO userBO, Integer userId) {
        User user = iUserDao.selectById(userId);
        UserVO userVO = null;
        if (BCrypt.checkpw(userBO.getOldPassword(), user.getPassword())) {
            if (StringUtil.isNotBlank(userBO.getPassword())) {
                userBO.setPassword(BCrypt.hashpw(userBO.getPassword(), BCrypt.gensalt()));
            }
            ObjectUtil.copyObject(userBO, user);
            int result = iUserDao.update(user);
            if (result > 0) {
                user = iUserDao.selectById(userId);
                userVO = generatorUserVO(user);
            }
        }
        return userVO;
    }

    @Override
    public UserVO userInfo(String userId) {
        Long id = HashIDUtil.decode(userId);
        User user = iUserDao.selectById(id);
        return generatorUserVO(user);
    }
}
