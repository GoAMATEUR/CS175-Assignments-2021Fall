package me.hsy.mycanvas.ui.home.beans

import java.util.*

class Note(val id: Long) {
    var date: Date? = null
    var course: Course? = null
    var state: State? = null
    var content: String? = null
    var classification: Classification? = null
}