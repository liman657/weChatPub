package com.learn.message.response;

import java.util.List;

/**
 * author : liman
 * create time : 2018/9/5
 * QQ:657271181
 * e-mail:liman65727@sina.com
 */
public class NewsMessage extends BaseMessage{

    private int ArticleCount;

    private List<Article> Articles;

    public int getArticleCount() {
        return ArticleCount;
    }

    public void setArticleCount(int articleCount) {
        ArticleCount = articleCount;
    }

    public List<Article> getArticles() {
        return Articles;
    }

    public void setArticles(List<Article> articles) {
        Articles = articles;
    }
}
