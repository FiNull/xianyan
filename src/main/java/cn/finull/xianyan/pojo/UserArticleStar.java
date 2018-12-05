package cn.finull.xianyan.pojo;

import cn.finull.framework.db.annotation.Id;

public class UserArticleStar {

    @Id
    private Integer id;
    private Integer authorId;
    private Integer articleId;
    private Boolean starStatus;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Boolean getStarStatus() {
        return starStatus;
    }

    public void setStarStatus(Boolean starStatus) {
        this.starStatus = starStatus;
    }
}
