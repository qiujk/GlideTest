package com.sn.glidetest.user;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
import com.sn.glidetest.glide.OkHttpGlideUrlLoader;
import com.sn.glidetest.glide.ProgressInterceptor;

import java.io.InputStream;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2018/1/12.
 */

public class UserGlideModule implements GlideModule {
    public static final int DISK_CACHE_SIZE = 500 * 1024 * 1024;

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        //其实Glide的这些默认配置都非常科学且合理，使用的缓存算法也都是效率极高的，
        // 因此在绝大多数情况下我们并不需要去修改这些默认配置，这也是Glide用法能如此简洁的一个原因
        //setMemoryCache()
        //用于配置Glide的内存缓存策略，默认配置是LruResourceCache。
        //setBitmapPool()
        //用于配置Glide的Bitmap缓存池，默认配置是LruBitmapPool。
        //setDiskCache()
        //用于配置Glide的硬盘缓存策略，默认配置是InternalCacheDiskCacheFactory。
        //setDiskCacheService()
        //用于配置Glide读取缓存中图片的异步执行器，默认配置是FifoPriorityThreadPoolExecutor，也就是先入先出原则。
        //setResizeService()
        //用于配置Glide读取非缓存中图片的异步执行器，默认配置也是FifoPriorityThreadPoolExecutor。
        //setDecodeFormat()
        //用于配置Glide加载图片的解码模式，默认配置是RGB_565。
        //————————————————————————————————————
        //更改默认的硬盘缓存策略
        //Glide默认的硬盘缓存策略使用的是InternalCacheDiskCacheFactory，
        // 这种缓存会将所有Glide加载的图片都存储到当前应用的私有目录下。
        // 这是一种非常安全的做法，但同时这种做法也造成了一些不便，
        // 因为私有目录下即使是开发者自己也是无法查看的，
        // 如果我想要去验证一下图片到底有没有成功缓存下来
        // 使用ExternalCacheDiskCacheFactory来替换默认的InternalCacheDiskCacheFactory，
        // 从而将所有Glide加载的图片都缓存到SD卡上
        builder.setDiskCache(new ExternalCacheDiskCacheFactory(context));
        //InternalCacheDiskCacheFactory和ExternalCacheDiskCacheFactory的默认硬盘缓存大小都是250M
        //那么Glide就会按照DiskLruCache算法的原则来清理缓存的图片
        //对默认缓存大小进行修改
        builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, DISK_CACHE_SIZE));
        //Glide加载图片的默认格式是RGB_565，而Picasso加载图片的默认格式是ARGB_8888
        //Glide也能使用ARGB_8888的图片格式
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        //替换默认的Glide组件
        try {
            //其实大多数情况下并不需要我们去做什么替换
            //比较大的替换需求，那就是Glide的HTTP通讯组件
            //Glide使用的是基于原生HttpURLConnection进行订制的HTTP通讯组件，但是现在大多数的Android开发者都更喜欢使用OkHttp，
            //因此将Glide中的HTTP通讯组件修改成OkHttp的这个需求比较常见，

            //glide.register(GlideUrl.class, InputStream.class, new OkHttpGlideUrlLoader.Factory());
            //增加拦截器，
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.addInterceptor(new ProgressInterceptor());
            OkHttpClient okHttpClient = builder.build();
            glide.register(GlideUrl.class, InputStream.class, new OkHttpGlideUrlLoader.Factory(okHttpClient));
        } catch (Exception e) {
            Log.e("GlideTest", "UserGlideModule registerComponents error:" + e.getMessage());
        }
    }
}
