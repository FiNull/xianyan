package cn.finull.xianyan.service.impl;

import cn.finull.framework.config.AppConfig;
import cn.finull.framework.util.HashIDUtil;
import cn.finull.framework.util.ObjectUtil;
import cn.finull.xianyan.bo.CommentBO;
import cn.finull.xianyan.dao.ICommentDao;
import cn.finull.xianyan.dao.IUserCommentStarDao;
import cn.finull.xianyan.dao.IUserDao;
import cn.finull.xianyan.pojo.Comment;
import cn.finull.xianyan.pojo.User;
import cn.finull.xianyan.pojo.UserCommentStar;
import cn.finull.xianyan.service.ICommentService;
import cn.finull.xianyan.vo.CommentVO;
import java.util.ArrayList;
import java.util.List;

public class CommentServiceImpl implements ICommentService {

    private IUserDao iUserDao;
    private ICommentDao iCommentDao;
    private IUserCommentStarDao iUserCommentStarDao;

    @Override
    public void init() {
        iUserDao = get(IUserDao.class);
        iCommentDao = get(ICommentDao.class);
        iUserCommentStarDao = get(IUserCommentStarDao.class);
    }

    private CommentVO generatorCommentVO(Comment comment, String userId) {
        CommentVO commentVO = new CommentVO();

        ObjectUtil.copyObject(comment,commentVO);

        commentVO.setId(HashIDUtil.encode(comment.getId()));
        commentVO.setAuthorId(HashIDUtil.encode(comment.getAuthorId()));

        User user = iUserDao.selectById(comment.getAuthorId());
        commentVO.setAuthor(user.getUsername());
        commentVO.setPhoto(AppConfig.getHttpPrefix() + user.getPhoto());

        commentVO.setStar(false);
        if (userId != null) {
            int result = iUserCommentStarDao.selectCountByUserIdAndCId((int) HashIDUtil.decode(userId), comment.getId()).get("id").intValue();
            if (result > 0) {
                commentVO.setStar(true);
            }
        }

        return commentVO;
    }

    @Override
    public List<CommentVO> findAll(String aId, int curNum, int len, String userId) {
        List<Comment> commentList = iCommentDao.selectAll(
                (int)HashIDUtil.decode(aId),curNum,len);
        List<CommentVO> commentVOList = new ArrayList<>();
        for (Comment comment : commentList) {
            commentVOList.add(generatorCommentVO(comment,userId));
        }
        return commentVOList;
    }

    @Override
    public boolean starComment(String userId, String cId) {

        UserCommentStar userCommentStar = iUserCommentStarDao
                .selectByUserIdAndCId((int)HashIDUtil.decode(userId),
                        (int)HashIDUtil.decode(cId));

        Comment comment = iCommentDao.selectById(HashIDUtil.decode(cId));
        if (comment == null || !comment.getDelStatus()) {
            return false;
        }

        int result;

        if (userCommentStar == null) {
            userCommentStar = new UserCommentStar();
            userCommentStar.setAuthorId((int)HashIDUtil.decode(userId));
            userCommentStar.setCommentId((int)HashIDUtil.decode(cId));
            userCommentStar.setStarStatus(true);
            comment.setStarNum(comment.getStarNum() + 1);
            result = iUserCommentStarDao.insert(userCommentStar);
        }
        else {
            userCommentStar.setStarStatus(!userCommentStar.getStarStatus());
            if (userCommentStar.getStarStatus()) {
                comment.setStarNum(comment.getStarNum() + 1);
            }
            else {
                comment.setStarNum(comment.getStarNum() - 1);
            }
            result = iUserCommentStarDao.update(userCommentStar);
        }

        return result > 0 && iCommentDao.update(comment) > 0;
    }

    @Override
    public CommentVO save(CommentBO commentBO,String userId) {
        Comment comment = new Comment();
        comment.setArticleId((int)HashIDUtil.decode(commentBO.getArticleId()));
        comment.setAuthorId((int)HashIDUtil.decode(userId));
        comment.setContent(commentBO.getContent());

        int result = iCommentDao.insert(comment);
        if (result == 0) {
            return null;
        }

        comment = iCommentDao.selectById(comment.getId());
        return generatorCommentVO(comment,userId);
    }

    @Override
    public boolean delete(String commentId) {
        Comment comment = iCommentDao.selectById(HashIDUtil.decode(commentId));
        if (comment == null) {
            return false;
        }
        comment.setDelStatus(false);
        return iCommentDao.update(comment) > 0;
    }
}
