package com.liushao.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

public record PublicArticleSummary(String id, String title, String summary, String image, String channelid,
        String columnid, String istop,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") Date createtime,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") Date updatetime) {}
