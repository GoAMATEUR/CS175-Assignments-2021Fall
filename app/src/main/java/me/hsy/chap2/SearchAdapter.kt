package me.hsy.chap2

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SearchAdapter: RecyclerView.Adapter<TextViewHolder>() {
    private val items = arrayListOf<ItemTemplate>()
    private var itemClickListener: ItemClickListener? =null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        return TextViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
//        val defaultItem: ItemTemplate = ItemTemplate(R.drawable.ic_launcher_background, "TITLE", "description")
//        holder.update(defaultItem)
        holder.update(items.get(position))
        holder.itemView.setOnClickListener{
            itemClickListener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
     fun updateItems(Items: List<ItemTemplate>){
         this.items.clear()
         this.items.addAll(Items)
         notifyDataSetChanged()
     }

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }
    interface ItemClickListener{
        fun onItemClick(position: Int)
    }
}