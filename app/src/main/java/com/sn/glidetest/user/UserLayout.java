package com.sn.glidetest.user;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

/**
 * 如果我们要进行自定义的话，通常只需要在两种Target的基础上去自定义就可以了，一种是SimpleTarget，一种是ViewTarget。
 * 对Glide 进行回调，获取图片信息
 */

public class UserLayout extends LinearLayout {

    private ViewTarget<UserLayout, GlideDrawable> viewTarget;
    //context 当前实例
    public UserLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        viewTarget = new ViewTarget<UserLayout, GlideDrawable>(this) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation glideAnimation) {
                UserLayout myLayout = getView();
                myLayout.setImageAsBackground(resource);
            }

            @Override
            public void onLoadStarted(Drawable placeholder) {
                super.onLoadStarted(placeholder);
            }
        };
    }
    public ViewTarget<UserLayout, GlideDrawable> getTarget() {
        return viewTarget;
    }
    public void setImageAsBackground(GlideDrawable resource) {
        setBackground(resource);
    }
}
