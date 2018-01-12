package com.sn.glidetest;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.sn.glidetest.glide.ProgressInterceptor;
import com.sn.glidetest.glide.TokenGlideUrl;
import com.sn.glidetest.user.UserDownloadTarget;
import com.sn.glidetest.user.UserLayout;

import java.io.File;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MainActivity extends AppCompatActivity {
    private Button btnLoadImg;
    private ImageView imageView;
    private UserLayout userLayout;
    ProgressDialog progressDialog;

    String url = "https://cn.bing.com/az/hprichbg/rb/SamburuNests_ZH-CN11974788746_1920x1080.jpg";
    String gifUrl = "http://p1.pstatp.com/large/166200019850062839d3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLoadImg = (Button) findViewById(R.id.btnloadimgag);
        imageView = (ImageView) findViewById(R.id.ivloadimage);
        btnLoadImg.setOnClickListener(onClickForButton);
        userLayout = (UserLayout) findViewById(R.id.userLayoutBackground);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("加载中");
    }

    public void loadProgressImage(View view) {
        //监听下载进度
        //依靠OkHttp强大的拦截器机制了,需要首先把HttpUrlConnection 替换成OkHttp

        ProgressInterceptor.addListener(url, new com.sn.glidetest.glide.ProgressListener() {
            @Override
            public void onProgress(int progress) {
                progressDialog.setProgress(progress);
            }
        });
        Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                //.into(userLayout.getTarget());
                .into(new GlideDrawableImageViewTarget(imageView) {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        progressDialog.show();
                    }

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        progressDialog.dismiss();
                        ProgressInterceptor.removeListener(url);
                    }
                });
    }

    public void loadTargetImage(View view) {
        //into传入别的参数
        String url = "https://cn.bing.com/az/hprichbg/rb/BowSnow_ZH-CN10193462171_1920x1080.jpg";
        Glide.with(this)
                .load(url)
                .into(userLayout.getTarget());
    }

    public void loadPreImage(View view) {
        //预加载
        //需要注意的是，我们如果使用了preload()方法，最好要将diskCacheStrategy的缓存策略指定成DiskCacheStrategy.SOURCE。
        // 因为preload()方法默认是预加载的原始图片大小，而into()方法则默认会根据ImageView控件的大小来动态决定加载图片的大小。
        // 因此，如果不将diskCacheStrategy的缓存策略指定成DiskCacheStrategy.SOURCE的话，
        // 很容易会造成我们在预加载完成之后再使用into()方法加载图片，却仍然还是要从网络上去请求图片这种现象
        String url = "https://cn.bing.com/az/hprichbg/rb/BowSnow_ZH-CN10193462171_1920x1080.jpg";
        Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .preload();
        //调用预加载后，再去调用内存中的图片
        Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
    }

    public void downloadImage(View view) {
        //使用Target h获取下载的文件
        //downloadOnly(Y target) 在主线程中下载图片的
        String url = "http://cn.bing.com/az/hprichbg/rb/TOAD_ZH-CN7336795473_1920x1080.jpg";
        Glide.with(this)
                .load(url)
                .downloadOnly(new UserDownloadTarget());

        //downloadOnly()方法表示只会下载图片
        //downloadOnly(int width, int height) 是用于在子线程中下载图片的
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String url = "https://cn.bing.com/az/hprichbg/rb/BowSnow_ZH-CN10193462171_1920x1080.jpg";
                    final Context context = getApplicationContext();
                    //我们先获取了一个Application Context，这个时候不能再用Activity作为Context了，
                    // 因为会有Activity销毁了但子线程还没执行完这种可能出现
                    FutureTarget<File> target = Glide.with(context)
                            .load(url)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                    final File imageFile = target.get();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadDownImage();
                            Toast.makeText(context, imageFile.getPath(), Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void loadDownImage() {
        //这里必须将硬盘缓存策略指定成DiskCacheStrategy.SOURCE或者DiskCacheStrategy.ALL，
        // 否则Glide将无法使用我们刚才下载好的图片缓存文件
        String url = "https://cn.bing.com/az/hprichbg/rb/BowSnow_ZH-CN10193462171_1920x1080.jpg";
        Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
    }

    View.OnClickListener onClickForButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnloadimgag:
                    String url = "https://cn.bing.com/az/hprichbg/rb/BowSnow_ZH-CN10193462171_1920x1080.jpg";
                    String gifUrl = "http://p1.pstatp.com/large/166200019850062839d3";
                    String urltoken = "http://url.com/image.jpg?token=d9caa6e02c990b0a";
                    //Glide 不用担心图片内存浪费，甚至是内存溢出的问题。因为Glide从来都不会直接将图片的完整尺寸全部加载到内存中，而是用多少加载多少
                    //with()方法返回的是一个RequestManager对象
                    Glide.with(getApplicationContext())
                            //解决Token加载图片 缓存中的token 不断变化导致内存变化的问题
                            .load(new TokenGlideUrl(url))
                            //只允许加载静态图片
                            //.asBitmap()
                            //.asGif()//只允许加载GIF
                            //占位图显示,loading 过程
                            .placeholder(R.drawable.loading_01)
                            //调用diskCacheStrategy()方法并传入DiskCacheStrategy.NONE，就可以禁用掉Glide的硬盘缓存功能了
                            //DiskCacheStrategy.NONE： 表示不缓存任何内容。
                            //DiskCacheStrategy.SOURCE： 表示只缓存原始图片。
                            //DiskCacheStrategy.RESULT： 表示只缓存转换过后的图片（默认选项）。
                            //DiskCacheStrategy.ALL ： 表示既缓存原始图片，也缓存转换过后的图片。
                            //.diskCacheStrategy(DiskCacheStrategy.NONE)
                            //加载失败
                            .error(R.drawable.error)
                            //listener监听图片的状态
                            /*
                            .listener(new RequestListener<TokenGlideUrl, Bitmap>() {
                                @Override
                                public boolean onException(Exception e, TokenGlideUrl model, Target<Bitmap> target,
                                                           boolean isFirstResource) {
                                    //而当图片加载失败的时候就会回调onException()方法
                                    Log.e("GildeTest", "onClickForButton Glide load error:" + e.getMessage());
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Bitmap resource, TokenGlideUrl model, Target<Bitmap> target,
                                                               boolean isFromMemoryCache, boolean isFirstResource) {
                                    //当图片加载完成的时候就会回调onResourceReady()方法
                                    return false;
                                }
                            })
                            */
                            //原始大小
                            //.override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            // 也支持图片压缩
                            //.override(100, 100)
                            //调用skipMemoryCache()方法并传入true，就表示禁用掉Glide的内存缓存功能
                            //.skipMemoryCache(true)
                            //图片转换centerCrop fitCenter 都封装了.transform()
                            //.centerCrop()
                            //.fitCenter()
                            //user重写图片处理
                            //.transform(new UserCircleCrop(getApplicationContext()))
                            //bitmapTransform()方法而不是transform()方法，
                            //因为glide-transformations库都是专门针对静态图片变换来进行设计的
                            .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                            .into(imageView);
                    //into 一个接收Target参数的重载。即使我们传入的参数是ImageView，Glide也会在内部自动构建一个Target对象。
                    //而如果我们能够掌握自定义Target技术的话，就可以更加随心所欲地控制Glide的回调了
                    //如果我们要进行自定义的话，通常只需要在两种Target的基础上去自定义就可以了，一种是SimpleTarget，一种是ViewTarget。

                    //// 加载本地图片
                    //File file = new File(getExternalCacheDir() + "/image.jpg");
                    //Glide.with(this).load(file).into(imageView);
                    //
                    //// 加载应用资源
                    //int resource = R.drawable.image;
                    //Glide.with(this).load(resource).into(imageView);
                    //
                    //// 加载二进制流
                    //byte[] image = getImageBytes();
                    //Glide.with(this).load(image).into(imageView);
                    //
                    //// 加载Uri对象
                    //Uri imageUri = getImageUri();
                    //Glide.with(this).load(imageUri).into(imageView);
                    break;
            }
        }
    };
}
