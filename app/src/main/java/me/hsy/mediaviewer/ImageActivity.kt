package me.hsy.mediaviewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import me.hsy.mediaviewer.ui.ViewAdapter
import java.util.ArrayList
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class ImageActivity : AppCompatActivity() {
    private var netImageList: List<String> = arrayListOf("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.soogif.com%2FC3aqdPxHURcuAnGw5LK6KV4sRMg257cN.gif&refer=http%3A%2F%2Fimg.soogif.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1640782986&t=a1571a89139ae97c593322aca11921dd",
        "https://img2.baidu.com/it/u=2004232198,1038007968&fm=26&fmt=auto",
        "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fp0.itc.cn%2Fq_70%2Fimages03%2F20200830%2Fc79d295e17814db08dfa12192fd8442c.jpeg&refer=http%3A%2F%2Fp0.itc.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1640782681&t=353ae18d05cb0e6451eb0cd89559fbb7",
        "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fk.sinaimg.cn%2Fn%2Fsports%2F2_img%2Fupload%2F130c9a33%2F798%2Fw1150h2048%2F20181128%2Fm8ZR-hphsupx4757960.jpg%2Fw640slw.jpg&refer=http%3A%2F%2Fk.sinaimg.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1640782681&t=d277b9f70eb6a6fc756e85aca1a6c7b0",
        "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fmedia0.giphy.com%2Fmedia%2F342Zsv5S4W8XC%2Fsource.gif&refer=http%3A%2F%2Fmedia0.giphy.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1640782992&t=12e6886fd5fb47f68eadfc084fd86df4") // list of image url
    private val pages: MutableList<View> = ArrayList() // list of pages

    private var viewPager: ViewPager? = null
    private var fullScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        title = "Image Viewer"
        for (i in netImageList.indices) {
            addImage(netImageList[i])
        }

        val viewAdapter = ViewAdapter()
        viewAdapter.setData(pages)
        viewPager = findViewById<ViewPager>(R.id.view_pager)
        viewPager!!.adapter = viewAdapter
//        val page = findViewById<LinearLayout>(R.id.image_page)
//        page.setOnClickListener{
//            Log.d("@=>", "Click")
//            fullScreen = if (!fullScreen) {
//                setTheme(R.style.NoActivityFullscreen)
//                true
//            } else {
//                setTheme(R.style.NotFullscreen)
//                false
//            }
//        }

    }

    private fun addImage(imageURL: String) {
        val imageHolder: ImageView = layoutInflater.inflate(R.layout.image_template, null).findViewById<ImageView>(R.id.image_holder)
        Glide.with(this)
            .load(imageURL)
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)) // Cache On
            .error(R.drawable.error)
            .into(imageHolder)

        pages.add(imageHolder)
    }
}