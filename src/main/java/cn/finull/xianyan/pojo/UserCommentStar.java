package cn.finull.xianyan.pojo;

import cn.finull.framework.db.annotation.Id;

public class UserCommentStar {

    @Id
    private Integer id;
    private Integer authorId;
    private Integer commentId;
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

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Boolean getStarStatus() {
        return starStatus;
    }

    public void setStarStatus(Boolean starStatus) {
        this.starStatus = starStatus;
    }
}
