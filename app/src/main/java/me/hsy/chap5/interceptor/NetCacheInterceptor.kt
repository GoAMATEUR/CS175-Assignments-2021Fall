package me.hsy.chap5.interceptor

import android.util.Log
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class NetCacheInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val cacheBuilder = CacheControl.Builder()
        cacheBuilder.maxAge(60, TimeUnit.SECONDS)
        cacheBuilder.maxStale(1, TimeUnit.HOURS)
        val cacheControl = cacheBuilder.build()

        var request = chain.request()
        request = request.newBuilder()
            .cacheControl(cacheControl)
            .build()
        var resp = chain.proceed(request)

        resp = resp.newBuilder()
            .removeHeader("Pragma")
            .header("Cache-Control", "public, max-age=60")
            .build();
        return resp
    }
}