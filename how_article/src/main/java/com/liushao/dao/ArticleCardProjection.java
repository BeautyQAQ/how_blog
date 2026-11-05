package com.liushao.dao;

import java.util.Date;

/** Closed projection: list queries never select the article body. */
public interface ArticleCardProjection {
    String getId();
    String getTitle();
    String getSummary();
    String getExcerpt();
    String getImage();
    String getChannelid();
    String getColumnid();
    String getIstop();
    Date getCreatetime();
    Date getUpdatetime();
}
