package com.example.assignment_pro1121_nhom3.utils;

import android.app.Application;

import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

public class MyApp extends Application {

    public static SimpleCache simpleCache;
    final Long exoPlayerCacheSize = (long) (90 * 1024 * 1024);
    LeastRecentlyUsedCacheEvictor leastRecentlyUsedCacheEvictor;
    ExoDatabaseProvider exoDatabaseProvider;

    @Override
    public void onCreate() {
        super.onCreate();
        leastRecentlyUsedCacheEvictor = new LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize);
        exoDatabaseProvider = new ExoDatabaseProvider(this);
        simpleCache = new SimpleCache(this.getCacheDir(), leastRecentlyUsedCacheEvictor, exoDatabaseProvider);
    }
}
