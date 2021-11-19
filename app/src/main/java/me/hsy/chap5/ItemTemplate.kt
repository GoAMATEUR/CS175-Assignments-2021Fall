package me.hsy.chap5

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView

class ItemTemplate @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, pos: String, meaning: String
) : LinearLayout(context, attrs) {
    init {
        inflate(context, R.layout.item_template, this)
        val p = findViewById<TextView>(R.id.word_class)
        val e = findViewById<TextView>(R.id.explanation)
        p.text = pos
        e.text = meaning
    }
}