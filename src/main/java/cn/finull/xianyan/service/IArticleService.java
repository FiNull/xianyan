package cn.finull.xianyan.service;

import cn.finull.framework.core.bean.Service;
import cn.finull.xianyan.bo.ArticleBO;
import cn.finull.xianyan.vo.ArticleDetailVO;
import cn.finull.xianyan.vo.ArticleVO;
import java.util.List;

public interface IArticleService extends Service {
    @Override
    default Class getClassKey() {
        return IArticleService.class;
    }

    /**
     * 查找所有文章
     * @param currNum 当前文章数
     * @param len 要查询的数量
     * @param userId 当前用户ID，游客传入null
     * @return 所有文章
     */
    List<ArticleVO> findAll(int currNum,int len,String userId);

    /**
     * 查询指定用户的所有文章
     * @param authorId 指定用户
     * @param currNum 当前数量
     * @param len 查询数量
     * @param userId 当前用户ID，游客传入null
     * @return 指定用户的所有文章
     */
    List<ArticleVO> findAllByUserId(String authorId,int currNum,int len,String userId);

    /**
     * 查找文章详情
     * @param articleId 文章ID
     * @param userId 当前用户ID，游客传入null
     * @return 文章详情
     */
    ArticleDetailVO findDetail(String articleId,String userId);

    /**
     * 修改文章点赞状态
     * @param userId 用户ID
     * @param articleId 文章ID
     * @return 成功返回true，否则返回false
     */
    boolean starArticle(String userId,String articleId);

    /**
     * 添加文章
     * @param articleBO 文章信息
     * @param userId 用户ID
     * @return 成功返回true，否则返回false
     */
    boolean save(ArticleBO articleBO,String userId);

    /**
     * 删除文章
     * @param articleId 文章ID
     * @return 成功返回true，否则返回false
     */
    boolean delete(String articleId);
}
