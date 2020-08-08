package com.orin.booruviewer;

import android.app.Application;

import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.orin.booruviewer.api.ApiRequest;
import com.orin.booruviewer.api.GelbooruApi;
import com.orin.booruviewer.util.FileUtils;

import java.io.File;
import java.util.HashMap;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        initImageLoader();
        initGelbooruApi();
        ApiRequest.init(this);
        FileUtils.init(this);
        BigImageViewer.initialize(GlideImageLoader.with(this));
    }

    private void initImageLoader() {
        File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(options)
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .build();

        ImageLoader.getInstance().init(config);
    }

    private void initGelbooruApi() {
        final String API_KEY = "14edc38a7116177fe8cbefcc110df631081a331ee7c34e301609a8a9b92470d8";
        final String USER_ID = "465352";
        HashMap<String, String> credentials = new HashMap<>();

        credentials.put("apiKey", API_KEY);
        credentials.put("userId", USER_ID);

        GelbooruApi.getInstance().init(credentials);
    }
}
