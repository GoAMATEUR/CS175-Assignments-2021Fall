package com.bytedance.compicatedcomponent.homework

import android.app.Activity
import android.os.Bundle
import android.os.Looper
import android.os.Message
import com.bytedance.compicatedcomponent.R
import java.util.logging.Handler

/**
 *  author : Huang Siyuan
 *  time   : 2021/11/2
 *  desc   :
 */
class ClockActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock)
    }
}