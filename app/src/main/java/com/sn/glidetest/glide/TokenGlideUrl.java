package com.sn.glidetest.glide;

import com.bumptech.glide.load.model.GlideUrl;

/**
 * 解决 Glide 加载带token的utl  Key 经常变化问题
 * http://blog.csdn.net/guolin_blog/article/details/54895665
 */

public class TokenGlideUrl extends GlideUrl {
    private String tokenUrl;

    public TokenGlideUrl(String url) {
        super(url);
        tokenUrl = url;
    }

    public String getTokenUrl() {
        return tokenUrl.replace(findTokenParam(), "");
    }

    private String findTokenParam() {
        String tokenParam = "";
        int tokenKeyIndex = tokenUrl.indexOf("?token=") >= 0 ? tokenUrl.indexOf("?token=") : tokenUrl.indexOf("&token=");
        if (tokenKeyIndex != -1) {
            int nextAndIndex = tokenUrl.indexOf("&", tokenKeyIndex + 1);
            if (nextAndIndex != -1) {
                tokenParam = tokenUrl.substring(tokenKeyIndex + 1, nextAndIndex + 1);
            } else {
                tokenParam = tokenUrl.substring(tokenKeyIndex);
            }
        }
        return tokenParam;
    }
}
