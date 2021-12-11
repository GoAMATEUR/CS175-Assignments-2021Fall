package me.hsy.mycanvas

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import me.hsy.mycanvas.ui.main.SectionsPagerAdapter
import me.hsy.mycanvas.databinding.ActivityCoursePageBinding
import me.hsy.mycanvas.ui.home.beans.Course

class CoursePage : AppCompatActivity() {

    private lateinit var binding: ActivityCoursePageBinding
    private var courseName: String? = null
    private var courseIntValue: Int = 2
    private var themeColor: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        courseIntValue = intent.getIntExtra("course_int", 2)
        themeColor = intent.getStringExtra("theme")
        Log.d("@=>", "${courseName}, $themeColor")

        courseName = Course.from(courseIntValue).courseName

        binding = ActivityCoursePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.title.text = courseName
        binding.appBar.setBackgroundColor(Color.parseColor(themeColor))
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager, courseIntValue)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)





//        val fab: FloatingActionButton = binding.fab

//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
    }
}