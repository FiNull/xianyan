package cn.finull.xianyan.config;

import cn.finull.framework.core.request.RequestHandlerInitialize;
import cn.finull.framework.core.request.Router;
import cn.finull.xianyan.handler.*;

/**
 * 路由映射配置
 */
public class HandlerConfig extends RequestHandlerInitialize {

    @Override
    public void addHandlers(Router router) {
        // 用户模块
        UserHandler userHandler = get(UserHandler.class);
        router.post("/user",userHandler::register)
                .post("/user/:username",userHandler::login)
                .get("/user/:username",userHandler::check)
                .put("/user/username",userHandler::logout)
                .put("/user",userHandler::updateUserInfo)
                .get("/user/articles/:userId/:currentNum/:len",
                        userHandler::articles)
                .get("/user/id/:id",userHandler::userInfo)
                .end();
        // 文件模块
        FileHandler fileHandler = get(FileHandler.class);
        router.post("/file",fileHandler::upload)
                .post("/upload",fileHandler::editUpload)
                .end();
        // 文章模块
        ArticleHandler articleHandler = get(ArticleHandler.class);
        router.get("/articles/:currentNum/:len",articleHandler::findAll)
                .get("/article/:articleId",articleHandler::findDetail)
                .put("/article/:articleId",articleHandler::starArticle)
                .post("/article",articleHandler::save)
                .delete("/article/:articleId",articleHandler::delete)
                .end();
        // 评论模块
        CommentHandler commentHandler = get(CommentHandler.class);
        router.get("/comments/:articleId/:currentNum/:len",commentHandler::findAll)
                .put("/comment/star/:cId",commentHandler::starComment)
                .post("/comment",commentHandler::save)
                .delete("/comment/:commentId",commentHandler::delete)
                .end();
    }
}
