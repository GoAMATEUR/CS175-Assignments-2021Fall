package me.hsy.mycanvas.ui.home.beans

import android.graphics.Color

enum class Classification(val intValue: Int) {
    //enum values
    Homework(2), Notification(1), Todo(0);

    companion object {
        fun from(intValue: Int): Classification {
            for (classification in Classification.values()) {
                if (classification.intValue == intValue) {
                    return classification
                }
            }
            return Homework
        }
    }
}