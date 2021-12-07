package me.hsy.mycanvas.ui.home.beans

import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import me.hsy.mycanvas.R

enum class Course(val intValue: Int, val color: Int, val courseName: String) {
    NETWORK(2,R.color.nw_color, "Computer Networks"),
    AI(1, R.color.ai_color.toInt(), "Artificial Intelligence"),
    DIP(0, R.color.dip_color.toInt(), "Digital Image Process"),
    CUSTOM(3, Color.RED, "TODO");

    companion object {
        fun from(intValue: Int): Course {
            for (course in Course.values()) {
                if (course.intValue == intValue) {
                    return course
                }
            }
            return CUSTOM
        }
    }
}