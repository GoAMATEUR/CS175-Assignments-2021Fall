package me.hsy.mediaviewer.ui

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class ViewAdapter: PagerAdapter() {
    private var dataList: List<View> = ArrayList()

    override fun getCount(): Int {
        return dataList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = dataList[position]
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(dataList[position])
    }

    fun setData(list: List<View>) {
        dataList = list
        notifyDataSetChanged()
    }
}