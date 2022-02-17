package me.hsy.chap2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.nio.channels.AsynchronousFileChannel.open

class TextViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_template, parent, false)
) {
    fun update(item: ItemTemplate) {
        itemView.findViewById<TextView>(R.id.title).setText(item.getTitle())
        itemView.findViewById<TextView>(R.id.text_location).setText("Location: "+item.getLocation())
        //itemView.findViewById<TextView>(R.id.text_location).append(item.getLocation())
        itemView.findViewById<TextView>(R.id.text_age).setText("Age: " + item.getAge())
        itemView.findViewById<TextView>(R.id.text_breed).setText("Breed: " + item.getBreed())



        itemView.findViewById<ImageView>(R.id.description_image).setImageResource(R.drawable.im_1)
        //itemView.
    }
}
