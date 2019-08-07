package cn.finull.xianyan.handler;

import cn.finull.framework.core.bean.Handler;
import cn.finull.framework.core.request.Parameter;
import cn.finull.framework.core.response.HttpStatus;
import cn.finull.framework.core.response.ResponseEntity;
import cn.finull.framework.util.HashIDUtil;
import cn.finull.xianyan.bo.UserBO;

import static cn.finull.xianyan.common.Commons.CURRENT_USER;

import cn.finull.xianyan.common.Commons;
import cn.finull.xianyan.service.IArticleService;
import cn.finull.xianyan.service.IUserService;
import cn.finull.xianyan.vo.ArticleVO;
import cn.finull.xianyan.vo.UserVO;

import java.util.List;

/**
 * 用户信息处理
 */
public class UserHandler implements Handler {
    @Override
    public Class getClassKey() {
        return UserHandler.class;
    }

    private IUserService iUserService;

    private IArticleService iArticleService;

    @Override
    public void init() {
        iUserService = get(IUserService.class);
        iArticleService = get(IArticleService.class);
    }

    private String getUserId(Parameter p) {
        String userId = null;
        UserVO userVO = (UserVO) p.session().get(Commons.CURRENT_USER);
        if (userVO != null) {
            userId = userVO.getId();
        }
        return userId;
    }

    /**
     * 检查用户名或邮箱是否已存在
     *
     * @return 200：可用；406：不可用
     */
    public ResponseEntity check(Parameter p) {
        String username = p.body().byName("username");
        return iUserService.check(username) ?
                new ResponseEntity(HttpStatus.NOT_ACCEPTABLE, null) :
                new ResponseEntity(HttpStatus.OK, null);
    }

    /**
     * 用户注册
     *
     * @return 用户信息 201：注册成功；500：注册失败
     */
    public ResponseEntity register(Parameter p) {
        UserBO userBO = p.body().to(UserBO.class);
        UserVO userVO = iUserService.register(userBO);
        if (userVO != null) {
            // 将用户设置为登录状态
            p.session().addSession(CURRENT_USER, userVO);
            return new ResponseEntity<>(HttpStatus.CREATED, userVO);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    /**
     * 用户登录
     *
     * @return 用户信息 200：登录成功；406：登录失败，用户名或密码错误
     */
    public ResponseEntity login(Parameter p) {
        UserVO userVO = iUserService.login(
                p.body().byName("username"),
                p.body().byName("password"));
        if (userVO != null) {
            p.session().addSession(CURRENT_USER, userVO);
            return new ResponseEntity<>(HttpStatus.OK, userVO);
        }
        return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE, null);
    }

    /**
     * 用户登出
     *
     * @return 200：登出成功
     */
    public ResponseEntity logout(Parameter p) {
        p.session().remove(CURRENT_USER);
        return new ResponseEntity(HttpStatus.OK, null);
    }

    /**
     * 修改用户信息
     *
     * @return 200：修改成功；403：用户未登录；500：修改失败
     */
    public ResponseEntity<UserVO> updateUserInfo(Parameter p) {
        UserVO userVO = (UserVO) p.session().get(CURRENT_USER);
        // 用户未登录
        if (userVO == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN, null);
        }
        UserBO userBO = p.body().to(UserBO.class);
        userVO = iUserService.updateUserInfo(userBO, (int) HashIDUtil.decode(userVO.getId()));
        if (userVO != null) {
            p.session().remove(CURRENT_USER);
            p.session().addSession(CURRENT_USER, userVO);
            return new ResponseEntity<>(HttpStatus.OK, userVO);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    /**
     * 查看用户所有文章
     *
     * @return
     */
    public ResponseEntity<List<ArticleVO>> articles(Parameter p) {
        String userId = p.body().byName("userId");
        int currNum = p.body().intByName("currentNum");
        int len = p.body().intByName("len");
        List<ArticleVO> articleVOList = iArticleService.findAllByUserId(userId, currNum, len, getUserId(p));
        return new ResponseEntity<>(articleVOList);
    }

    /**
     * 根据ID获取用户信息
     *
     * @return
     */
    public ResponseEntity userInfo(Parameter p) {
        String userId = p.body().byName("id");
        UserVO userVO = iUserService.userInfo(userId);
        if (userVO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND, null);
        }
        return new ResponseEntity<>(HttpStatus.OK, userVO);
    }
}
