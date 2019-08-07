package cn.finull.xianyan.handler;

import cn.finull.framework.core.bean.Handler;
import cn.finull.framework.core.request.Parameter;
import cn.finull.framework.core.response.HttpStatus;
import cn.finull.framework.core.response.ResponseEntity;
import cn.finull.xianyan.bo.ArticleBO;
import cn.finull.xianyan.common.Commons;
import cn.finull.xianyan.service.IArticleService;
import cn.finull.xianyan.vo.ArticleDetailVO;
import cn.finull.xianyan.vo.ArticleVO;
import cn.finull.xianyan.vo.UserVO;

import java.util.List;

public class ArticleHandler implements Handler {
    @Override
    public Class getClassKey() {
        return ArticleHandler.class;
    }

    private IArticleService iArticleService;

    @Override
    public void init() {
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
     * 查询所有文章
     *
     * @return 文章列表
     */
    public ResponseEntity<List<ArticleVO>> findAll(Parameter p) {
        String keyword = p.body().byName("keyword");
        int curNum = p.body().intByName("currentNum");
        int len = p.body().intByName("len");
        return new ResponseEntity<>(
                iArticleService.findAll(keyword, curNum, len, getUserId(p)));
    }

    /**
     * 查看文章详情
     *
     * @return 文章详情 404:资源未找到
     */
    public ResponseEntity findDetail(Parameter p) {
        String articleId = p.body().byName("articleId");
        ArticleDetailVO articleDetailVO = iArticleService.findDetail(articleId, getUserId(p));
        if (articleDetailVO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND, null);
        }
        return new ResponseEntity<>(articleDetailVO);
    }

    /**
     * 修改文章点赞状态
     *
     * @return 200:修改成功 403:未登录 500:修改失败
     */
    public ResponseEntity starArticle(Parameter p) {
        UserVO userVO = (UserVO) p.session().get(Commons.CURRENT_USER);
        if (userVO == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN, null);
        }
        boolean flag = iArticleService.starArticle(userVO.getId(), p.body().byName("articleId"));
        return new ResponseEntity<>(flag ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    /**
     * 添加文章
     *
     * @return 201:添加成功 403:未登录 500:添加失败
     */
    public ResponseEntity save(Parameter p) {
        UserVO userVO = (UserVO) p.session().get(Commons.CURRENT_USER);
        if (userVO == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN, null);
        }

        ArticleBO articleBO = p.body().to(ArticleBO.class);
        boolean flag = iArticleService.save(articleBO, userVO.getId());

        return new ResponseEntity<>(flag ? HttpStatus.CREATED : HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    // 管理员模块

    /**
     * 删除文章
     *
     * @return 200:删除成功 403:没有权限 404:资源未找到
     */
    public ResponseEntity delete(Parameter p) {
        UserVO userVO = (UserVO) p.session().get(Commons.CURRENT_USER);
        if (userVO == null || !userVO.getRole()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN, null);
        }

        boolean flag = iArticleService.delete(p.body().byName("articleId"));
        return new ResponseEntity<>(flag ? HttpStatus.OK : HttpStatus.NOT_FOUND, null);
    }
}
