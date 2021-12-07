package me.hsy.mycanvas.ui.home.beans

// Author: HUANG SIYUAN

enum class State(val intValue: Int) {
    // State of the task
    TODO(0), DONE(1);

    companion object{
        fun from(intValue: Int): State {
            for (state in State.values()) {
                if (state.intValue == intValue) {
                    return state
                }
            }
            return TODO
        }
    }
}