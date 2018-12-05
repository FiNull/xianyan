package cn.finull.xianyan.service.impl;

import cn.finull.framework.config.AppConfig;
import cn.finull.framework.util.HashIDUtil;
import cn.finull.framework.util.ObjectUtil;
import cn.finull.xianyan.bo.ArticleBO;
import cn.finull.xianyan.dao.IArticleDao;
import cn.finull.xianyan.dao.IUserArticleStarDao;
import cn.finull.xianyan.dao.IUserDao;
import cn.finull.xianyan.pojo.Article;
import cn.finull.xianyan.pojo.User;
import cn.finull.xianyan.pojo.UserArticleStar;
import cn.finull.xianyan.service.IArticleService;
import cn.finull.xianyan.vo.ArticleDetailVO;
import cn.finull.xianyan.vo.ArticleVO;
import java.util.ArrayList;
import java.util.List;

public class ArticleServiceImpl implements IArticleService {

    private IArticleDao iArticleDao;
    private IUserDao iUserDao;
    private IUserArticleStarDao iUserArticleStarDao;

    @Override
    public void init() {
        iArticleDao = get(IArticleDao.class);
        iUserDao = get(IUserDao.class);
        iUserArticleStarDao = get(IUserArticleStarDao.class);
    }

    private List<ArticleVO> generatorArticleVOList(List<Article> articleList,String userId) {
        List<ArticleVO> articleVOList = new ArrayList<>();
        for (Article article : articleList) {
            ArticleVO articleVO = new ArticleVO();
            ObjectUtil.copyObject(article,articleVO);
            articleVO.setId(HashIDUtil.encode(article.getId()));
            articleVO.setMainPic(AppConfig.getHttpPrefix() + article.getMainPic());
            articleVO.setAuthorId(HashIDUtil.encode(article.getAuthorId()));
            User user = iUserDao.selectById(article.getAuthorId());
            articleVO.setAuthor(user.getUsername());
            articleVO.setStar(false);
            if (userId != null) {
                int result = iUserArticleStarDao.selectCountByUserIdAndAId((int)HashIDUtil.decode(userId),article.getId()).get("id").intValue();
                if (result > 0) {
                    articleVO.setStar(true);
                }
            }
            articleVOList.add(articleVO);
        }
        return articleVOList;
    }

    @Override
    public List<ArticleVO> findAll(int currNum, int len, String userId) {
        List<Article> articleList = iArticleDao.selectAll(currNum, len);
        return generatorArticleVOList(articleList,userId);
    }

    public List<ArticleVO> findAllByUserId(String authorId,int currNum,int len,String userId) {
        List<Article> articleList = iArticleDao.selectAll((int)HashIDUtil.decode(authorId),currNum,len);
        return generatorArticleVOList(articleList,userId);
    }

    @Override
    public ArticleDetailVO findDetail(String articleId, String userId) {
        Article article = iArticleDao.selectById(HashIDUtil.decode(articleId));
        if (article == null || !article.getDelStatus()) {
            return null;
        }

        article.setReadNum(article.getReadNum() + 1);

        iArticleDao.update(article);

        ArticleDetailVO articleDetailVO = new ArticleDetailVO();

        ObjectUtil.copyObject(article,articleDetailVO);

        articleDetailVO.setId(articleId);
        articleDetailVO.setAuthorId(HashIDUtil.encode(article.getAuthorId()));

        User user = iUserDao.selectById(article.getAuthorId());
        articleDetailVO.setAuthor(user.getUsername());

        articleDetailVO.setStar(false);
        if (userId != null) {
            int result = iUserArticleStarDao.selectCountByUserIdAndAId((int)HashIDUtil.decode(userId),article.getId()).get("id").intValue();
            if (result > 0) {
                articleDetailVO.setStar(true);
            }
        }
        return articleDetailVO;
    }

    @Override
    public boolean starArticle(String userId, String articleId) {

        UserArticleStar articleStar = iUserArticleStarDao
                .selectByUserIdAndAId((int) HashIDUtil.decode(userId),
                        (int) HashIDUtil.decode(articleId));

        Article article = iArticleDao.selectById(HashIDUtil.decode(articleId));
        if (article == null) {
            return false;
        }

        int result;

        if (articleStar == null) {
            articleStar = new UserArticleStar();
            articleStar.setArticleId((int)HashIDUtil.decode(articleId));
            articleStar.setAuthorId((int)HashIDUtil.decode(userId));
            articleStar.setStarStatus(true);
            article.setStarNum(article.getStarNum() + 1);
            result = iUserArticleStarDao.insert(articleStar);
        }
        else {
            articleStar.setStarStatus(!articleStar.getStarStatus());
            if (articleStar.getStarStatus()) {
                article.setStarNum(article.getStarNum() + 1);
            }
            else {
                article.setStarNum(article.getStarNum() - 1);
            }
            result = iUserArticleStarDao.update(articleStar);
        }

        return result > 0 && iArticleDao.update(article) > 0;
    }

    @Override
    public boolean save(ArticleBO articleBO, String userId) {
        Article article = new Article();
        ObjectUtil.copyObject(articleBO,article);

        article.setAuthorId((int)HashIDUtil.decode(userId));

        return iArticleDao.insert(article) > 0;
    }

    @Override
    public boolean delete(String articleId) {
        Article article = iArticleDao.selectById(HashIDUtil.decode(articleId));
        if (article == null) {
            return false;
        }
        article.setDelStatus(false);
        return iArticleDao.update(article) > 0;
    }
}
