package me.hsy.mycanvas.util

import java.text.SimpleDateFormat
import java.util.*

class TimeParser {

    companion object{
        fun dateToMill(formatDate: String): Long {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            var date = simpleDateFormat.parse(formatDate)
            return date.time
        }
        fun millTodate(time: Long): String {
            val date = Date(time)
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            //Log.d("@=>", date.day.toString())
            return simpleDateFormat.format(date)
        }
    }

}