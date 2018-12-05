package cn.finull.xianyan.service;

import cn.finull.framework.core.bean.Service;
import cn.finull.xianyan.bo.CommentBO;
import cn.finull.xianyan.vo.CommentVO;
import java.util.List;

public interface ICommentService extends Service {

    @Override
    default Class getClassKey() {
        return ICommentService.class;
    }

    /**
     * 查找所有评论列表
     * @param aId 文章ID
     * @param curNum 当前数量
     * @param len 查找的数量
     * @param userId 用户ID，游客为null
     * @return 评论列表
     */
    List<CommentVO> findAll(String aId,int curNum,int len,String userId);

    /**
     * 设置点赞状态
     * @param userId 用户ID
     * @param cId 评论ID
     * @return 成功返回true；否则返回false
     */
    boolean starComment(String userId,String cId);

    /**
     * 添加评论
     * @param commentBO 评论信息
     * @param userId 用户ID
     * @return 评论信息
     */
    CommentVO save(CommentBO commentBO,String userId);

    // 管理员操作

    /**
     * 删除评论
     * @param commentId 评论ID
     * @return 成功返回true，否则返回false
     */
    boolean delete(String commentId);
}
