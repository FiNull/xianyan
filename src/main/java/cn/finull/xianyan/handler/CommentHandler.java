package cn.finull.xianyan.handler;

import cn.finull.framework.core.bean.Handler;
import cn.finull.framework.core.request.Parameter;
import cn.finull.framework.core.response.HttpStatus;
import cn.finull.framework.core.response.ResponseEntity;
import cn.finull.xianyan.bo.CommentBO;
import cn.finull.xianyan.common.Commons;
import cn.finull.xianyan.service.ICommentService;
import cn.finull.xianyan.vo.CommentVO;
import cn.finull.xianyan.vo.UserVO;
import java.util.List;

public class CommentHandler implements Handler {
    @Override
    public Class getClassKey() {
        return CommentHandler.class;
    }

    private ICommentService iCommentService;

    @Override
    public void init() {
        iCommentService = get(ICommentService.class);
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
     * 查询所有评论
     * @return 评论列表
     */
    public ResponseEntity<List<CommentVO>> findAll(Parameter p) {
        String aId = p.body().byName("articleId");
        int curNum = p.body().intByName("currentNum");
        int len = p.body().intByName("len");
        return new ResponseEntity<>(
                iCommentService.findAll(aId,curNum,len,getUserId(p)));
    }

    /**
     * 修改评论点赞状态，若未点赞，设置为点赞状态；否则取消点赞
     * @return 200:操作成功 403:用户未登录 500:操作失败(数据库连接)
     */
    public ResponseEntity starComment(Parameter p) {
        UserVO userVO = (UserVO) p.session().get(Commons.CURRENT_USER);
        if (userVO == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN,null);
        }
        boolean flag =  iCommentService.starComment(userVO.getId(),p.body().byName("cId"));
        return new ResponseEntity<>(flag ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR,null);
    }

    /**
     * 添加评论
     * @return 201:操作成功 403:用户未登录 500:添加失败
     */
    public ResponseEntity save(Parameter p) {
        UserVO userVO = (UserVO) p.session().get(Commons.CURRENT_USER);
        if (userVO == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN,null);
        }
        CommentBO commentBO = p.body().to(CommentBO.class);
        CommentVO commentVO = iCommentService.save(commentBO,userVO.getId());
        if (commentVO == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR,null);
        }
        return new ResponseEntity<>(HttpStatus.CREATED,commentVO);
    }

    // 管理员模块

    /**
     * 删除评论
     * @return 200:删除成功 403:无权限 404:资源不存在
     */
    public ResponseEntity delete(Parameter p) {
        UserVO userVO = (UserVO) p.session().get(Commons.CURRENT_USER);
        if (userVO == null || !userVO.getRole()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN,null);
        }
        boolean flag = iCommentService.delete(p.body().byName("commentId"));
        return new ResponseEntity<>(flag ? HttpStatus.OK : HttpStatus.NOT_FOUND,null);
    }
}
